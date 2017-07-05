package mcjty.rftoolsdim.items;

import mcjty.lib.varia.SoundTools;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class DimletParcelItem extends GenericRFToolsItem {
    public DimletParcelItem() {
        super("dimlet_parcel");
        setMaxStackSize(64);
    }

    private Random random = new Random();

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (hand != EnumHand.MAIN_HAND) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!world.isRemote) {
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            int amount = random.nextInt(GeneralConfiguration.maxParcelContents - GeneralConfiguration.minParcelContents + 1) + GeneralConfiguration.minParcelContents;
            if (amount > 0) {
                SoundTools.playSound(world, SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.player.levelup")), player.posX, player.posY, player.posZ, 1.0f, 1.0f);
                for (int i = 0 ; i < amount ; i++) {
                    ItemStack part = DimletRandomizer.getRandomPart(random);
                    if (!player.inventory.addItemStackToInventory(part.copy())) {
                        player.entityDropItem(part.copy(), 1.05f);
                    }
                }
            }
            player.openContainer.detectAndSendChanges();
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add(TextFormatting.GREEN + "A present for you! Use it well");
        list.add(TextFormatting.GREEN + "Right click to get the gifts");
    }

}
