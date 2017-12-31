package mcjty.rftoolsdim.dimensions.types;

import java.util.HashMap;
import java.util.Map;

public enum SpecialType {
    SPECIAL_PEACEFUL("Peaceful", true),
    SPECIAL_EFFICIENCY("Efficiency", true),
    SPECIAL_EFFICIENCY_LOW("MediocreEfficiency", true),
    SPECIAL_SHELTER("Shelter", false),
    SPECIAL_SEED("Seed", false),
    SPECIAL_SPAWN("Spawn", true),
    SPECIAL_NOANIMALS("NoAnimals", true),
    SPECIAL_OWNER("Owner", false),
    SPECIAL_CHEATER("Cheater", true);

    private static final Map<String,SpecialType> SPECIAL_TYPE_MAP = new HashMap<>();

    static {
        for (SpecialType type : SpecialType.values()) {
            SPECIAL_TYPE_MAP.put(type.getId(), type);
        }
    }

    private final String id;
    private final boolean injectable;

    private SpecialType(String id, boolean isInjectable) {
        this.id = id;
        this.injectable = isInjectable;
    }

    public String getId() {
        return id;
    }

    public boolean isInjectable() {
        return injectable;
    }

    public static SpecialType getSpecialById(String id) {
        return SPECIAL_TYPE_MAP.get(id);
    }

}
