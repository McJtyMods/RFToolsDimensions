package mcjty.rftoolsdim.dimension.features.instances;

import mcjty.rftoolsdim.dimension.features.IFeature;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.List;
import java.util.Random;

public record SphereFeature(boolean hollow, boolean liquid) implements IFeature {

    @Override
    public boolean generate(WorldGenLevel reader, ChunkGenerator generator, RandomSource rand, BlockPos pos,
                            List<BlockState> states, List<BlockState> liquids, long prime) {
        ChunkPos cp = new ChunkPos(pos);
        int chunkX = cp.x;
        int chunkZ = cp.z;
        int size = 1;

        BlockState filler = Blocks.AIR.defaultBlockState();

        boolean generated = false;
        for (int dx = -size; dx <= size; dx++) {
            int cx = chunkX + dx;
            for (int dz = -size; dz <= size; dz++) {
                int cz = chunkZ + dz;
                if (isFeatureCenter(reader, cx, cz, prime)) {
                    if (liquid) {
                        int index = getCenteredIndex(reader, cx, cz, prime, liquids.size());
                        filler = liquids.get(index);
                    }
                    generate(reader, chunkX, chunkZ, dx, dz, states, filler, prime);
                    generated = true;
                }
            }
        }
        return generated;
    }

    private void generate(WorldGenLevel world, int chunkX, int chunkZ, int dx, int dz,
                          List<BlockState> states, BlockState filler, long prime) {
        long seeder = world.getSeed() + (chunkZ + dz) * 256203221L + (chunkX + dx) * prime;
        RandomSource random = new LegacyRandomSource(seeder);
        random.nextFloat();
        int radius = random.nextInt(12) + 9;
        int centery = random.nextInt(60) + 40;

        int centerx = 8 + (dx) * 16;
        int centerz = 8 + (dz) * 16;
        double sqradius = radius * radius;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            double dxdx = (x - centerx) * (x - centerx);
            for (int z = 0; z < 16; z++) {
                double dzdz = (z - centerz) * (z - centerz);
                for (int y = centery - radius; y <= centery + radius; y++) {
                    double dydy = (y - centery) * (y - centery);
                    double sqdist = dxdx + dydy + dzdz;
                    if (sqdist <= sqradius) {
                        pos.set(chunkX * 16 + x, y, chunkZ * 16 + z);
                        if ((!hollow) || Math.sqrt(sqdist) >= radius - 2) {
                            world.setBlock(pos, IFeature.select(states, random), 0);
                        } else {
                            world.setBlock(pos, filler, 0);
                        }
                    }
                }
            }
        }
    }

    private static int getCenteredIndex(WorldGenLevel world, int chunkX, int chunkZ, long prime, int max) {
        if (max == 1) {
            return 0;
        }
        Random random = new Random((chunkX * 3399018867L + chunkZ * prime) ^ world.getSeed());
        random.nextFloat();
        return random.nextInt(max);
    }

    private static boolean isFeatureCenter(WorldGenLevel world, int chunkX, int chunkZ, long prime) {
        double factor = 0.05f;
        Random random = new Random((chunkX * prime + chunkZ * 3399018867L) ^ world.getSeed());
        random.nextFloat();
        double value = random.nextFloat();
        return value < factor;
    }
}
