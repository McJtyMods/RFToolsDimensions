package mcjty.rftoolsdim.dimensions.types;

import java.util.HashMap;
import java.util.Map;

public enum SpecialType {
    SPECIAL_PEACEFUL("Peaceful"),
    SPECIAL_EFFICIENCY("Efficiency"),
    SPECIAL_EFFICIENCY_LOW("MediocreEfficiency"),
    SPECIAL_SHELTER("Shelter"),
    SPECIAL_SEED("Seed"),
    SPECIAL_SPAWN("Spawn"),
    SPECIAL_NOANIMALS("NoAnimals"),
    SPECIAL_OWNER("Owner");

    private static final Map<String,SpecialType> SPECIAL_TYPE_MAP = new HashMap<>();

    static {
        for (SpecialType type : SpecialType.values()) {
            SPECIAL_TYPE_MAP.put(type.getId(), type);
        }
    }


    private final String id;


    SpecialType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static SpecialType getSpecialById(String id) {
        return SPECIAL_TYPE_MAP.get(id);
    }

}
