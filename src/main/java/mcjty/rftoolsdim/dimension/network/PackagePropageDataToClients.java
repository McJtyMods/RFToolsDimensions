package mcjty.rftoolsdim.dimension.network;

import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PackagePropageDataToClients {

    private final Map<ResourceLocation, ClientDimensionData.Power> powerMap;
    private final long seed;

    public PackagePropageDataToClients(PacketBuffer buf) {
        int size = buf.readInt();
        powerMap = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            ResourceLocation id = buf.readResourceLocation();
            long power = buf.readLong();
            long max = buf.readLong();
            powerMap.put(id, new ClientDimensionData.Power(power, max));
        }
        seed = buf.readLong();
    }

    public PackagePropageDataToClients(Map<ResourceLocation, ClientDimensionData.Power> powerMap, long seed) {
        this.powerMap = powerMap;
        this.seed = seed;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(powerMap.size());
        for (Map.Entry<ResourceLocation, ClientDimensionData.Power> entry : powerMap.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeLong(entry.getValue().getPower());
            buf.writeLong(entry.getValue().getMax());
        }
        buf.writeLong(seed);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> ClientDimensionData.get().updateDataFromServer(powerMap, seed));
        ctx.setPacketHandled(true);
    }

}