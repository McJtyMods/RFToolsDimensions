package mcjty.rftoolsdim.network;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.thirteen.ChannelBuilder;
import mcjty.lib.thirteen.SimpleChannel;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class RFToolsDimMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = ChannelBuilder
                .named(new ResourceLocation(RFToolsDim.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net.getNetwork();

        // Server side
        net.registerMessageServer(id(), PacketGetDimensionEnergy.class, PacketGetDimensionEnergy::toBytes, PacketGetDimensionEnergy::new, PacketGetDimensionEnergy::handle);

        // Client side
        net.registerMessageClient(id(), PacketRegisterDimensions.class, PacketRegisterDimensions::toBytes, PacketRegisterDimensions::new, PacketRegisterDimensions::handle);
        net.registerMessageClient(id(), PacketReturnEnergy.class, PacketReturnEnergy::toBytes, PacketReturnEnergy::new, PacketReturnEnergy::handle);
        net.registerMessageClient(id(), PacketSyncDimensionInfo.class, PacketSyncDimensionInfo::toBytes, PacketSyncDimensionInfo::new, PacketSyncDimensionInfo::handle);
        net.registerMessageClient(id(), PacketSyncRules.class, PacketSyncRules::toBytes, PacketSyncRules::new, PacketSyncRules::handle);
    }

    private static int id() {
        return PacketHandler.nextPacketID();
    }
}
