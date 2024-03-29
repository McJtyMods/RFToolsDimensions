package mcjty.rftoolsdim.modules.dimlets.network;

import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// Server will use this packet to send back dimlets to the client
public class PacketSendDimletPackages {

    private final Map<DimletKey, DimletSettings> dimlets;

    public PacketSendDimletPackages(Map<DimletKey, DimletSettings> dimlets) {
        this.dimlets = new HashMap<>(dimlets);
    }

    public PacketSendDimletPackages(PacketBuffer buf) {
        int size = buf.readInt();
        dimlets = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            DimletKey key = new DimletKey(buf);
            DimletSettings settings = new DimletSettings(buf);
            dimlets.put(key, settings);
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(dimlets.size());
        for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
            entry.getKey().toBytes(buf);
            entry.getValue().toBytes(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            DimletDictionary dictionary = DimletDictionary.get();
            for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
                dictionary.register(entry.getKey(), entry.getValue());
            }
        });
        ctx.setPacketHandled(true);
    }
}
