package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.api.Infusable;
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

public class DimletWorkbenchBlock extends GenericRFToolsBlock<DimletWorkbenchTileEntity, DimletWorkbenchContainer> implements Infusable {

    public DimletWorkbenchBlock() {
        super(Material.IRON, DimletWorkbenchTileEntity.class, DimletWorkbenchContainer::new, "dimlet_workbench", true);
        setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BiFunction<DimletWorkbenchTileEntity, DimletWorkbenchContainer, GenericGuiContainer<? super DimletWorkbenchTileEntity>> getGuiFactory() {
        return GuiDimletWorkbench::new;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "With this workbench you can deconstruct dimlets");
            list.add(TextFormatting.WHITE + "into individual parts and also reconstruct new dimlets");
            list.add(TextFormatting.WHITE + "out of these parts.");
            list.add(TextFormatting.YELLOW + "Infusing bonus: increased chance of getting");
            list.add(TextFormatting.YELLOW + "all parts out of the deconstructed dimlet.");
        } else {
            list.add(TextFormatting.WHITE + GuiProxy.SHIFT_MESSAGE);
        }
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_DIMLET_WORKBENCH;
    }
}
