package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.dimensions.world.GenericChunkGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * The base terrain generator.
 */
public interface BaseTerrainGenerator {
    void setup(World world, GenericChunkGenerator generator);

    void generate(int chunkX, int chunkZ, ChunkPrimer chunkPrimer);

    void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer chunkPrimer, Biome[] Biomes);

    static void setBlockState(ChunkPrimer primer, int index, IBlockState state) {
        primer.setBlockState((index >> 12) & 15, index & 255, (index >> 8) & 15, state);
    }

    // From 's' (inclusive) to 'e' (exclusive)
    static void setBlockStateRange(ChunkPrimer primer, int s, int e, IBlockState state) {
        while(s < e)
            setBlockState(primer, s++, state);
    }

    static IBlockState getBlockState(ChunkPrimer primer, int index) {
        return primer.getBlockState((index >> 12) & 15, index & 255, (index >> 8) & 15);
    }
}
