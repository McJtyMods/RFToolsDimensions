package mcjty.rftoolsdim.blocks.painter;

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

public class EssencePainterBlock extends GenericRFToolsBlock<EssencePainterTileEntity, EssencePainterContainer> {

    public EssencePainterBlock() {
        super(Material.IRON, EssencePainterTileEntity.class, EssencePainterContainer.class, "essence_painter", true);
    }

    @Override
    public int getGuiID() {
        return RFToolsDim.GUI_ESSENCE_PAINTER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiEssencePainter.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Some of the more cosmetic essences which are");
            list.add(TextFormatting.WHITE + "needed to create dimlets can be colored with");
            list.add(TextFormatting.WHITE + "this painter");
        } else {
            list.add(TextFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }
}
