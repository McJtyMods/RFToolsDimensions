package mcjty.rftoolsdim.modules.dimlets.data;

import java.util.HashMap;
import java.util.Map;

public enum DimletType {
    TERRAIN("t"),
    ATTRIBUTE("a"),
    BIOME_CONTROLLER("bc"),
    BIOME_CATEGORY("bt"),
    BIOME("bi"),
    FEATURE("f"),
    STRUCTURE("s"),
    TIME("ti"),
    TAG("ta"),
    BLOCK("b"),
    DIGIT("d"),
    ADMIN("ad"),
    FLUID("fl");

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
        return this != DIGIT && this != ADMIN;
    }

    public String getShortName() {
        return shortName;
    }

    public static DimletType byName(String name) {
        return TYPE_MAP.get(name.toLowerCase());
    }

}
