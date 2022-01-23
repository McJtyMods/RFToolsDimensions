package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;

import java.util.HashMap;
import java.util.Map;

public enum AttributeType {
    DEFAULT("default", KnowledgeSet.SET1),
//    FLATTER("flatter", KnowledgeSet.SET1),
//    ELEVATED("elevated", KnowledgeSet.SET1),
//    NOBIOMESURFACE("nobiomesurface", KnowledgeSet.SET1),
    NOOCEANS("nooceans", KnowledgeSet.SET1),
    WATERWORLD("waterworld", KnowledgeSet.SET1),
    CITIES("cities", KnowledgeSet.SET1),
//    NOBEDROCK("nobedrock", KnowledgeSet.SET1),
    NOBLOBS("noblobs", KnowledgeSet.SET1);

    private final String name;
    private final KnowledgeSet set;

    private static final Map<String, AttributeType> ATTRIBUTE_BY_NAME = new HashMap<>();

    static {
        for (AttributeType type : values()) {
            ATTRIBUTE_BY_NAME.put(type.getName(), type);
        }
    }

    AttributeType(String name, KnowledgeSet set) {
        this.name = name;
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public static AttributeType byName(String name) {
        return ATTRIBUTE_BY_NAME.get(name.toLowerCase());
    }
}
