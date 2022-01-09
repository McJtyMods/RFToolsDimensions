package mcjty.rftoolsdim.modules.dimensionbuilder.client;

import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.DimensionMonitorItem;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.PhasedFieldGenerator;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ClientHelpers {

    public static String getDimensionName(ItemStack stack) {
        Level world = SafeClientTools.getClientWorld();
        if (world == null) {
            return "";
        }
        return world.dimension().location().getPath();
    }

    public static String getPowerString(ItemStack s) {
        Level world = SafeClientTools.getClientWorld();
        if (world == null) {
            return "";
        }
        ResourceLocation id = world.dimension().location();
        long power = ClientDimensionData.get().getPower(id);
        long max = ClientDimensionData.get().getMaxPower(id);
        return power == -1 ? "<n.a.>" : power + " (" + max + ")";
    }

    public static void initOverrides(DimensionMonitorItem item) {
        ItemProperties.register(item, new ResourceLocation(RFToolsDim.MODID, "power"), (stack, world, livingEntity) -> {
            Level w = world;
            if (w == null) {
                if (livingEntity == null) {
                    return 0;
                }
                w = livingEntity.getCommandSenderWorld();
            }
            ResourceLocation id = w.dimension().location();
            long power = ClientDimensionData.get().getPower(id);
            long max = ClientDimensionData.get().getMaxPower(id);
            if (max < 0) {
                return 8;
            }
            long level = (9 * power) / max;
            if (level < 0) {
                level = 0;
            } else if (level > 8) {
                level = 9;
            }
            return 8-level;
        });
    }

    public static void initOverrides(PhasedFieldGenerator item) {
        ItemProperties.register(item, new ResourceLocation(RFToolsDim.MODID, "power"), (stack, world, livingEntity) -> {
            long power = 0;
            if (stack.hasTag()) {
                power = stack.getTag().getLong("Energy");
            }
            long max = DimensionBuilderConfig.PHASEDFIELD_MAXENERGY.get();
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
