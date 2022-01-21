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

public class SpikesGenerator {

    @NotNull
    public static CompletableFuture<ChunkAccess> fillFromNoise(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        BlockState defaultBlock = generator.getDefaultBlock();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = calculateSpikeHeight(realx, realz, generator.getSeed());
                for (int y = chunkAccess.getMinBuildHeight() ; y < height ; y++) {
                    chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                    hmOcean.update(x, y, z, defaultBlock);
                    hmWorld.update(x, y, z, defaultBlock);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @NotNull
    public static NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        BlockState[] states = new BlockState[calculateSpikeHeight(pX, pZ, generator.getSeed())];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }

    public static int calculateSpikeHeight(int pX, int pZ, long seed) {
        Random random = new Random((pX * 65657771L + pZ * 56548073L) ^ seed);
        random.nextFloat();
        return random.nextInt(100) + 20;
    }
}
