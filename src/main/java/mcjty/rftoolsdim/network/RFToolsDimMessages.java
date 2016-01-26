package mcjty.rftoolsdim.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RFToolsDimMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetDimensionEnergy.Handler.class, PacketGetDimensionEnergy.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketRegisterDimensions.Handler.class, PacketRegisterDimensions.class, PacketHandler.nextID(), Side.SERVER);
        net.registerMessage(PacketReturnEnergy.Handler.class, PacketReturnEnergy.class, PacketHandler.nextID(), Side.CLIENT);
        net.registerMessage(PacketSyncDimensionInfo.Handler.class, PacketSyncDimensionInfo.class, PacketHandler.nextID(), Side.CLIENT);
        net.registerMessage(PacketSyncRules.Handler.class, PacketSyncRules.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
