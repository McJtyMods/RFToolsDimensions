package mcjty.rftoolsdim.blocks.enscriber;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimensionEnscriberBlock extends GenericRFToolsBlock {

    public DimensionEnscriberBlock() {
        super(Material.iron, DimensionEnscriberTileEntity.class, DimensionEnscriberContainer.class, "dimension_enscriber", true);
    }

    @Override
    public int getGuiID() {
        return RFToolsDim.GUI_DIMENSION_ENSCRIBER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "With this device you can construct your dimensions");
            list.add(EnumChatFormatting.WHITE + "by combining specific dimlets into an empty dimension.");
            list.add(EnumChatFormatting.WHITE + "tab. You can also deconstruct dimension tabs to get the");
            list.add(EnumChatFormatting.WHITE + "original dimlets back.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }
}
