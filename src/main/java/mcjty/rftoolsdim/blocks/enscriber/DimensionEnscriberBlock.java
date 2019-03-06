package mcjty.rftoolsdim.blocks.enscriber;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import mcjty.rftoolsdim.gui.GuiProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.function.BiFunction;

public class DimensionEnscriberBlock extends GenericRFToolsBlock<DimensionEnscriberTileEntity, DimensionEnscriberContainer> {

    public DimensionEnscriberBlock() {
        super(Material.IRON, DimensionEnscriberTileEntity.class, DimensionEnscriberContainer::new, "dimension_enscriber", true);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_DIMENSION_ENSCRIBER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BiFunction<DimensionEnscriberTileEntity, DimensionEnscriberContainer, GenericGuiContainer<? super DimensionEnscriberTileEntity>> getGuiFactory() {
        return GuiDimensionEnscriber::new;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "With this device you can construct your dimensions");
            list.add(TextFormatting.WHITE + "by combining specific dimlets into an empty dimension.");
            list.add(TextFormatting.WHITE + "tab. You can also deconstruct dimension tabs to get the");
            list.add(TextFormatting.WHITE + "original dimlets back.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }
}
