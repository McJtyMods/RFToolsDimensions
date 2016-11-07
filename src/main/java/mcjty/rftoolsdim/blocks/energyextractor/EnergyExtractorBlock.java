package mcjty.rftoolsdim.blocks.energyextractor;

import mcjty.lib.container.EmptyContainer;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EnergyExtractorBlock extends GenericRFToolsBlock<EnergyExtractorTileEntity, EmptyContainer> {

    public EnergyExtractorBlock() {
        super(Material.IRON, EnergyExtractorTileEntity.class, EmptyContainer.class, "energy_extractor", false);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This device can be used to extract energy out of");
            list.add(TextFormatting.WHITE + "the current dimension. Be careful with this as");
            list.add(TextFormatting.WHITE + "the dimension needs that energy too!");
            list.add(TextFormatting.WHITE + "This device only works in RFTools dimensions.");
        } else {
            list.add(TextFormatting.WHITE + RFToolsDim.SHIFT_MESSAGE);
        }
    }
}
