package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class BaseChunkGenerator extends ChunkGenerator {

    protected final DimensionSettings settings;
    protected INoiseGenerator surfaceDepthNoise;
    protected final SharedSeedRandom randomSeed;

    protected final List<BlockState> defaultBlocks = new ArrayList<>();
    private BlockState defaultFluid;

    public BaseChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(new RFTBiomeProvider(registry, settings), new DimensionStructuresSettings(false));
        this.settings = settings;
        this.randomSeed = new SharedSeedRandom(settings.getSeed());
//        this.surfaceDepthNoise = (INoiseGenerator)(noisesettings.useSimplexSurfaceNoise() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
        this.surfaceDepthNoise = new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0));  //) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
        defaultBlocks.addAll(settings.getCompiledDescriptor().getBaseBlocks());
        defaultFluid = settings.getCompiledDescriptor().getBaseLiquid();
        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.NOOCEANS)) {
            defaultFluid = Blocks.AIR.defaultBlockState();
        }
    }

    // Get a (possibly random) default block
    public BlockState getDefaultBlock() {
        if (defaultBlocks.size() == 1) {
            return defaultBlocks.get(0);
        } else {
            int idx = randomSeed.nextInt(defaultBlocks.size());
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
    public void buildSurfaceAndBedrock(WorldGenRegion region, IChunk chunk) {
        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.NOBIOMESURFACE)) {
            this.makeBedrock(chunk);
            return;
        }
        ChunkPos chunkpos = chunk.getPos();
        int cx = chunkpos.x;
        int cz = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setBaseChunkSeed(cx, cz);
        ChunkPos chunkpos1 = chunk.getPos();
        int xStart = chunkpos1.getMinBlockX();
        int zStart = chunkpos1.getMinBlockZ();
        BlockPos.Mutable mpos = new BlockPos.Mutable();

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                int xx = xStart + x;
                int zz = zStart + z;
                int yy = chunk.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                double noise = this.surfaceDepthNoise.getSurfaceNoiseValue((double)xx * 0.0625D, (double)zz * 0.0625D, 0.0625D, (double)x * 0.0625D) * 15.0D;
                region.getBiome(mpos.set(xStart + x, yy, zStart + z))
                        .buildSurfaceAt(sharedseedrandom, chunk, xx, zz, yy, noise, defaultBlocks.get(0), getBaseLiquid(), this.getSeaLevel(), region.getSeed());
            }
        }
        this.makeBedrock(chunk);
    }

    protected BlockState getBaseLiquid() {
        return this.defaultFluid;
    }


    protected void makeBedrock(IChunk chunkIn) {
        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.NOBEDROCK)) {
            return;
        }
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        int xs = chunkIn.getPos().getMinBlockX();
        int zs = chunkIn.getPos().getMinBlockZ();
        for(BlockPos blockpos : BlockPos.betweenClosed(xs, 0, zs, xs + 15, 0, zs + 15)) {
            for(int y = 4; y >= 0; --y) {
                if (y <= randomSeed.nextInt(5)) {
                    chunkIn.setBlockState(mpos.set(blockpos.getX(), y, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                }
            }
        }
    }

}
