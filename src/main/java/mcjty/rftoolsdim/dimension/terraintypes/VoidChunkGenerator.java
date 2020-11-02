package mcjty.rftoolsdim.dimension.terraintypes;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;

import java.util.List;
import java.util.Random;

public class VoidChunkGenerator extends BaseChunkGenerator<VoidChunkGenerator.Config> {

    public VoidChunkGenerator(IWorld world, BiomeProvider biomeProvider) {
        super(world, biomeProvider, 4, 8, 256, new Config(), true);  // @todo configurable settings?
        this.randomSeed.skip(2620);
    }

    @Override
    public int getGroundHeight() {
        return world.getSeaLevel()+1;
    }

    @Override
    protected void makeBaseInternal(IWorld world, int seaLevel,
                                    ObjectList<AbstractVillagePiece> villagePieces, ObjectList<JigsawJunction> jigsawJunctions,
                                    int chunkX, int chunkZ, int x, int z,
                                    List<BlockState> baseBlocks, ChunkPrimer primer,
                                    Heightmap heightmapOceanFloor, Heightmap heightmapWorldSurface) {
    }

    @Override
    protected void makeBedrock(IChunk chunkIn, Random rand) {
    }

    @Override
    protected double[] getBiomeNoiseColumn(int noiseX, int noiseZ) {
        return new double[0];
    }

    @Override
    protected void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {

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
