package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.types.FlatDimension;
import mcjty.rftoolsdim.dimension.types.NormalDimension;
import mcjty.rftoolsdim.dimension.types.VoidDimension;
import mcjty.rftoolsdim.dimension.types.WavesDimension;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TerrainType {
    FLAT("flat", FlatDimension::new),
    WAVING("waving", WavesDimension::new),
    VOID("void", VoidDimension::new),
    NORMAL("normal", NormalDimension::new);

    private final String name;
    private final BiFunction<World, DimensionType, Dimension> dimensionSupplier;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, BiFunction<World, DimensionType, Dimension> dimensionSupplier) {
        this.name = name;
        this.dimensionSupplier = dimensionSupplier;
    }

    public String getName() {
        return name;
    }

    public BiFunction<World, DimensionType, Dimension> getDimensionSupplier() {
        return dimensionSupplier;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name);
    }
}
