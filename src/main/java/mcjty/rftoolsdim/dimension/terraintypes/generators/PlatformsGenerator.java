package mcjty.rftoolsdim.dimension.terraintypes.generators;

import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class PlatformsGenerator {
    public static CompletableFuture<ChunkAccess> fillFromNoise(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        long seed = generator.getSeed();
        BlockState defaultBlock = generator.getDefaultBlock();

        for (int dx = -3 ; dx <= 3 ; dx++) {
            for (int dz = -3 ; dz <= 3 ; dz++) {
                int platx = (chunkpos.x + dx) * 16 + 8;
                int platz = (chunkpos.z + dz) * 16 + 8;

                int radius = getPlatformRadius(chunkpos.x + dx, chunkpos.z + dz, seed);
                int height = getPlatformHeight(chunkAccess, chunkpos.x + dx, chunkpos.z + dz, seed);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int realx = chunkpos.x * 16 + x;
                        int realz = chunkpos.z * 16 + z;
                        if (Math.abs(platx-realx) <= radius && Math.abs(platz-realz) <= radius) {
                            chunkAccess.setBlockState(mpos.set(x, height, z), defaultBlock, false);
                            hmOcean.update(x, height, z, defaultBlock);
                            hmWorld.update(x, height, z, defaultBlock);
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    private static int getPlatformRadius(int cx, int cz, long seed) {
        Random random = new Random((cx * 996646177L + cz * 3996645049L) ^ seed);
        random.nextFloat();
        return random.nextInt(20) + 10;
    }

    private static int getPlatformHeight(LevelHeightAccessor accessor, int cx, int cz, long seed) {
        Random random = new Random((cx * 65657771L + cz * 56548073L) ^ seed);
        random.nextFloat();
        return random.nextInt(accessor.getMaxBuildHeight() - accessor.getMinBuildHeight() - 100) + accessor.getMinBuildHeight() + 40;
    }

    public static int getBaseHeight(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        int cx = SectionPos.blockToSectionCoord(pX);
        int cz = SectionPos.blockToSectionCoord(pZ);
        int curheight = 0;
        for (int dx = -3 ; dx <= 3 ; dx++) {
            for (int dz = -3 ; dz <= 3 ; dz++) {
                long seed = generator.getSeed();
                int height = getPlatformHeight(level, cx + dx, cz + dz, seed);
                if (height > curheight) {
                    int radius = getPlatformRadius(cx + dx, cz + dz, seed);
                    int platx = (cx + dx) * 16 + 8;
                    int platz = (cz + dz) * 16 + 8;
                    curheight = findHeight(cx, cz, curheight, platx, platz, radius, height);
                }
            }
        }
        return curheight;
    }

    private static int findHeight(int cx, int cz, int curheight, int platx, int platz, int radius, int height) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = cx * 16 + x;
                int realz = cz * 16 + z;
                if (Math.abs(platx -realx) <= radius && Math.abs(platz -realz) <= radius) {
                    if (height > curheight) {
                        return height;
                    }
                }
            }
        }
        return curheight;
    }

    @NotNull
    public static NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        // @todo not entirely correct
        BlockState[] states = new BlockState[getBaseHeight(pX, pZ, level, generator)];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }
}
