package mcjty.rftoolsdim.setup;

import mcjty.lib.network.PacketHandler;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.network.PackagePropageDataToClients;
import mcjty.rftoolsdim.dimension.network.PacketDimensionUpdate;
import mcjty.rftoolsdim.modules.dimlets.network.PacketSendDimletPackages;
import mcjty.rftoolsdim.modules.workbench.network.PacketPatternToClient;
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

        net.registerMessage(id(), PacketDimensionUpdate.class, PacketDimensionUpdate::toBytes, PacketDimensionUpdate::new, PacketDimensionUpdate::handle);
        net.registerMessage(id(), PacketSendDimletPackages.class, PacketSendDimletPackages::toBytes, PacketSendDimletPackages::new, PacketSendDimletPackages::handle);
        net.registerMessage(id(), PacketPatternToClient.class, PacketPatternToClient::toBytes, PacketPatternToClient::new, PacketPatternToClient::handle);
        net.registerMessage(id(), PackagePropageDataToClients.class, PackagePropageDataToClients::toBytes, PackagePropageDataToClients::new, PackagePropageDataToClients::handle);

        PacketHandler.registerStandardMessages(id(), net);
    }
}
