package mcjty.rftoolsdim.blocks.enscriber;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimensionEnscriberBlock extends GenericRFToolsBlock<DimensionEnscriberTileEntity, DimensionEnscriberContainer> {

    public DimensionEnscriberBlock() {
        super(Material.IRON, DimensionEnscriberTileEntity.class, DimensionEnscriberContainer.class, "dimension_enscriber", true);
    }

    @Override
    public int getGuiID() {
        return RFToolsDim.GUI_DIMENSION_ENSCRIBER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiDimensionEnscriber.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "With this device you can construct your dimensions");
            list.add(TextFormatting.WHITE + "by combining specific dimlets into an empty dimension.");
            list.add(TextFormatting.WHITE + "tab. You can also deconstruct dimension tabs to get the");
            list.add(TextFormatting.WHITE + "original dimlets back.");
        } else {
            list.add(TextFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }
}
