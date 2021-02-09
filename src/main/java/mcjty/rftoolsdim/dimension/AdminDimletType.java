package mcjty.rftoolsdim.dimension;

import java.util.HashMap;
import java.util.Map;

public enum AdminDimletType {
    OWNER("owner"),
    CHEATER("cheater");

    private final String name;

    private static final Map<String, AdminDimletType> ADMIN_BY_NAME = new HashMap<>();

    static {
        for (AdminDimletType type : values()) {
            ADMIN_BY_NAME.put(type.getName(), type);
        }
    }

    AdminDimletType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AdminDimletType byName(String name) {
        return ADMIN_BY_NAME.get(name.toLowerCase());
    }
}
