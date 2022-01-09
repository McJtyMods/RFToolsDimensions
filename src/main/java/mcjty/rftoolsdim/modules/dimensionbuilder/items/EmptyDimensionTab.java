package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EmptyDimensionTab extends Item {

    public EmptyDimensionTab() {
        super(new Item.Properties().tab(RFToolsDim.setup.getTab()).stacksTo(1));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        // @todo 1.16 better tooltips
        list.add(new TextComponent(ChatFormatting.YELLOW + "Put this empty dimension tab in a 'Dimension Enscriber'"));
        list.add(new TextComponent(ChatFormatting.YELLOW + "where you can construct a dimension using dimlets"));
    }
}
