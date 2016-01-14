package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
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
}
