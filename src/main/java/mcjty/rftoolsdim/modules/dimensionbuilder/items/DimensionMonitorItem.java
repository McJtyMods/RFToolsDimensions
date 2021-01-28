package mcjty.rftoolsdim.modules.dimensionbuilder.items;

import mcjty.lib.McJtyLib;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.power.ClientPowerManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class DimensionMonitorItem extends Item implements ITooltipSettings {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsdim.shiftmessage"), TooltipBuilder.parameter("power", this::getPowerString))
            .infoShift(header(),
                    TooltipBuilder.parameter("power", this::getPowerString),
                    TooltipBuilder.parameter("name", this::getDimensionName));

    private String getDimensionName(ItemStack stack) {
        World world = McJtyLib.proxy.getClientWorld();
        if (world == null) {
            return "";
        }
        return world.getDimensionKey().getLocation().getPath();
    }

    private String getPowerString(ItemStack s) {
        World world = McJtyLib.proxy.getClientWorld();
        if (world == null) {
            return "";
        }
        long power = ClientPowerManager.get().getPower(world.getDimensionKey().getLocation());
        return power == -1 ? "<n.a.>" : ""+power;
    }

    public DimensionMonitorItem() {
        super(new Properties().group(RFToolsDim.setup.getTab()).maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);
    }

    public static void initOverrides(DimensionMonitorItem item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation(RFToolsDim.MODID, "power"), (stack, world, livingEntity) -> {
            World w = world;
            if (w == null) {
                if (livingEntity == null) {
                    return 0;
                }
                w = livingEntity.getEntityWorld();
            }
            long power = ClientPowerManager.get().getPower(w.getDimensionKey().getLocation());
            long max = DimensionConfig.MAX_DIMENSION_POWER.get();
            long level = (9 * power) / max;
            if (level < 0) {
                level = 0;
            } else if (level > 8) {
                level = 9;
            }
            return 8-level;
        });
    }


}
