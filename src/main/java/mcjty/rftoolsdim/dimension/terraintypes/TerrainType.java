package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TerrainType {
    FLAT("flat", KnowledgeSet.SET1, FlatChunkGenerator::new),
    WAVES("waves", KnowledgeSet.SET1, WavesChunkGenerator::new),
    VOID("void", KnowledgeSet.SET2, VoidChunkGenerator::new),
    NORMAL("normal", KnowledgeSet.SET3, NormalChunkGenerator::new);

    private final String name;
    private final KnowledgeSet set;
    private final BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> generatorSupplier;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, KnowledgeSet set, BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> generatorSupplier) {
        this.name = name;
        this.set = set;
        this.generatorSupplier = generatorSupplier;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public BiFunction<MinecraftServer, DimensionSettings, ChunkGenerator> getGeneratorSupplier() {
        return generatorSupplier;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name.toLowerCase());
    }
}
