package mcjty.rftoolsdim.dimensions.world.terrain;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class GridTerrainGenerator extends NormalTerrainGenerator {

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState baseBlock = provider.dimensionInformation.getBaseBlockForTerrain();

        int borderx;
        if ((chunkX & 1) == 0) {
            borderx = 0;
        } else {
            borderx = 15;
        }
        int borderz;
        if ((chunkZ & 1) == 0) {
            borderz = 0;
        } else {
            borderz = 15;
        }

        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                // Clear the bedrock
                for (int y = 0 ; y < 10 ; y++) {
                    // @todo optimize references to air/defaultstate
                    BaseTerrainGenerator.setBlockState(primer, index+y, Blocks.AIR.getDefaultState());
                }

                boolean filled = (x == borderx) && (z == borderz);
                if (filled) {
                    for (int y = 0 ; y < 128 ; y++) {
                        BaseTerrainGenerator.setBlockState(primer, index++, baseBlock);
                        // @todo support for 127
//                        if (baseMeta == 127) {
//                            realMeta = (byte)(y & 0xf);
                    }
                    index += 128;
                } else if (x == borderx || z == borderz) {
                    for (int y = 0 ; y < 128 ; y+=32) {
                        if (y > 0) {
                            BaseTerrainGenerator.setBlockState(primer, index-1, baseBlock);
                            // @todo support for 127
//                            if (baseMeta == 127) {
//                                realMeta = (byte)((y/2 + x/2 + z/2) & 0xf);
                        }
                        BaseTerrainGenerator.setBlockState(primer, index, baseBlock);
                        // @todo support for 127
//                        if (baseMeta == 127) {
//                            realMeta = (byte)((y/2 + x/2 + z/2) & 0xf);
                        index += 32;
                    }
                    index += 128;
                } else {
                    index += 256;
                }
            }
        }
    }

    @Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] Biomes) {
    }
}
