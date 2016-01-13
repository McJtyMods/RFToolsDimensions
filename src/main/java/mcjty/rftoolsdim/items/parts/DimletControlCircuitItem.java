package mcjty.rftoolsdim.items.parts;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimletControlCircuitItem extends GenericRFToolsItem {

    public DimletControlCircuitItem() {
        super("dimlet_control_circuit");
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "Every dimlet needs one control circuit. You can get");
            list.add(EnumChatFormatting.WHITE + "this by deconstructing other dimlets in the Dimlet");
            list.add(EnumChatFormatting.WHITE + "Workbench. In that same workbench you can also use");
            list.add(EnumChatFormatting.WHITE + "this item to make new dimlets. Note that you need a");
            list.add(EnumChatFormatting.WHITE + "control circuit of the right rarity in order to make");
            list.add(EnumChatFormatting.WHITE + "a dimlet of that rarity.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0 ; i < 7 ; i++) {
            list.add(new ItemStack(ModItems.dimletControlCircuitItem, 1, i));
        }
    }
}
