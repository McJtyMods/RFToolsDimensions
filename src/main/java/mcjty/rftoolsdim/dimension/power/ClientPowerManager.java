package mcjty.rftoolsdim.dimension.power;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ClientPowerManager {

    private static final ClientPowerManager INSTANCE = new ClientPowerManager();

    private final Map<ResourceLocation, Long> powerMap = new HashMap<>();

    public static ClientPowerManager get() {
        return INSTANCE;
    }

    public long getPower(ResourceLocation id) {
        return powerMap.getOrDefault(id, -1L);
    }

    public void updatePowerFromServer(Map<ResourceLocation, Long> powerMap) {
        this.powerMap.clear();
        this.powerMap.putAll(powerMap);
    }
}
