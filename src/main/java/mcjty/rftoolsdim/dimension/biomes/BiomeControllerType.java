package mcjty.rftoolsdim.dimension.biomes;

import java.util.HashMap;
import java.util.Map;

public enum BiomeControllerType {
    DEFAULT("default"),
    CHECKER("checker"),
    SINGLE("single");

    private final String name;
    private static final Map<String, BiomeControllerType> FEATURE_BY_NAME = new HashMap<>();

    static {
        for (BiomeControllerType type : values()) {
            FEATURE_BY_NAME.put(type.getName(), type);
        }
    }

    BiomeControllerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BiomeControllerType byName(String name) {
        return FEATURE_BY_NAME.get(name.toLowerCase());
    }
}
