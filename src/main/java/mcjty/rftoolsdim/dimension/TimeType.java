package mcjty.rftoolsdim.dimension;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public enum TimeType {
    NORMAL("normal", DimensionRegistry.NORMAL_TIME_ID),
    DAY("day", DimensionRegistry.FIXED_DAY_ID),
    NIGHT("night", DimensionRegistry.FIXED_NIGHT_ID);

    private final String name;
    private final ResourceLocation dimensionType;

    private static final Map<String, TimeType> TYPE_BY_NAME = new HashMap<>();

    static {
        for (TimeType type : values()) {
            TYPE_BY_NAME.put(type.getName(), type);
        }
    }

    TimeType(String name, ResourceLocation dimensionType) {
        this.name = name;
        this.dimensionType = dimensionType;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getDimensionType() {
        return dimensionType;
    }

    public static TimeType byName(String name) {
        return TYPE_BY_NAME.get(name.toLowerCase());
    }
}
