package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.DimensionInformation;
import mcjty.rftoolsdim.dimension.DimensionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.List;

public class FlatChunkGenerator extends ChunkGenerator<FlatChunkGenerator.Config> {

    public FlatChunkGenerator(IWorld world, BiomeProvider biomeProvider) {
        super(world, biomeProvider, Config.createDefault());
    }

    @Override
    public void func_225551_a_(WorldGenRegion region, IChunk chunk) {
        BlockState bedrock = Blocks.BEDROCK.getDefaultState();

        DimensionInformation info = DimensionManager.get(region.getWorld()).getDimensionInformation(region.getWorld());
        List<BlockState> baseBlocks = info.getBaseBlocks();

        ChunkPos chunkpos = chunk.getPos();

        BlockPos.Mutable pos = new BlockPos.Mutable();

        int x;
        int z;

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                chunk.setBlockState(pos.setPos(x, 0, z), bedrock, false);
            }
        }

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
//                int realx = chunkpos.x * 16 + x;
//                int realz = chunkpos.z * 16 + z;
                int height = 65;    // @todo make configurable?
                for (int y = 1 ; y < height ; y++) {
                    chunk.setBlockState(pos.setPos(x, y, z), baseBlocks.get(region.getRandom().nextInt(baseBlocks.size())), false);
                }
            }
        }

    }

    @Override
    public int getGroundHeight() {
        return world.getSeaLevel()+1;
    }

    @Override
    public void makeBase(IWorld world, IChunk chunk) {

    }

    @Override
    public void decorate(WorldGenRegion region) {
        super.decorate(region);

        int chunkX = region.getMainChunkX() * 16;
        int chunkZ = region.getMainChunkZ() * 16;
        BlockPos blockpos = new BlockPos(chunkX, 0, chunkZ);
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), chunkX, chunkZ);

        DimensionInformation info = DimensionManager.get(region.getWorld()).getDimensionInformation(region.getWorld());
        int i = 0;
        for(GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
            try {
                for (ConfiguredFeature<?, ?> configuredFeature : info.getFeatures()) {
                    sharedseedrandom.setFeatureSeed(i1, i, stage.ordinal());
                    configuredFeature.place(region, this, sharedseedrandom, blockpos);
                }
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.makeCrashReport(exception, "Biome decoration");
                crashreport.makeCategory("Generation").addDetail("CenterX", chunkX).addDetail("CenterZ", chunkZ).addDetail("Step", stage).addDetail("Seed", i1);
                throw new ReportedException(crashreport);
            }
            ++i;
        }
    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
        // @todo
//        DimensionInformation info = DimensionManager.get(world.getWorld()).getDimensionInformation(world);
//        List<BlockState> baseBlocks = info.getBaseBlocks();
//
//        for(int i = ablockstate.length - 1; i >= 0; --i) {
//            BlockState blockstate = ablockstate[i];
//            if (blockstate != null && heightmapType.getHeightLimitPredicate().test(blockstate)) {
//                return i + 1;
//            }
//        }
        return 0;
    }

    public static class Config extends GenerationSettings {

        public static Config createDefault() {
            Config config = new Config();
            config.setDefaultBlock(Blocks.DIAMOND_BLOCK.getDefaultState());
            config.setDefaultFluid(Blocks.LAVA.getDefaultState());
            return config;
        }

    }

}
