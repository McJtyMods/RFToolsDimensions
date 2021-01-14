package mcjty.rftoolsdim.dimension.features;

import mcjty.rftoolsdim.dimension.features.instances.CubeFeature;
import mcjty.rftoolsdim.dimension.features.instances.SphereFeature;

import java.util.HashMap;
import java.util.Map;

public enum FeatureType {
    NONE("none", null),
    CUBES("cubes", new CubeFeature()),
    SPHERES("spheres", new SphereFeature());

    private final String name;
    private final IFeature feature;

    private static final Map<String, FeatureType> FEATURE_BY_NAME = new HashMap<>();

    static {
        for (FeatureType type : values()) {
            FEATURE_BY_NAME.put(type.getName(), type);
        }
    }

    FeatureType(String name, IFeature feature) {
        this.name = name;
        this.feature = feature;
    }

    public String getName() {
        return name;
    }

    public IFeature getFeature() {
        return feature;
    }

    public static FeatureType byName(String name) {
        return FEATURE_BY_NAME.get(name.toLowerCase());
    }
}
