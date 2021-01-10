package mcjty.rftoolsdim.setup;

import mcjty.lib.network.PacketHandler;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionUpdatePacket;
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

        net.registerMessage(id(), DimensionUpdatePacket.class, DimensionUpdatePacket::toBytes, DimensionUpdatePacket::new, DimensionUpdatePacket::handle);

        PacketHandler.registerStandardMessages(id(), net);
    }
}
