package mcjty.rftoolsdim.dimension.data;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ClientDimensionData {

    private static final ClientDimensionData INSTANCE = new ClientDimensionData();

    private Map<ResourceLocation, Power> powerMap = new HashMap<>();
    private long worldSeed = -1;

    public static ClientDimensionData get() {
        return INSTANCE;
    }

    public long getPower(ResourceLocation id) {
        return powerMap.getOrDefault(id, Power.NONE).power;
    }

    public long getMaxPower(ResourceLocation id) {
        return powerMap.getOrDefault(id, Power.NONE).max;
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public void updateDataFromServer(Map<ResourceLocation, Power> powerMap, long seed) {
        this.powerMap = powerMap;
        this.worldSeed = seed;
    }

    public void clear() {
        worldSeed = -1;
        powerMap.clear();
    }

    public static class Power {
        private final long power;
        private final long max;

        public final static Power NONE = new Power(-1, -1);

        public Power(long power, long max) {
            this.power = power;
            this.max = max;
        }

        public long getPower() {
            return power;
        }

        public long getMax() {
            return max;
        }
    }
}
