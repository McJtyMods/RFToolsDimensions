package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseGeneratorSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSamplingSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSettingsBuilder;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseSliderBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFToolsChunkGenerator extends NoiseBasedChunkGenerator {

    public static final int FLATHEIGHT = 120;
    public static final int WAVE_BASE = 80;

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
        if (terrainType != TerrainType.VOID && terrainType != TerrainType.FLAT) {
            super.buildSurface(level, structureFeatureManager, chunkAccess);
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        return switch (terrainType) {
            case FLAT -> fillFromNoiseFlat(chunkAccess);
            case VOID -> CompletableFuture.completedFuture(chunkAccess);
            case WAVES -> fillFromNoiseWaves(chunkAccess);
            default -> super.fillFromNoise(executor, blender, structureFeatureManager, chunkAccess);
        };
    }

    @NotNull
    private CompletableFuture<ChunkAccess> fillFromNoiseWaves(ChunkAccess chunkAccess) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = calculateWaveHeight(realx, realz);
                for (int y = chunkAccess.getMinBuildHeight() ; y < height ; y++) {
                    BlockState state = defaultBlock;
                    chunkAccess.setBlockState(mpos.set(x, y, z), state, false);
                    hmOcean.update(x, y, z, state);
                    hmWorld.update(x, y, z, state);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    private int calculateWaveHeight(int realx, int realz) {
        return (int) (WAVE_BASE + Math.sin(realx / 20.0f) * 10 + Math.cos(realz / 20.0f) * 10);
    }

    @NotNull
    private CompletableFuture<ChunkAccess> fillFromNoiseFlat(ChunkAccess chunkAccess) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for (int y = chunkAccess.getMinBuildHeight(); y < FLATHEIGHT; ++y) {
            BlockState blockstate = defaultBlock;
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    chunkAccess.setBlockState(mpos.set(x, y, z), blockstate, false);
                    heightmap.update(x, y, z, blockstate);
                    heightmap1.update(x, y, z, blockstate);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types type, LevelHeightAccessor level) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        return switch (terrainType) {
            case FLAT -> FLATHEIGHT-1;
            case VOID -> level.getMinBuildHeight();
            case WAVES -> calculateWaveHeight(pX, pZ);
            default -> super.getBaseHeight(pX, pZ, type, level);
        };
    }

    public @NotNull NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level) {
        TerrainType terrainType = dimensionSettings.getCompiledDescriptor().getTerrainType();
        return switch (terrainType) {
            case FLAT -> getBaseColumnFlat(pX, pZ, level);
            case VOID -> new NoiseColumn(level.getMinBuildHeight(), new BlockState[0]);
            case WAVES -> getBaseColumnWaves(pX, pZ, level);
            default -> super.getBaseColumn(pX, pZ, level);
        };
    }

    private NoiseColumn getBaseColumnFlat(int pX, int pZ, LevelHeightAccessor level) {
        BlockState[] states = new BlockState[FLATHEIGHT-1];
        Arrays.fill(states, defaultBlock);
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }

    private NoiseColumn getBaseColumnWaves(int pX, int pZ, LevelHeightAccessor level) {
        BlockState[] states = new BlockState[calculateWaveHeight(pX, pZ)];
        Arrays.fill(states, defaultBlock);
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }

    public DimensionSettings getDimensionSettings() {
        return dimensionSettings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
}
