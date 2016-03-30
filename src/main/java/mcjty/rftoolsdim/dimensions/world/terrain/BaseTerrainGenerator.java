package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * The base terrain generator.
 */
public interface BaseTerrainGenerator {
    void setup(World world, GenericChunkProvider provider);

    void generate(int chunkX, int chunkZ, ChunkPrimer chunkPrimer);

    void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer chunkPrimer, BiomeGenBase[] biomeGenBases);

    static final IBlockState defaultState = Blocks.air.getDefaultState();

    public static void setBlockState(ChunkPrimer primer, int index, IBlockState state) {
        primer.data[index] = (char) Block.BLOCK_STATE_IDS.get(state);
    }

    public static IBlockState getBlockState(ChunkPrimer primer, int index) {
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(primer.data[index]);
        return iblockstate == null ? defaultState : iblockstate;

    }
}
