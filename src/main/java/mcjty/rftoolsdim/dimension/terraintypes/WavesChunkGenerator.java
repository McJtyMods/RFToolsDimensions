package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import mcjty.rftoolsdim.dimension.tools.OffsetBlockReader;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class WavesChunkGenerator extends BaseChunkGenerator {

    public static final Codec<WavesChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(WavesChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(WavesChunkGenerator::getDimensionSettings)
            ).apply(instance, WavesChunkGenerator::new));

    public WavesChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        this(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), settings);
    }

    public WavesChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(null, settings);  // @todo 1.18
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public ChunkGenerator withSeed(long l) {
        return new WavesChunkGenerator(getBiomeRegistry(), getDimensionSettings());
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = calculateWaveHeight(realx, realz);
                for (int y = 1 ; y < height ; y++) {
                    BlockState state = getDefaultBlock();
                    chunk.setBlockState(mpos.set(x, y, z), state, false);
                    hmOcean.update(x, y, z, state);
                    hmWorld.update(x, y, z, state);
                }
            }
        }
        return null;    // @todo 1.18
    }

    @Override
    public int getBaseHeight(int x, int z, @Nonnull Heightmap.Types type, LevelHeightAccessor level) {
        int realx = x;  // @todo 1.16 is this the actual x/z?
        int realz = z;
        int height = calculateWaveHeight(realx, realz);
        for (int i = height; i >= 0; --i) {
            BlockState blockstate = defaultBlocks.get(0);
            if (type.isOpaque().test(blockstate)) {
                return i + 1;
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level) {
        int realx = x;  // @todo 1.16 is this the actual x/z?
        int realz = z;
        int height = calculateWaveHeight(realx, realz);
//        return new OffsetBlockReader(defaultBlocks.get(0), height);
        return null;
    }

    private int calculateWaveHeight(int realx, int realz) {
        return (int) (65 + Math.sin(realx / 20.0f) * 10 + Math.cos(realz / 20.0f) * 10);
    }

}
