package mcjty.rftoolsdim.setup;

import mcjty.lib.network.PacketHandler;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.network.DimensionUpdatePacket;
import mcjty.rftoolsdim.modules.dimlets.network.PacketRequestDimlets;
import mcjty.rftoolsdim.modules.dimlets.network.PacketSendDimletsToClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RFToolsDimMessages {
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
        net.registerMessage(id(), PacketRequestDimlets.class, PacketRequestDimlets::toBytes, PacketRequestDimlets::new, PacketRequestDimlets::handle);
        net.registerMessage(id(), PacketSendDimletsToClient.class, PacketSendDimletsToClient::toBytes, PacketSendDimletsToClient::new, PacketSendDimletsToClient::handle);

        PacketHandler.registerStandardMessages(id(), net);
    }
}
