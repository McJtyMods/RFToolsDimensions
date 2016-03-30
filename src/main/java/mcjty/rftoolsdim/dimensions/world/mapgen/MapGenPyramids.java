package mcjty.rftoolsdim.dimensions.world.mapgen;

import mcjty.lib.varia.BlockMeta;
import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
import mcjty.rftoolsdim.dimensions.world.terrain.BaseTerrainGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class MapGenPyramids {
    private final GenericChunkProvider provider;

    public MapGenPyramids(GenericChunkProvider provider) {
        this.provider = provider;
    }

    public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState[] blocks = provider.dimensionInformation.getPyramidBlocks();

        Random random = new Random((world.getSeed() + (chunkX)) * 1133 + (chunkZ) * 37 + 77);
        random.nextFloat();

        if (random.nextFloat() < .05f) {
            int x = 8;
            int z = 8;

            int y = getBestY(primer, 8, 8);
            if (y < 10 || y > 230) {
                return;
            }
            IBlockState block = Blocks.stone.getDefaultState();
            if (blocks.length > 1) {
                block = blocks[random.nextInt(blocks.length)];
            } else if (blocks.length == 1) {
                block = blocks[0];
            }

            for (int i = 7 ; i >= 0 ; i--) {
                for (int dx = -i ; dx <= i-1 ; dx++) {
                    for (int dz = -i ; dz <= i-1 ; dz++) {
                        int index = ((x+dx) * 16 + (z+dz)) * 256;
                        BaseTerrainGenerator.setBlockState(primer, index + y, block);
                    }
                }
                y++;
            }
        }
    }

    private int getBestY(ChunkPrimer primer, int x, int z) {
        int y = findTopSolid(primer, x, z);
        int y1 = findTopSolid(primer, x - 7, z - 7);
        if (y1 < y) {
            y = y1;
        }
        y1 = findTopSolid(primer, x + 7, z - 7);
        if (y1 < y) {
            y = y1;
        }
        y1 = findTopSolid(primer, x - 7, z + 7);
        if (y1 < y) {
            y = y1;
        }
        y1 = findTopSolid(primer, x + 7, z + 7);
        if (y1 < y) {
            y = y1;
        }
        return y;
    }

    private int findTopSolid(ChunkPrimer primer, int x, int z) {
        int index = (x * 16 + z) * 256;
        int y = 255;
        while (y >= 5 && (BaseTerrainGenerator.getBlockState(primer, index+y) == null || BaseTerrainGenerator.getBlockState(primer, index+y).getBlock().getMaterial(BaseTerrainGenerator.getBlockState(primer, index+y)) == Material.air)) {
            y--;
        }
        return y;
    }
}
