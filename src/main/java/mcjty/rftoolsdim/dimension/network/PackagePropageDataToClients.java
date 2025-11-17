package mcjty.rftoolsdim.dimension.network;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record PackagePropageDataToClients(Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap, long seed) implements CustomPacketPayload  {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "propagate_data_to_clients");
    public static final CustomPacketPayload.Type<PackagePropageDataToClients> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PackagePropageDataToClients> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.clientDataMap.size());
                for (var entry : packet.clientDataMap.entrySet()) {
                    buf.writeResourceLocation(entry.getKey());
                    entry.getValue().writeToBuf(buf);
                }
                buf.writeLong(packet.seed);
            },
            buf -> {
                int size = buf.readInt();
                Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap = new HashMap<>(size);
                for (int i = 0; i < size; i++) {
                    ResourceLocation id = buf.readResourceLocation();
                    clientDataMap.put(id, ClientDimensionData.ClientData.create(buf));
                }
                long seed = buf.readLong();
                return new PackagePropageDataToClients(clientDataMap, seed);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientDimensionData.get().updateDataFromServer(clientDataMap, seed));
    }

}
