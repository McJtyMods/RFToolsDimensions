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

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class DimensionMonitorItem extends Item implements ITooltipSettings {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"), TooltipBuilder.parameter("power", ClientHelpers::getPowerString))
            .infoShift(header(),
                    TooltipBuilder.parameter("power", ClientHelpers::getPowerString),
                    TooltipBuilder.parameter("name", ClientHelpers::getDimensionName));

    public DimensionMonitorItem() {
        super(new Properties().group(RFToolsDim.setup.getTab()).maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);
    }


}
