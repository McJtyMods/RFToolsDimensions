package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EmptyDimensionTab extends Item {

    public EmptyDimensionTab() {
        super(new Item.Properties().tab(RFToolsDim.setup.getTab()).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        // @todo 1.16 better tooltips
        list.add(new StringTextComponent(TextFormatting.YELLOW + "Put this empty dimension tab in a 'Dimension Enscriber'"));
        list.add(new StringTextComponent(TextFormatting.YELLOW + "where you can construct a dimension using dimlets"));
    }
}
