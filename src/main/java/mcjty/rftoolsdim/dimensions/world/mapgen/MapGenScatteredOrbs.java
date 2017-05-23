package mcjty.rftoolsdim.dimensions.world.mapgen;

import mcjty.rftoolsdim.dimensions.world.GenericChunkGenerator;
import mcjty.rftoolsdim.dimensions.world.terrain.BaseTerrainGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class MapGenScatteredOrbs {
    private final GenericChunkGenerator provider;
    private final int r;

    public MapGenScatteredOrbs(GenericChunkGenerator provider) {
        this.provider = provider;
        r = 3;
    }

    public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState[] blocks = provider.dimensionInformation.getScatteredSphereBlocks();

        for (int cx = -r ; cx <= r ; cx++) {
            for (int cz = -r ; cz <= r ; cz++) {

                Random random = new Random((world.getSeed() + (chunkX+cx)) * 113 + (chunkZ+cz) * 31 + 77);
                random.nextFloat();

                if (random.nextFloat() < .05f) {
                    int x = cx * 16 + random.nextInt(16);
                    int y = 40 + random.nextInt(40);
                    int z = cz * 16 + random.nextInt(16);
                    int radius = random.nextInt(30) + (20);

                    IBlockState block = Blocks.STONE.getDefaultState();
                    if (blocks.length > 1) {
                        block = blocks[random.nextInt(blocks.length)];
                    } else if (blocks.length == 1) {
                        block = blocks[0];
                    }

                    fillSphere(primer, x, y, z, radius, block, random);
                }
            }
        }
    }

    private void fillSphere(ChunkPrimer primer, int centerx, int centery, int centerz, int radius, IBlockState block,
                            Random random) {
        for (int x = 0 ; x < 16 ; x++) {
            double dxdx = (x-centerx) * (x-centerx);
            for (int z = 0 ; z < 16 ; z++) {
                double dzdz = (z-centerz) * (z-centerz);
                int index = (x * 16 + z) * 256;
                for (int y = centery-radius ; y <= centery+radius ; y++) {
                    double dydy = (y-centery) * (y-centery);
                    double dist = Math.sqrt(dxdx + dydy + dzdz);
                    if (dist < radius) {
                        double f = 1 - (dist / radius);
                        if (random.nextDouble() < (f * f)) {
                            BaseTerrainGenerator.setBlockState(primer, index + y, block);
                        }
                    }
                }
            }
        }
    }

}
