package mcjty.rftoolsdim.modules.dimensionbuilder.client;

import mcjty.lib.McJtyLib;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.power.ClientPowerManager;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.DimensionMonitorItem;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.PhasedFieldGenerator;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ClientHelpers {

    public static String getDimensionName(ItemStack stack) {
        World world = McJtyLib.proxy.getClientWorld();
        if (world == null) {
            return "";
        }
        return world.getDimensionKey().getLocation().getPath();
    }

    public static String getPowerString(ItemStack s) {
        World world = McJtyLib.proxy.getClientWorld();
        if (world == null) {
            return "";
        }
        long power = ClientPowerManager.get().getPower(world.getDimensionKey().getLocation());
        return power == -1 ? "<n.a.>" : ""+power;
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

    public static void initOverrides(PhasedFieldGenerator item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation(RFToolsDim.MODID, "power"), (stack, world, livingEntity) -> {
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
