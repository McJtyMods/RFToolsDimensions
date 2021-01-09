package mcjty.rftoolsdim.dimension.terraintypes;

import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TerrainType {
    FLAT("flat"),
    WAVING("waving"),
    VOID("void"),
    NORMAL("normal");

    private final String name;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name);
    }
}
