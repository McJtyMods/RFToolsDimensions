package mcjty.rftoolsdim.dimension.terraintypes.generators;

import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.tools.PerlinNoiseGenerator14;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RavineGenerator {

    @NotNull
    public static CompletableFuture<ChunkAccess> fillFromNoise(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        PerlinNoiseGenerator14 noise = generator.getPerlinNoise();

        BlockState defaultBlock = generator.getDefaultBlock();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = calculateHeight(realx, realz, noise);
                for (int y = chunkAccess.getMinBuildHeight() ; y < height ; y++) {
                    chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                    hmOcean.update(x, y, z, defaultBlock);
                    hmWorld.update(x, y, z, defaultBlock);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    public static int getBaseHeight(int pX, int pZ, RFToolsChunkGenerator generator) {
        return calculateHeight(pX, pZ, generator.getPerlinNoise());
    }

    private static int calculateHeight(int pX, int pZ, PerlinNoiseGenerator14 noise) {
        double value = (noise.getValue(pX/8.0, pZ/8.0)/14.0)+1;
        if (value < 0) {
            value = 0;
        }
        value *= 3;
        int az = Math.abs(pZ);
        if (az > 50) {
            if (az < 100) {
                value *= 1 + 29.0 * (az-50)/50;
            } else {
                value *= 30;
            }
        }
        return (int) value + 70;
    }

    @NotNull
    public static NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        BlockState[] states = new BlockState[calculateHeight(pX, pZ, generator.getPerlinNoise())];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }
}
