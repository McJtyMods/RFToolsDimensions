package mcjty.rftoolsdim.dimension.features;

import mcjty.rftoolsdim.dimension.features.instances.CubeFeature;
import mcjty.rftoolsdim.dimension.features.instances.SphereFeature;
import mcjty.rftoolsdim.dimension.features.instances.TendrilsFeature;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;

import java.util.HashMap;
import java.util.Map;

public enum FeatureType {
    NONE("none", KnowledgeSet.SET1, null),
    CUBES("cubes", KnowledgeSet.SET2, new CubeFeature(false)),
    HOLLOW_CUBES("hollow_cubes", KnowledgeSet.SET2, new CubeFeature(true)),
    SPHERES("spheres", KnowledgeSet.SET3, new SphereFeature(false)),
    HOLLOW_SPHERES("hollow_spheres", KnowledgeSet.SET3, new SphereFeature(true)),
    TENDRILS("tendrils", KnowledgeSet.SET4, new TendrilsFeature());

    private final String name;
    private final KnowledgeSet set;
    private final IFeature feature;

    private static final Map<String, FeatureType> FEATURE_BY_NAME = new HashMap<>();

    static {
        for (FeatureType type : values()) {
            FEATURE_BY_NAME.put(type.getName(), type);
        }
    }

    FeatureType(String name, KnowledgeSet set, IFeature feature) {
        this.name = name;
        this.set = set;
        this.feature = feature;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public IFeature getFeature() {
        return feature;
    }

    public static FeatureType byName(String name) {
        return FEATURE_BY_NAME.get(name.toLowerCase());
    }
}
