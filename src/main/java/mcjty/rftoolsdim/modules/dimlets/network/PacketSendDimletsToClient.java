package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

// Server will use this packet to send back dimlets to the client
public class PacketSendDimletsToClient {

    private Set<DimletKey> dimlets;

    public PacketSendDimletsToClient(Set<DimletKey> dimlets) {
        this.dimlets = new HashSet<>(dimlets);
    }

    public PacketSendDimletsToClient(PacketBuffer buf) {
        int size = buf.readInt();
        dimlets = new HashSet<>(size);
        for (int i = 0 ; i < size ; i++) {
            short idx = buf.readShort();
            DimletType type = DimletType.values()[idx];
            String key = buf.readString(32767);
            dimlets.add(new DimletKey(type, key));
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(dimlets.size());
        for (DimletKey key : dimlets) {
            buf.writeShort(key.getType().ordinal());
            buf.writeString(key.getKey());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            DimletClientHelper.setDimletsOnGui(dimlets);
        });
    }
}
