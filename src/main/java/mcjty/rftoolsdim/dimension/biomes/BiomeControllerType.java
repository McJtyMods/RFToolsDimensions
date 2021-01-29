package mcjty.rftoolsdim.dimension.biomes;

import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;

import java.util.HashMap;
import java.util.Map;

public enum BiomeControllerType {
    DEFAULT("default", KnowledgeSet.SET1),
    CHECKER("checker", KnowledgeSet.SET2),
    SINGLE("single", KnowledgeSet.SET3);

    private final String name;
    private final KnowledgeSet set;
    private static final Map<String, BiomeControllerType> FEATURE_BY_NAME = new HashMap<>();

    static {
        for (BiomeControllerType type : values()) {
            FEATURE_BY_NAME.put(type.getName(), type);
        }
    }

    BiomeControllerType(String name, KnowledgeSet set) {
        this.name = name;
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public static BiomeControllerType byName(String name) {
        return FEATURE_BY_NAME.get(name.toLowerCase());
    }
}
