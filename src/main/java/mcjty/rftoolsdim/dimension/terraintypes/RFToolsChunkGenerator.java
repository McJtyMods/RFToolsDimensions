package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseGeneratorSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSamplingSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSliderBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
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

    // Refresh after changing settings
    public void changeSettings(Consumer<NoiseSettingsBuilder> noiseBuilderConsumer,
                               Consumer<NoiseSamplingSettingsBuilder> samplingSettingsBuilderConsumer,
                               Consumer<NoiseSliderBuilder> topSliderBuilderConsumer,
                               Consumer<NoiseSliderBuilder> bottomSliderBuilderConsumer) {
        NoiseGeneratorSettings settings = this.settings.get();

        NoiseSamplingSettingsBuilder samplingSettingsBuilder = NoiseSamplingSettingsBuilder.create(settings.noiseSettings().noiseSamplingSettings());
        samplingSettingsBuilderConsumer.accept(samplingSettingsBuilder);

        NoiseSliderBuilder topSliderBuilder = NoiseSliderBuilder.create(settings.noiseSettings().topSlideSettings());
        topSliderBuilderConsumer.accept(topSliderBuilder);

        NoiseSliderBuilder bottomSliderBuilder = NoiseSliderBuilder.create(settings.noiseSettings().bottomSlideSettings());
        bottomSliderBuilderConsumer.accept(bottomSliderBuilder);

        NoiseSettingsBuilder noiseSettingsBuilder = NoiseSettingsBuilder.create(settings.noiseSettings())
                .samplingSettings(samplingSettingsBuilder)
                .topSlider(topSliderBuilder)
                .bottomSlider(bottomSliderBuilder);
        noiseBuilderConsumer.accept(noiseSettingsBuilder);

        NoiseGeneratorSettings newsettings = NoiseGeneratorSettingsBuilder.create(settings)
                .noiseSettings(noiseSettingsBuilder)
                .build();
        this.settings = () -> newsettings;
        this.sampler = new NoiseSampler(newsettings.noiseSettings(), newsettings.isNoiseCavesEnabled(), seed, noises, newsettings.getRandomSource());
    }

    public NoiseGeneratorSettings getNoiseGeneratorSettings() {
        return settings.get();
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        if (terrainType != TerrainType.VOID) {
            super.buildSurface(level, structureFeatureManager, chunkAccess);
        }
//        level.getLevel().isDebug = true;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        if (terrainType != TerrainType.VOID) {
            return super.fillFromNoise(executor, blender, structureFeatureManager, chunkAccess);
        } else {
            return CompletableFuture.completedFuture(chunkAccess);
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
