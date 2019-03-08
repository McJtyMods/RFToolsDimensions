package mcjty.rftoolsdim.network;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.world.World;

public class ReturnEnergyHelper {
    public static void setEnergyLevel(PacketReturnEnergy message) {
        World world = RFToolsDim.proxy.getClientWorld();
        DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);
        dimensionStorage.setEnergyLevel(message.getId(), message.getEnergy());
    }

}
