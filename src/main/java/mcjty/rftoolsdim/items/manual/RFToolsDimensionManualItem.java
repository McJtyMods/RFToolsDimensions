package mcjty.rftoolsdim.items.manual;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RFToolsDimensionManualItem extends GenericRFToolsItem {

    public RFToolsDimensionManualItem() {
        super("rftoolsdim_manual");
        setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            player.openGui(RFToolsDim.instance, GuiProxy.GUI_MANUAL_DIMENSION, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
