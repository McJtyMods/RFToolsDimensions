package mcjty.rftoolsdim.dimension.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class SpheresFeature extends Feature<NoFeatureConfig> {

    public SpheresFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        ChunkPos cp = new ChunkPos(pos);
        int chunkX = cp.x;
        int chunkZ = cp.z;
        int size = 1;

        for (int dx = -size; dx <= size; dx++) {
            int cx = chunkX + dx;
            for (int dz = -size; dz <= size; dz++) {
                int cz = chunkZ + dz;
                if (isFeatureCenter(reader, cx, cz)) {
                    generate(reader, chunkX, chunkZ, dx, dz);
                }
            }
        }
        return true;
    }

    private void generate(ISeedReader world, int chunkX, int chunkZ, int dx, int dz) {
        Random random = new Random(world.getSeed() + (chunkZ+dz) * 256203221L + (chunkX+dx) * 899809363L);
        random.nextFloat();
        int radius = random.nextInt(12) + 9;
        int centery = random.nextInt(60) + 40;

        BlockState block = Blocks.DIAMOND_BLOCK.getDefaultState();
        int centerx = 8 + (dx) * 16;
        int centerz = 8 + (dz) * 16;
        double sqradius = radius * radius;

        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = 0 ; x < 16 ; x++) {
            double dxdx = (x-centerx) * (x-centerx);
            for (int z = 0 ; z < 16 ; z++) {
                double dzdz = (z-centerz) * (z-centerz);
                for (int y = centery-radius ; y <= centery+radius ; y++) {
                    double dydy = (y-centery) * (y-centery);
                    double sqdist = dxdx + dydy + dzdz;
                    if (sqdist <= sqradius) {
                        world.setBlockState(pos.setPos(chunkX * 16 + x, y, chunkZ * 16 + z), block, 0);
                    }
                }
            }
        }
    }

    private static boolean isFeatureCenter(IWorld world, int chunkX, int chunkZ) {
        double factor = 0.05f;
        Random random = new Random(chunkX * 3347 + chunkZ * 3399018867L);   // @todo check primes?
        random.nextFloat();
        double value = random.nextFloat();
//        System.out.println((value < factor ? "YES" : "no ") + "  chunkX = " + chunkX + "," + chunkZ + "    factor=" + factor +", value=" + value);
        return value < factor;
    }
}
