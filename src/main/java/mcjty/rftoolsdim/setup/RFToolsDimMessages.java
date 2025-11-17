package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.network.PackagePropageDataToClients;
import mcjty.rftoolsdim.dimension.tools.PacketSyncDimensionListChanges;
import mcjty.rftoolsdim.modules.dimlets.network.PacketSendDimletPackages;
import mcjty.rftoolsdim.modules.workbench.network.PacketPatternToClient;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class RFToolsDimMessages {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(RFToolsDim.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToClient(PacketSendDimletPackages.TYPE, PacketSendDimletPackages.CODEC, PacketSendDimletPackages::handle);
        registrar.playToClient(PacketPatternToClient.TYPE, PacketPatternToClient.CODEC, PacketPatternToClient::handle);
        registrar.playToClient(PackagePropageDataToClients.TYPE, PackagePropageDataToClients.CODEC, PackagePropageDataToClients::handle);
        registrar.playToClient(PacketSyncDimensionListChanges.TYPE, PacketSyncDimensionListChanges.CODEC, PacketSyncDimensionListChanges::handle);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToAll(T packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}
