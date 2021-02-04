package mcjty.rftoolsdim.dimension;

import java.util.HashMap;
import java.util.Map;

public enum SpecialDimletType {
    OWNER("owner"),
    CHEATER("cheater");

    private final String name;

    private static final Map<String, SpecialDimletType> SPECIAL_BY_NAME = new HashMap<>();

    static {
        for (SpecialDimletType type : values()) {
            SPECIAL_BY_NAME.put(type.getName(), type);
        }
    }

    SpecialDimletType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SpecialDimletType byName(String name) {
        return SPECIAL_BY_NAME.get(name.toLowerCase());
    }
}
