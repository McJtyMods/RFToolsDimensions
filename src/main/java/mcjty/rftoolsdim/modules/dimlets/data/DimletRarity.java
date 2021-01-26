package mcjty.rftoolsdim.modules.dimlets.data;

import java.util.HashMap;
import java.util.Map;

public enum DimletRarity {
    COMMON,
    UNCOMMON,
    RARE,
    LEGENDARY;

    private static final Map<String, DimletRarity> TYPE_MAP = new HashMap<>();

    static {
        for (DimletRarity type : values()) {
            TYPE_MAP.put(type.name().toLowerCase(), type);
            TYPE_MAP.put(type.getShortName(), type);
        }
    }

    public String getShortName() {
        return name().substring(0, 1).toLowerCase();
    }

    public static DimletRarity byName(String name) {
        return TYPE_MAP.get(name.toLowerCase());
    }

}
