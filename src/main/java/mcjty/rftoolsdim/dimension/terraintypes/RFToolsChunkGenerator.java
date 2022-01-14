package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.function.Supplier;

public class RFToolsChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<RFToolsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(RegistryLookupCodec.create(
                    Registry.NOISE_REGISTRY).forGetter((ins) -> ins.noises),
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter((ins) -> ins.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter((ins) -> ins.seed),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((ins) -> ins.settings),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("dimsettings").forGetter(RFToolsChunkGenerator::getDimensionSettings))
            .apply(instance, instance.stable(RFToolsChunkGenerator::new)));

    // Mirror because the one in NoiseBasedChunkGenerator is private
    private final Registry<NormalNoise.NoiseParameters> noises;
    protected final DimensionSettings dimensionSettings;

    public RFToolsChunkGenerator(Registry<NormalNoise.NoiseParameters> noiseRegistry, BiomeSource biomeSource, long seed,
                                 Supplier<NoiseGeneratorSettings> settingsSupplier, DimensionSettings dimensionSettings) {
        super(noiseRegistry, biomeSource, seed, settingsSupplier);
        this.noises = noiseRegistry;
        this.dimensionSettings = dimensionSettings;
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        if (terrainType != TerrainType.VOID) {
            super.buildSurface(level, structureFeatureManager, chunkAccess);
        }
    }

    public DimensionSettings getDimensionSettings() {
        return dimensionSettings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
}
