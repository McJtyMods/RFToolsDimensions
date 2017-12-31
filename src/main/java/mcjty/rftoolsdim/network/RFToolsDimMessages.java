package mcjty.rftoolsdim.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RFToolsDimMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetDimensionEnergy.Handler.class, PacketGetDimensionEnergy.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketRegisterDimensions.Handler.class, PacketRegisterDimensions.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketReturnEnergy.Handler.class, PacketReturnEnergy.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketSyncDimensionInfo.Handler.class, PacketSyncDimensionInfo.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketSyncRules.Handler.class, PacketSyncRules.class, PacketHandler.nextPacketID(), Side.CLIENT);
    }
}
