package mcjty.rftoolsdim.dimension.terraintypes.generators;

import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.core.BlockPos;
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

public class MazeGenerator {

    public static CompletableFuture<ChunkAccess> fillFromNoise(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        BlockState defaultBlock = generator.getDefaultBlock();
        long seed = generator.getSeed();

        for (int cy = 0 ; cy < 256 ; cy += 16) {
            boolean isTopOpen = isTopOpen(chunkpos.x, cy, chunkpos.z, seed);
            boolean isBottomOpen = cy != 0 && isTopOpen(chunkpos.x, cy - 16, chunkpos.z, seed);
            boolean isEastOpen = isEastOpen(chunkpos.x, cy, chunkpos.z, seed);
            boolean isWestOpen = isEastOpen(chunkpos.x-1, cy, chunkpos.z, seed);
            boolean isSouthOpen = isSouthOpen(chunkpos.x, cy, chunkpos.z, seed);
            boolean isNorthOpen = isSouthOpen(chunkpos.x, cy, chunkpos.z-1, seed);
            for (int y = cy ; y < cy+4 ; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (!isBottomOpen || x < 4 || z < 4 || x > 11 || z > 11) {
                            chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                            hmOcean.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                            hmWorld.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                        }
                    }
                }
            }
            for (int y = cy+4 ; y < cy+11 ; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (x < 4) {
                            if (!isWestOpen || z < 4 || z > 11) {
                                chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                                hmOcean.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                                hmWorld.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                            }
                        } else if (x > 11) {
                            if (!isEastOpen || z < 4 || z > 11) {
                                chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                                hmOcean.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                                hmWorld.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                            }
                        } else if (z < 4) {
                            if (!isNorthOpen) {
                                chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                                hmOcean.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                                hmWorld.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                            }
                        } else if (z > 11) {
                            if (!isSouthOpen) {
                                chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                                hmOcean.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                                hmWorld.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                            }
                        }
                    }
                }
            }
            for (int y = cy+11 ; y < cy+16 ; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (!isTopOpen || x < 4 || z < 4 || x > 11 || z > 11) {
                            chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                            hmOcean.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                            hmWorld.update(x, chunkAccess.getMaxBuildHeight() - 1, z, defaultBlock);
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    private static boolean isTopOpen(int cx, int cy, int cz, long seed) {
        Random random = new Random((cx * 699891043L + cy * 944690531L + cz * 16670002577L) ^ seed);
        random.nextFloat();
        return random.nextFloat() < .3;
    }

    private static boolean isSouthOpen(int cx, int cy, int cz, long seed) {
        Random random = new Random((cx * 944690531L + cy * 34324326931L + cz * 31415927161L) ^ seed);
        random.nextFloat();
        return random.nextFloat() < .7;
    }

    private static boolean isEastOpen(int cx, int cy, int cz, long seed) {
        Random random = new Random((cx * 217185461L + cy * 9899947L + cz * 342432435349L) ^ seed);
        random.nextFloat();
        return random.nextFloat() < .7;
    }

    public static int getBaseHeight(int pX, int pZ, LevelHeightAccessor level) {
        return 256;
    }

    @NotNull
    public static NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        BlockState[] states = new BlockState[-level.getMinBuildHeight() + getBaseHeight(pX, pZ, level)];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }
}
