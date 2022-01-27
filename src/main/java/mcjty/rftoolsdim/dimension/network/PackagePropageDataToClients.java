package mcjty.rftoolsdim.dimension.network;

import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PackagePropageDataToClients {

    private final Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap;
    private final long seed;

    public PackagePropageDataToClients(FriendlyByteBuf buf) {
        int size = buf.readInt();
        clientDataMap = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            ResourceLocation id = buf.readResourceLocation();
            clientDataMap.put(id, ClientDimensionData.ClientData.create(buf));
        }
        seed = buf.readLong();
    }

    public PackagePropageDataToClients(Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap, long seed) {
        this.clientDataMap = clientDataMap;
        this.seed = seed;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(clientDataMap.size());
        for (var entry : clientDataMap.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            entry.getValue().writeToBuf(buf);
        }
        buf.writeLong(seed);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> ClientDimensionData.get().updateDataFromServer(clientDataMap, seed));
        ctx.setPacketHandled(true);
    }

}