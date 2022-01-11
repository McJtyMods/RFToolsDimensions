package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;

import java.util.HashMap;
import java.util.Map;

public enum TerrainType {
    FLAT("flat", KnowledgeSet.SET1),
    WAVES("waves", KnowledgeSet.SET1),
    VOID("void", KnowledgeSet.SET2),
    NORMAL("normal", KnowledgeSet.SET3),
    ISLANDS("islands", KnowledgeSet.SET4),
    CAVERN("cavern", KnowledgeSet.SET5)
    ;

    private final String name;
    private final KnowledgeSet set;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, KnowledgeSet set) {
        this.name = name;
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name.toLowerCase());
    }
}
