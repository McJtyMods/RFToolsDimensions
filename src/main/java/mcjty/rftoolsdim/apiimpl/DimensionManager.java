package mcjty.rftoolsdim.apiimpl;

import mcjty.rftoolsdim.api.dimension.IDimensionInformation;
import mcjty.rftoolsdim.api.dimension.IDimensionManager;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class DimensionManager implements IDimensionManager {

    @Override
    public int getCurrentRF(int id) {
        if (!isRFToolsDimension(id)) {
            return -1;
        }
        World world = MinecraftServer.getServer().getEntityWorld();
        DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
        return storage.getEnergyLevel(id);
    }

    @Override
    public boolean isRFToolsDimension(int id) {
        World world = MinecraftServer.getServer().getEntityWorld();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        return dimensionManager.getDimensionInformation(id) != null;
    }

    @Override
    public IDimensionInformation getDimensionInformation(int id) {
        World world = MinecraftServer.getServer().getEntityWorld();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        return dimensionManager.getDimensionInformation(id);
    }
}
