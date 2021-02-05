package mcjty.rftoolsdim.dimension.data;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ClientDimensionData {

    private static final ClientDimensionData INSTANCE = new ClientDimensionData();

    private final Map<ResourceLocation, Long> powerMap = new HashMap<>();
    private long worldSeed = -1;

    public static ClientDimensionData get() {
        return INSTANCE;
    }

    public long getPower(ResourceLocation id) {
        return powerMap.getOrDefault(id, -1L);
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public void updateDataFromServer(Map<ResourceLocation, Long> powerMap, long seed) {
        this.powerMap.clear();
        this.powerMap.putAll(powerMap);
        this.worldSeed = seed;
    }

    public void clear() {
        worldSeed = -1;
        powerMap.clear();
    }
}
