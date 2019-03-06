package mcjty.rftoolsdim.items;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.gui.GuiProxy;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Set;

public class KnownDimlet extends GenericRFToolsItem {

    public KnownDimlet() {
        super("known_dimlet");
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation models[] = new ModelResourceLocation[DimletType.values().length];
        int meta = 0;
        for (DimletType type : DimletType.values()) {
            models[meta] = new ModelResourceLocation(getRegistryName() + "_" + type.dimletType.getName().toLowerCase(), "inventory");
            ModelBakery.registerItemVariants(this, models[meta]);
            meta++;
        }

        ModelLoader.setCustomMeshDefinition(this, stack -> models[stack.getItemDamage()]);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
        if (KnownDimletConfiguration.isSeedDimlet(key)) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound == null) {
                tagCompound = new NBTTagCompound();
            }

            boolean locked = tagCompound.getBoolean("locked");
            if (locked) {
                Logging.message(player, TextFormatting.YELLOW + "This seed dimlet is locked. You cannot modify it!");
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }

            long forcedSeed = tagCompound.getLong("forcedSeed");
            if (player.isSneaking()) {
                if (forcedSeed == 0) {
                    Logging.message(player, TextFormatting.YELLOW + "This dimlet has no seed. You cannot lock it!");
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
                tagCompound.setBoolean("locked", true);
                Logging.message(player, "Dimlet locked!");
            } else {
                long seed = world.getSeed();
                tagCompound.setLong("forcedSeed", seed);
                Logging.message(player, "Seed set to: " + seed);
            }

            stack.setTagCompound(tagCompound);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag showExtended) {
        super.addInformation(itemStack, player, list, showExtended);
        DimletKey key = KnownDimletConfiguration.getDimletKey(itemStack);
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if (showExtended.isAdvanced()) {
            list.add(TextFormatting.GOLD + "Key: " + key.getId());
        }
        if (settings == null) {
            list.add(TextFormatting.WHITE + "Dimlet " + key.getType().dimletType.getName() + "." + key.getId());
            list.add(TextFormatting.RED + "This dimlet is blacklisted!");
            return;
        }

        list.add(TextFormatting.BLUE + "Rarity: " + settings.getRarity() + (KnownDimletConfiguration.isCraftable(key) ? " (craftable)" : ""));
        list.add(TextFormatting.YELLOW + "Create cost: " + settings.getCreateCost() + " RF/tick");
        int maintainCost = settings.getMaintainCost();
        if (maintainCost < 0) {
            list.add(TextFormatting.YELLOW + "Maintain cost: " + maintainCost + "% RF/tick");
        } else {
            list.add(TextFormatting.YELLOW + "Maintain cost: " + maintainCost + " RF/tick");
        }
        list.add(TextFormatting.YELLOW + "Tick cost: " + settings.getTickCost() + " ticks");

        if (KnownDimletConfiguration.isSeedDimlet(key)) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null && tagCompound.getLong("forcedSeed") != 0) {
                long forcedSeed = tagCompound.getLong("forcedSeed");
                boolean locked = tagCompound.getBoolean("locked");
                list.add(TextFormatting.BLUE + "Forced seed: " + forcedSeed + (locked ? " [LOCKED]" : ""));
            } else {
                list.add(TextFormatting.BLUE + "Right click to copy seed from dimension.");
                list.add(TextFormatting.BLUE + "Shift-Right click to lock copied seed.");
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            for (String info : key.getType().dimletType.getInformation()) {
                list.add(TextFormatting.WHITE + info);
            }
            // @todo
//            List<String> extra = KnownDimletConfiguration.idToExtraInformation.get(entry.getKey());
//            if (extra != null) {
//                for (String info : extra) {
//                    list.add(TextFormatting.YELLOW + info);
//                }
//            }
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        DimletType type = DimletType.values()[itemStack.getItemDamage()];
        return super.getUnlocalizedName(itemStack) + "_" + type.dimletType.getName().toLowerCase();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
        String displayName = super.getItemStackDisplayName(stack);
        if (key.getId() == null) {
            return displayName;
        } else {
            return displayName + " (" + KnownDimletConfiguration.getDisplayName(key) + ")";
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            Set<DimletKey> dimlets = KnownDimletConfiguration.getCraftableDimlets();
            for (DimletKey key : dimlets) {
                subItems.add(KnownDimletConfiguration.getDimletStack(key));
            }
        }
//
//        int meta = 0;
//        for (DimletType type : DimletType.values()) {
//            list.add(new ItemStack(this, 1, meta));
//            meta++;
//        }
    }

}
