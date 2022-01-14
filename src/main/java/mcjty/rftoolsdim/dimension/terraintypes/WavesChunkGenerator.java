package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Deprecated
public class WavesChunkGenerator extends NoiseBasedChunkGenerator {

    protected final DimensionSettings settings;
    protected final List<BlockState> defaultBlocks = new ArrayList<>();
    private final Random random;
    private BlockState defaultFluid;

    public WavesChunkGenerator(Registry<NormalNoise.NoiseParameters> p_188609_, BiomeSource p_188610_, long p_188611_, Supplier<NoiseGeneratorSettings> p_188612_, DimensionSettings settings, Random random) {
        super(p_188609_, p_188610_, p_188611_, p_188612_);
        this.settings = settings;
        this.random = random;
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
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

    // Get a (possibly random) default block
    public BlockState getDefaultBlock() {
        if (defaultBlocks.size() == 1) {
            return defaultBlocks.get(0);
        } else {
            int idx = random.nextInt(defaultBlocks.size());
            return defaultBlocks.get(idx);
        }
    }

    public DimensionSettings getDimensionSettings() {
        return settings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((RFTBiomeProvider)biomeSource).getBiomeRegistry();
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
        super.buildSurface(region, structureFeatureManager, chunk);
    }

    protected BlockState getBaseLiquid() {
        return this.defaultFluid;
    }

    protected void makeBedrock(ChunkAccess chunkIn) {
        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.NOBEDROCK)) {
            return;
        }
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        int xs = chunkIn.getPos().getMinBlockX();
        int zs = chunkIn.getPos().getMinBlockZ();
        for(BlockPos blockpos : BlockPos.betweenClosed(xs, 0, zs, xs + 15, 0, zs + 15)) {
            for(int y = 4; y >= 0; --y) {
                if (y <= random.nextInt(5)) {
                    chunkIn.setBlockState(mpos.set(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                }
            }
        }
    }
}
