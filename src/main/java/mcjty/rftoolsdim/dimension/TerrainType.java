package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.dimension.terraintypes.FlatChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.NormalChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.WavesChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TerrainType {
    FLAT("flat", FlatChunkGenerator::new),
    WAVING("waving", WavesChunkGenerator::new),
    NORMAL("normal", NormalChunkGenerator::new);

    private final String name;
    private final BiFunction<IWorld, BiomeProvider, ChunkGenerator<?>> generatorSupplier;

    private static Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, BiFunction<IWorld, BiomeProvider, ChunkGenerator<?>> generatorSupplier) {
        this.name = name;
        this.generatorSupplier = generatorSupplier;
    }

    public String getName() {
        return name;
    }

    public BiFunction<IWorld, BiomeProvider, ChunkGenerator<?>> getGenerator() {
        return generatorSupplier;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name);
    }
}
