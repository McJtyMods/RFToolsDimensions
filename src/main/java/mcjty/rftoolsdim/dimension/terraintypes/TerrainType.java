package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.DimensionRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum TerrainType {
    FLAT("flat", DimensionRegistry.FLAT_ID, server -> null),
    WAVES("waves", DimensionRegistry.WAVES_ID, WavesChunkGenerator::new),
    VOID("void", DimensionRegistry.VOID_ID, VoidChunkGenerator::new),
    NORMAL("normal", DimensionRegistry.NORMAL_ID, server -> null);

    private final String name;
    private final ResourceLocation typeId;
    private final Function<MinecraftServer, ChunkGenerator> generatorSupplier;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, ResourceLocation typeId, Function<MinecraftServer, ChunkGenerator> generatorSupplier) {
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

    public Function<MinecraftServer, ChunkGenerator> getGeneratorSupplier() {
        return generatorSupplier;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name);
    }
}
