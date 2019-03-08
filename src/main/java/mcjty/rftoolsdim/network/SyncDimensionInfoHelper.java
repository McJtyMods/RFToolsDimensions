package mcjty.rftoolsdim.network;

import java.util.Map;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SyncDimensionInfoHelper {

    public static void syncDimensionManagerFromServer(Map<Integer, DimensionDescriptor> dimensions, Map<Integer, DimensionInformation> dimensionInformation) {
        Logging.log("Received dimension information from server");
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManagerClient();
        dimensionManager.syncFromServer(dimensions, dimensionInformation);
//        dimensionManager.save(world);
    }

}
