package mcjty.rftoolsdim.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class RFToolsDimMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
//        net.registerMessage(PacketCrafter.Handler.class, PacketCrafter.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
//        net.registerMessage(PacketPlayersReady.Handler.class, PacketPlayersReady.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
