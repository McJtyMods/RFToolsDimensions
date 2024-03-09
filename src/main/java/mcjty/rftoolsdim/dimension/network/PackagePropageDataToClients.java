package mcjty.rftoolsdim.dimension.network;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record PackagePropageDataToClients(Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap, long seed) implements CustomPacketPayload  {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsDim.MODID, "propagate_data_to_clients");

    public static PackagePropageDataToClients create(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            ResourceLocation id = buf.readResourceLocation();
            clientDataMap.put(id, ClientDimensionData.ClientData.create(buf));
        }
        long seed = buf.readLong();
        return new PackagePropageDataToClients(clientDataMap, seed);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(clientDataMap.size());
        for (var entry : clientDataMap.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            entry.getValue().writeToBuf(buf);
        }
        buf.writeLong(seed);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> ClientDimensionData.get().updateDataFromServer(clientDataMap, seed));
    }

}