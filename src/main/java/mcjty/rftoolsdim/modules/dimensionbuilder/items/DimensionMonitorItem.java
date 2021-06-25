package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.ClientHelpers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

import net.minecraft.item.Item.Properties;

public class DimensionMonitorItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"), TooltipBuilder.parameter("power", ClientHelpers::getPowerString))
            .infoShift(header(),
                    TooltipBuilder.parameter("power", ClientHelpers::getPowerString),
                    TooltipBuilder.parameter("name", ClientHelpers::getDimensionName));

    public DimensionMonitorItem() {
        super(new Properties().tab(RFToolsDim.setup.getTab()).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.get().makeTooltip(getRegistryName(), stack, list, flagIn);
    }


}
