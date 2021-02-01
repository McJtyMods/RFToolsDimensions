package mcjty.rftoolsdim.modules.dimlets.data;

import java.util.HashMap;
import java.util.Map;

public enum DimletType {
    TERRAIN("t"),
    BIOME_CONTROLLER("bc"),
    BIOME("bi"),
    FEATURE("f"),
    TIME("ti"),
    BLOCK("b"),
    DIGIT("d");

    private final String shortName;

    private static final Map<String, DimletType> TYPE_MAP = new HashMap<>();

    static {
        for (DimletType type : values()) {
            TYPE_MAP.put(type.getShortName().toLowerCase(), type);
            TYPE_MAP.put(type.name().toLowerCase(), type);
        }
    }

    DimletType(String shortName) {
        this.shortName = shortName;
    }

    public boolean usesKnowledgeSystem() {
        return this != DIGIT;
    }

    public String getShortName() {
        return shortName;
    }

    public static DimletType byName(String name) {
        return TYPE_MAP.get(name.toLowerCase());
    }

}
