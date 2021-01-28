package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TerrainType {
    FLAT("flat", FlatChunkGenerator::new),
    WAVES("waves", WavesChunkGenerator::new),
    VOID("void", VoidChunkGenerator::new),
    NORMAL("normal", NormalChunkGenerator::new);

    private final String name;
    private final BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> generatorSupplier;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> generatorSupplier) {
        this.name = name;
        this.generatorSupplier = generatorSupplier;
    }

    public String getName() {
        return name;
    }

    public BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> getGeneratorSupplier() {
        return generatorSupplier;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name.toLowerCase());
    }
}
