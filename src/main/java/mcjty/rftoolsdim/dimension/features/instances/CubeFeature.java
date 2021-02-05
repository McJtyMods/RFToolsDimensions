package mcjty.rftoolsdim.dimension.features.instances;

import mcjty.rftoolsdim.dimension.features.IFeature;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class CubeFeature implements IFeature {

    private final boolean hollow;

    public CubeFeature(boolean hollow) {
        this.hollow = hollow;
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos,
                            List<BlockState> states, long prime) {
        ChunkPos cp = new ChunkPos(pos);
        int chunkX = cp.x;
        int chunkZ = cp.z;
        int size = 1;

        boolean generated = false;
        for (int dx = -size; dx <= size; dx++) {
            int cx = chunkX + dx;
            for (int dz = -size; dz <= size; dz++) {
                int cz = chunkZ + dz;
                if (isFeatureCenter(reader, cx, cz, prime)) {
                    generate(reader, chunkX, chunkZ, dx, dz, states, prime);
                    generated = true;
                }
            }
        }
        return generated;
    }

    private void generate(ISeedReader world, int chunkX, int chunkZ, int dx, int dz,
                          List<BlockState> states, long prime) {
        Random random = new Random(world.getSeed() + (chunkZ+dz) * prime + (chunkX+dx) * 899809363L);
        random.nextFloat();
        int radius = random.nextInt(12) + 9;
        int centery = random.nextInt(60) + 40;

        int centerx = 8 + (dx) * 16;
        int centerz = 8 + (dz) * 16;

        BlockState air = Blocks.AIR.getDefaultState();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = 0 ; x < 16 ; x++) {
            int xdist = Math.abs(x - centerx);
            if (xdist <= radius) {
                for (int z = 0; z < 16; z++) {
                    int zdist = Math.abs(z - centerz);
                    if (zdist <= radius) {
                        for (int y = centery - radius; y <= centery + radius; y++) {
                            pos.setPos(chunkX * 16 + x, y, chunkZ * 16 + z);
                            if ((!hollow) || y == centery - radius || y == centery + radius || xdist == radius || zdist == radius) {
                                world.setBlockState(pos, IFeature.select(states, random), 0);
                            } else {
                                world.setBlockState(pos, air, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isFeatureCenter(ISeedReader world, int chunkX, int chunkZ, long prime) {
        double factor = 0.05f;
        Random random = new Random((chunkX * prime + chunkZ * 343457327L) ^ world.getSeed());
        random.nextFloat();
        double value = random.nextFloat();
        return value < factor;
    }
}
