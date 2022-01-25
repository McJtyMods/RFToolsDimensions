package mcjty.rftoolsdim.dimension.data;

import mcjty.rftoolsdim.dimension.additional.SkyType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ClientDimensionData {

    private static final ClientDimensionData INSTANCE = new ClientDimensionData();

    private Map<ResourceLocation, ClientData> clientDataMap = new HashMap<>();
    private long worldSeed = -1;

    public static ClientDimensionData get() {
        return INSTANCE;
    }

    public long getPower(ResourceLocation id) {
        return clientDataMap.getOrDefault(id, ClientData.NONE).power;
    }

    public long getMaxPower(ResourceLocation id) {
        return clientDataMap.getOrDefault(id, ClientData.NONE).max;
    }

    @Nonnull
    public ClientData getClientData(ResourceLocation id) {
        return clientDataMap.getOrDefault(id, ClientData.NONE);
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public void updateDataFromServer(Map<ResourceLocation, ClientData> clientDataMap, long seed) {
        this.clientDataMap = clientDataMap;
        this.worldSeed = seed;
    }

    public void clear() {
        worldSeed = -1;
        clientDataMap.clear();
    }

    public record ClientData(long power, long max, SkyType skyType) {
        public static final ClientData NONE = new ClientData(-1, -1, SkyType.NORMAL);

        public static ClientData create(FriendlyByteBuf buf) {
            long power = buf.readLong();
            long max = buf.readLong();
            SkyType type = SkyType.values()[buf.readShort()];
            return new ClientData(power, max, type);
        }

        public void writeToBuf(FriendlyByteBuf buf) {
            buf.writeLong(power);
            buf.writeLong(max);
            buf.writeShort(skyType.ordinal());
        }
    }
}
