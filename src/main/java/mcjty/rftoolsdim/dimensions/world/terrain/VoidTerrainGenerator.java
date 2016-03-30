package mcjty.rftoolsdim.dimensions.world.terrain;

import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;

public class VoidTerrainGenerator implements BaseTerrainGenerator {

    @Override
    public void setup(World world, GenericChunkProvider provider) {

    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        for (int i = 0 ; i < 65536 ; i++) {
            BaseTerrainGenerator.setBlockState(primer, i, Blocks.air.getDefaultState());
        }
    }

    @Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, BiomeGenBase[] biomeGenBases) {
        // @todo?
//        for (int i = 0 ; i < 65536 ; i++) {
//            abyte[i] = 0;
//        }
    }

}
