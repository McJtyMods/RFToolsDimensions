package mcjty.rftoolsdim.dimensions.types;

import java.util.HashMap;
import java.util.Map;

public enum StructureType {
    STRUCTURE_NONE("None"),
    STRUCTURE_VILLAGE("Village"),
    STRUCTURE_STRONGHOLD("Stronghold"),
    STRUCTURE_DUNGEON("Dungeon"),
    STRUCTURE_FORTRESS("Fortress"),
    STRUCTURE_MINESHAFT("Mineshaft"),
    STRUCTURE_SCATTERED("Scattered"),
    STRUCTURE_RECURRENTCOMPLEX("RecurrentComplex");

    private static final Map<String,StructureType> STRUCTURE_TYPE_MAP = new HashMap<>();

    static {
        for (StructureType type : StructureType.values()) {
            STRUCTURE_TYPE_MAP.put(type.getId(), type);
        }
    }

    private final String id;

    StructureType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static StructureType getStructureById(String id) {
        return STRUCTURE_TYPE_MAP.get(id);
    }
}
