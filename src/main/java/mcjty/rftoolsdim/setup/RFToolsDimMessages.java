package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.network.PackagePropageDataToClients;
import mcjty.rftoolsdim.dimension.tools.PacketSyncDimensionListChanges;
import mcjty.rftoolsdim.modules.dimlets.network.PacketSendDimletPackages;
import mcjty.rftoolsdim.modules.workbench.network.PacketPatternToClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static mcjty.lib.network.PlayPayloadContext.wrap;

public class RFToolsDimMessages {
    private static SimpleChannel INSTANCE;

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

        net.registerMessage(id(), PacketSendDimletPackages.class, PacketSendDimletPackages::write, PacketSendDimletPackages::create, wrap(PacketSendDimletPackages::handle));        net.registerMessage(id(), PacketPatternToClient.class, PacketPatternToClient::toBytes, PacketPatternToClient::new, PacketPatternToClient::handle);
        net.registerMessage(id(), PackagePropageDataToClients.class, PackagePropageDataToClients::write, PackagePropageDataToClients::create, wrap(PackagePropageDataToClients::handle));
        net.registerMessage(id(), PacketSyncDimensionListChanges.class, PacketSyncDimensionListChanges::write, PacketSyncDimensionListChanges::create, wrap(PacketSyncDimensionListChanges::handle));
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        INSTANCE.sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToAll(T packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static <T> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
