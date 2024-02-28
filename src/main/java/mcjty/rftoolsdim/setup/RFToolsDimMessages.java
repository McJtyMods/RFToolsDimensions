package mcjty.rftoolsdim.setup;

import mcjty.lib.network.IPayloadRegistrar;
import mcjty.lib.network.Networking;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.network.PackagePropageDataToClients;
import mcjty.rftoolsdim.dimension.tools.PacketSyncDimensionListChanges;
import mcjty.rftoolsdim.modules.dimlets.network.PacketSendDimletPackages;
import mcjty.rftoolsdim.modules.workbench.network.PacketPatternToClient;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.PacketDistributor;

public class RFToolsDimMessages {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = Networking.registrar(RFToolsDim.MODID)
                .versioned("1.0")
                .optional();

        registrar.play(PacketSendDimletPackages.class, PacketSendDimletPackages::create, handler -> handler.server(PacketSendDimletPackages::handle));
        registrar.play(PacketPatternToClient.class, PacketPatternToClient::create, handler -> handler.client(PacketPatternToClient::handle));
        registrar.play(PackagePropageDataToClients.class, PackagePropageDataToClients::create, handler -> handler.client(PackagePropageDataToClients::handle));
        registrar.play(PacketSyncDimensionListChanges.class, PacketSyncDimensionListChanges::create, handler -> handler.server(PacketSyncDimensionListChanges::handle));
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        registrar.getChannel().sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToAll(T packet) {
        registrar.getChannel().send(PacketDistributor.ALL.noArg(), packet);
    }

    public static <T> void sendToServer(T packet) {
        registrar.getChannel().sendToServer(packet);
    }
}
