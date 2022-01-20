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
import java.util.concurrent.CompletableFuture;

public class WavesGenerator {

    public static final int WAVE_BASE = 80;

    @NotNull
    public static CompletableFuture<ChunkAccess> fillFromNoiseWaves(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        BlockState defaultBlock = generator.getDefaultBlock();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = calculateWaveHeight(realx, realz);
                for (int y = chunkAccess.getMinBuildHeight() ; y < height ; y++) {
                    chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                    hmOcean.update(x, y, z, defaultBlock);
                    hmWorld.update(x, y, z, defaultBlock);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    public static int calculateWaveHeight(int realx, int realz) {
        return (int) (WAVE_BASE + Math.sin(realx / 20.0f) * 10 + Math.cos(realz / 20.0f) * 10);
    }

    @NotNull
    public static NoiseColumn getBaseColumnWaves(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        BlockState[] states = new BlockState[calculateWaveHeight(pX, pZ)];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }
}
