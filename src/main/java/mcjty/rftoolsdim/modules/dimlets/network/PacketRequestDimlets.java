package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.setup.RFToolsDimMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

// Client will send this packet to request dimlets from the server
public class PacketRequestDimlets {

    public PacketRequestDimlets() {
    }

    public PacketRequestDimlets(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Set<DimletKey> dimlets = DimletDictionary.get().getDimlets();
            RFToolsDimMessage.INSTANCE.sendTo(new PacketSendDimletsToClient(dimlets), ctx.getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
    }
}
