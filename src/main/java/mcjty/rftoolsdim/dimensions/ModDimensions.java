package mcjty.rftoolsdim.dimensions;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.world.GenericWorldGenerator;
import mcjty.rftoolsdim.dimensions.world.GenericWorldProvider;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModDimensions {

    public static DimensionType rftoolsType;

    public static void init() {
        int id = GeneralConfiguration.rftoolsProviderId;
        if (id == -1) {
            for (DimensionType type : DimensionType.values()) {
                if (type.getId() > id) {
                    id = type.getId();
                }
            }
            id++;
        }
        Logging.log("Registering rftools dimension type at id " + id);
        rftoolsType = DimensionType.register("rftools_dimension", "_rftools", id, GenericWorldProvider.class, false);

        GameRegistry.registerWorldGenerator(new GenericWorldGenerator(), 1000);
    }

    public static void initDimensions() {
        WorldServer world = DimensionManager.getWorld(0);
        if (world != null) {
            RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
            if (dimensionManager != null) {
                dimensionManager.registerDimensions();
            }
        }
    }
}
