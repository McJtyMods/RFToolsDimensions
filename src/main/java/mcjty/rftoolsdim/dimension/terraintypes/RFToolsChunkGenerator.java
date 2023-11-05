package mcjty.rftoolsdim.dimension.terraintypes;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.compat.LostCityCompat;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSamplingSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSliderBuilder;
import mcjty.rftoolsdim.dimension.terraintypes.generators.*;
import mcjty.rftoolsdim.tools.PerlinNoiseGenerator14;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RFToolsChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<RFToolsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter(ins -> ins.structureSets),
                    RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(ins -> ins.noises),
                    Codec.list(StructureSet.CODEC).fieldOf("structures").forGetter(ins -> ins.overrideStructures),
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter((ins) -> ins.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter(RFToolsChunkGenerator::getSeed),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((ins) -> ins.settings),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("dimsettings").forGetter(RFToolsChunkGenerator::getDimensionSettings))
            .apply(instance, instance.stable(RFToolsChunkGenerator::new)));

    // Mirror because the one in NoiseBasedChunkGenerator is private
    private final Registry<NormalNoise.NoiseParameters> noises;
    private final DimensionSettings dimensionSettings;
    private final List<Holder<StructureSet>> overrideStructures;
    private final long seed;

    private PerlinNoiseGenerator14 perlinNoise = null;

    public RFToolsChunkGenerator(Registry<StructureSet> structureSetRegistry,
                                 Registry<NormalNoise.NoiseParameters> noiseRegistry,
                                 List<Holder<StructureSet>> overrideStructures,
                                 BiomeSource biomeSource, long seed,
                                 Holder<NoiseGeneratorSettings> settingsSupplier, DimensionSettings dimensionSettings) {
        super(structureSetRegistry, noiseRegistry, biomeSource, settingsSupplier);
        this.noises = noiseRegistry;
        this.dimensionSettings = dimensionSettings;
        this.overrideStructures = overrideStructures;
        this.seed = seed;
        this.featuresPerStep = Suppliers.memoize(() -> {
            return FeatureSorter.buildFeaturesPerStep(List.copyOf(biomeSource.possibleBiomes()), (biome) -> {
                List<HolderSet<PlacedFeature>> features = biome.value().getGenerationSettings().features();
                List<HolderSet<PlacedFeature>> newFeatures = new ArrayList<>();
                for (HolderSet<PlacedFeature> set : features) {
                    List<Holder<PlacedFeature>> list = set.stream().sorted(Comparator.comparing(placedFeatureHolder -> placedFeatureHolder.unwrapKey().map(ResourceKey::toString).orElse(""))).toList();
                    newFeatures.add(HolderSet.direct(list));
                }
                return newFeatures;
            }, true);
        });
    }

    @Override
    @Nonnull
    public Stream<Holder<StructureSet>> possibleStructureSets() {
        return overrideStructures.stream();
    }

    // Refresh after changing settings
    public void changeSettings(Consumer<NoiseSettingsBuilder> noiseBuilderConsumer,
                               Consumer<NoiseSamplingSettingsBuilder> samplingSettingsBuilderConsumer,
                               Consumer<NoiseSliderBuilder> topSliderBuilderConsumer,
                               Consumer<NoiseSliderBuilder> bottomSliderBuilderConsumer) {
        NoiseGeneratorSettings settings = this.settings.value();

        // @todo 1.19
//        NoiseSamplingSettingsBuilder samplingSettingsBuilder = NoiseSamplingSettingsBuilder.create(settings.noiseSettings().noiseSamplingSettings());
//        samplingSettingsBuilderConsumer.accept(samplingSettingsBuilder);
//
//        NoiseSliderBuilder topSliderBuilder = NoiseSliderBuilder.create(settings.noiseSettings().topSlideSettings());
//        topSliderBuilderConsumer.accept(topSliderBuilder);
//
//        NoiseSliderBuilder bottomSliderBuilder = NoiseSliderBuilder.create(settings.noiseSettings().bottomSlideSettings());
//        bottomSliderBuilderConsumer.accept(bottomSliderBuilder);
//
//        NoiseSettingsBuilder noiseSettingsBuilder = NoiseSettingsBuilder.create(settings.noiseSettings())
//                .samplingSettings(samplingSettingsBuilder)
//                .topSlider(topSliderBuilder)
//                .bottomSlider(bottomSliderBuilder);
//        noiseBuilderConsumer.accept(noiseSettingsBuilder);
//
//        NoiseGeneratorSettings newsettings = NoiseGeneratorSettingsBuilder.create(settings)
//                .noiseSettings(noiseSettingsBuilder)
//                .build(dimensionSettings);
//        this.settings = Holder.direct(newsettings);
    }

    public NoiseGeneratorSettings getNoiseGeneratorSettings() {
        return settings.value();
    }

    public long getSeed() {
        return seed;
    }

    public BlockState getDefaultBlock() {
        return defaultBlock;
    }

    public PerlinNoiseGenerator14 getPerlinNoise() {
        if (perlinNoise == null) {
            perlinNoise = new PerlinNoiseGenerator14(seed, 4);
        }
        return perlinNoise;
    }

    private void checkForCities(WorldGenRegion region, TerrainType terrainType) {
        if (LostCityCompat.hasLostCities() && dimensionSettings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.CITIES)) {
            LostCityCompat.registerDimension(region.getLevel().dimension(), LostCityCompat.getProfile(terrainType));
        }
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureFeatureManager, RandomState randomState, ChunkAccess chunkAccess) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        checkForCities(level, terrainType);
        if (terrainType != TerrainType.VOID && terrainType != TerrainType.FLAT) {
            super.buildSurface(level, structureFeatureManager, randomState, chunkAccess);
        }
    }

    @Override
    @Nonnull
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureFeatureManager, ChunkAccess chunkAccess) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        return switch (terrainType) {
            case FLAT -> FlatGenerator.fillFromNoise(chunkAccess, this);
            case VOID -> CompletableFuture.completedFuture(chunkAccess);
            case WAVES -> WavesGenerator.fillFromNoise(chunkAccess, this);
            case SPIKES -> SpikesGenerator.fillFromNoise(chunkAccess, this);
            case GRID -> GridGenerator.fillFromNoise(chunkAccess, this);
            case PLATFORMS -> PlatformsGenerator.fillFromNoise(chunkAccess, this);
            case MAZE -> MazeGenerator.fillFromNoise(chunkAccess, this);
            case RAVINE -> RavineGenerator.fillFromNoise(chunkAccess, this);
            default -> super.fillFromNoise(executor, blender, randomState, structureFeatureManager, chunkAccess);
        };
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        return switch (terrainType) {
            case FLAT -> FlatGenerator.FLATHEIGHT-1;
            case VOID -> level.getMinBuildHeight();
            case WAVES -> WavesGenerator.calculateWaveHeight(pX, pZ);
            case SPIKES -> SpikesGenerator.calculateSpikeHeight(pX, pZ, seed);
            case GRID -> GridGenerator.getBaseHeight(pX, pZ, level);
            case PLATFORMS -> PlatformsGenerator.getBaseHeight(pX, pZ, level, this);
            case MAZE -> MazeGenerator.getBaseHeight(pX, pZ, level);
            case RAVINE -> RavineGenerator.getBaseHeight(pX, pZ, this);
            default -> super.getBaseHeight(pX, pZ, type, level, randomState);
        };
    }

    @Override
    @NotNull
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level, RandomState randomState) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        return switch (terrainType) {
            case FLAT -> FlatGenerator.getBaseColumn(pX, pZ, level, this);
            case VOID -> new NoiseColumn(level.getMinBuildHeight(), new BlockState[0]);
            case WAVES -> WavesGenerator.getBaseColumn(pX, pZ, level, this);
            case SPIKES -> SpikesGenerator.getBaseColumn(pX, pZ, level, this);
            case GRID -> GridGenerator.getBaseColumn(pX, pZ, level, this);
            case PLATFORMS -> PlatformsGenerator.getBaseColumn(pX, pZ, level, this);
            case MAZE -> MazeGenerator.getBaseColumn(pX, pZ, level, this);
            case RAVINE -> RavineGenerator.getBaseColumn(pX, pZ, level, this);
            default -> super.getBaseColumn(pX, pZ, level, randomState);
        };
    }

    public DimensionSettings getDimensionSettings() {
        return dimensionSettings;
    }

    @Override
    @Nonnull
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
}
