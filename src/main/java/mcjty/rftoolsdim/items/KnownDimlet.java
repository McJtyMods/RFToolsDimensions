package mcjty.rftoolsdim.items;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
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

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return models[stack.getItemDamage()];
            }
        });
    }


    // @todo
//    @Override
//    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
//        if (world.isRemote) {
//            return stack;
//        }
//
//        DimletKey key = KnownDimletConfiguration.getDimletKey(stack, world);
//        DimletEntry entry = KnownDimletConfiguration.getEntry(key);
//        if (entry != null) {
//            if (isSeedDimlet(entry)) {
//                NBTTagCompound tagCompound = stack.getTagCompound();
//                if (tagCompound == null) {
//                    tagCompound = new NBTTagCompound();
//                }
//
//                boolean locked = tagCompound.getBoolean("locked");
//                if (locked) {
//                    Logging.message(player, EnumChatFormatting.YELLOW + "This seed dimlet is locked. You cannot modify it!");
//                    return stack;
//                }
//
//                long forcedSeed = tagCompound.getLong("forcedSeed");
//                if (player.isSneaking()) {
//                    if (forcedSeed == 0) {
//                        Logging.message(player, EnumChatFormatting.YELLOW + "This dimlet has no seed. You cannot lock it!");
//                        return stack;
//                    }
//                    tagCompound.setBoolean("locked", true);
//                    Logging.message(player, "Dimlet locked!");
//                } else {
//                    long seed = world.getSeed();
//                    tagCompound.setLong("forcedSeed", seed);
//                    Logging.message(player, "Seed set to: " + seed);
//                }
//
//                stack.setTagCompound(tagCompound);
//            }
//        }
//
//        return stack;
//    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        DimletKey key = KnownDimletConfiguration.getDimletKey(itemStack);
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if (settings == null) {
            list.add(EnumChatFormatting.WHITE + "Dimlet " + key.getType().dimletType.getName() + "." + key.getId());
            list.add(EnumChatFormatting.RED + "This dimlet is blacklisted!");
            return;
        }

        list.add(EnumChatFormatting.BLUE + "Rarity: " + settings.getRarity() + (KnownDimletConfiguration.isCraftable(key) ? " (craftable)" : ""));
        list.add(EnumChatFormatting.YELLOW + "Create cost: " + settings.getCreateCost() + " RF/tick");
        int maintainCost = settings.getMaintainCost();
        if (maintainCost < 0) {
            list.add(EnumChatFormatting.YELLOW + "Maintain cost: " + maintainCost + "% RF/tick");
        } else {
            list.add(EnumChatFormatting.YELLOW + "Maintain cost: " + maintainCost + " RF/tick");
        }
        list.add(EnumChatFormatting.YELLOW + "Tick cost: " + settings.getTickCost() + " ticks");

        if (KnownDimletConfiguration.isSeedDimlet(key)) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null && tagCompound.getLong("forcedSeed") != 0) {
                long forcedSeed = tagCompound.getLong("forcedSeed");
                boolean locked = tagCompound.getBoolean("locked");
                list.add(EnumChatFormatting.BLUE + "Forced seed: " + forcedSeed + (locked ? " [LOCKED]" : ""));
            } else {
                list.add(EnumChatFormatting.BLUE + "Right click to copy seed from dimension.");
                list.add(EnumChatFormatting.BLUE + "Shift-Right click to lock copied seed.");
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            for (String info : key.getType().dimletType.getInformation()) {
                list.add(EnumChatFormatting.WHITE + info);
            }
            // @todo
//            List<String> extra = KnownDimletConfiguration.idToExtraInformation.get(entry.getKey());
//            if (extra != null) {
//                for (String info : extra) {
//                    list.add(EnumChatFormatting.YELLOW + info);
//                }
//            }
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
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
    public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        Set<DimletKey> dimlets = KnownDimletConfiguration.getCraftableDimlets();
        for (DimletKey key : dimlets) {
            list.add(KnownDimletConfiguration.getDimletStack(key));
        }
//
//        int meta = 0;
//        for (DimletType type : DimletType.values()) {
//            list.add(new ItemStack(this, 1, meta));
//            meta++;
//        }
    }

}
