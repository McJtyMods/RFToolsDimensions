package mcjty.rftoolsdim.apiimpl;

import mcjty.rftoolsdim.api.dimension.IDimensionInformation;
import mcjty.rftoolsdim.api.dimension.IDimensionManager;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.world.World;

public class DimensionManager implements IDimensionManager {

    @Override
    public int getCurrentRF(World world, int id) {
        if (!isRFToolsDimension(world, id)) {
            return -1;
        }
        DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
        return storage.getEnergyLevel(id);
    }

    @Override
    public boolean isRFToolsDimension(World world, int id) {
        RfToolsDimensionManager dimensionManager = world.isRemote ? RfToolsDimensionManager.getDimensionManagerClient() : RfToolsDimensionManager.getDimensionManager(world);
        return dimensionManager.getDimensionInformation(id) != null;
    }

    @Override
    public IDimensionInformation getDimensionInformation(World world, int id) {
        RfToolsDimensionManager dimensionManager = world.isRemote ? RfToolsDimensionManager.getDimensionManagerClient() : RfToolsDimensionManager.getDimensionManager(world);
        return dimensionManager.getDimensionInformation(id);
    }
}
