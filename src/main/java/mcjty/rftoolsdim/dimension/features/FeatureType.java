package mcjty.rftoolsdim.dimension.features;

import java.util.HashMap;
import java.util.Map;

public enum FeatureType {
    NONE("none"),
    SPHERES("spheres");

    private final String name;

    private static final Map<String, FeatureType> FEATURE_BY_NAME = new HashMap<>();

    static {
        for (FeatureType type : values()) {
            FEATURE_BY_NAME.put(type.getName(), type);
        }
    }

    FeatureType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FeatureType byName(String name) {
        return FEATURE_BY_NAME.get(name);
    }
}
