package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TerrainType {
    FLAT("flat", DimensionRegistry.FLAT_ID, FlatChunkGenerator::new),
    WAVES("waves", DimensionRegistry.WAVES_ID, WavesChunkGenerator::new),
    VOID("void", DimensionRegistry.VOID_ID, VoidChunkGenerator::new),
    NORMAL("normal", DimensionRegistry.NORMAL_ID, NormalChunkGenerator::new);

    private final String name;
    private final ResourceLocation typeId;
    private final BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> generatorSupplier;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, ResourceLocation typeId, BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> generatorSupplier) {
        this.name = name;
        this.typeId = typeId;
        this.generatorSupplier = generatorSupplier;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getTypeId() {
        return typeId;
    }

    public BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> getGeneratorSupplier() {
        return generatorSupplier;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name.toLowerCase());
    }
}
