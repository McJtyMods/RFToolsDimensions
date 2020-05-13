package mcjty.rftoolsdim.setup;

import mcjty.lib.network.PacketHandler;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RFToolsDimMessage {
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RFToolsDim.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Server side
//        net.registerMessage(id(), PacketGetLog.class, PacketGetLog::toBytes, PacketGetLog::new, PacketGetLog::handle);

        // Client side
//        net.registerMessage(id(), PacketLogReady.class, PacketLogReady::toBytes, PacketLogReady::new, PacketLogReady::handle);

        PacketHandler.registerStandardMessages(id(), net);
    }
}
