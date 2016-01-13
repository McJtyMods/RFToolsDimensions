package mcjty.rftoolsdim.items.parts;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.items.GenericRFToolsItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EfficiencyEssenceItem extends GenericRFToolsItem {
    public EfficiencyEssenceItem() {
        super("efficiency_essence");
        setMaxStackSize(64);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This essence item is the main ingredient for");
            list.add(EnumChatFormatting.WHITE + "the efficiency dimlet in the Dimlet Workbench.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }
}
