package mcjty.rftoolsdim.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EmptyDimensionTab extends GenericRFToolsItem {

    public EmptyDimensionTab() {
        super("empty_dimension_tab");
        setMaxStackSize(16);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        list.add(TextFormatting.YELLOW + "Put this empty dimension tab in a 'Dimension Enscriber'");
        list.add(TextFormatting.YELLOW + "where you can construct a dimension using dimlets");
    }
}
