package mcjty.rftoolsdim.dimensions.world.mapgen;

import mcjty.rftoolsdim.dimensions.world.GenericChunkGenerator;
import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
import mcjty.rftoolsdim.dimensions.world.terrain.BaseTerrainGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class MapGenOrbs {
    private final GenericChunkGenerator provider;
    private final boolean large;
    private final int r;

    public MapGenOrbs(GenericChunkGenerator provider, boolean large) {
        this.provider = provider;
        this.large = large;
        r = large ? 2 : 1;
    }

    public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState[] blocks = large ? provider.dimensionInformation.getHugeSphereBlocks() : provider.dimensionInformation.getSphereBlocks();

        for (int cx = -r ; cx <= r ; cx++) {
            for (int cz = -r ; cz <= r ; cz++) {

                Random random = new Random((world.getSeed() + (chunkX+cx)) * 113 + (chunkZ+cz) * 31 + 77);
                random.nextFloat();

                if (random.nextFloat() < .05f) {
                    int x = cx * 16 + random.nextInt(16);
                    int y = 40 + random.nextInt(40);
                    int z = cz * 16 + random.nextInt(16);
                    int radius = random.nextInt(large ? 20 : 6) + (large ? 10 : 4);

                    IBlockState block = Blocks.stone.getDefaultState();
                    if (blocks.length > 1) {
                        block = blocks[random.nextInt(blocks.length)];
                    } else if (blocks.length == 1) {
                        block = blocks[0];
                    }

                    fillSphere(primer, x, y, z, radius, block);
                }
            }
        }
    }

    private void fillSphere(ChunkPrimer primer, int centerx, int centery, int centerz, int radius, IBlockState block) {
        double sqradius = radius * radius;

        for (int x = 0 ; x < 16 ; x++) {
            double dxdx = (x-centerx) * (x-centerx);
            for (int z = 0 ; z < 16 ; z++) {
                double dzdz = (z-centerz) * (z-centerz);
                int index = (x * 16 + z) * 256;
                for (int y = centery-radius ; y <= centery+radius ; y++) {
                    double dydy = (y-centery) * (y-centery);
                    double sqdist = dxdx + dydy + dzdz;
                    if (sqdist <= sqradius) {
                        BaseTerrainGenerator.setBlockState(primer, index + y, block);
                    }
                }
            }
        }
    }

}
