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

public class GridGenerator {

    public static CompletableFuture<ChunkAccess> fillFromNoiseGrid(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        ChunkPos chunkpos = chunkAccess.getPos();

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        Heightmap hmOcean = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap hmWorld = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        BlockState defaultBlock = generator.getDefaultBlock();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                if (realx % 32 == 0 && realz % 32 == 0) {
                    for (int y = chunkAccess.getMinBuildHeight() ; y < chunkAccess.getMaxBuildHeight() ; y++) {
                        chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                    }
                    hmOcean.update(x, chunkAccess.getMaxBuildHeight()-1, z, defaultBlock);
                    hmWorld.update(x, chunkAccess.getMaxBuildHeight()-1, z, defaultBlock);
                } else if (realx % 32 == 0 || realz % 32 == 0) {
                    for (int y = chunkAccess.getMinBuildHeight() ; y < chunkAccess.getMaxBuildHeight() ; y += 32) {
                        chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                        hmOcean.update(x, y, z, defaultBlock);
                        hmWorld.update(x, y, z, defaultBlock);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    public static int getBaseHeightGrid(int pX, int pZ, LevelHeightAccessor level) {
        if (pX % 32 == 0 && pZ % 32 == 0) {
            return level.getMaxBuildHeight()-1;
        } else if (pX % 32 == 0 || pZ % 32 == 0) {
            return level.getMaxBuildHeight()-32;
        } else {
            return 0;
        }
    }

    @NotNull
    public static NoiseColumn getBaseColumnGrid(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        // @todo not entirely correct
        BlockState[] states = new BlockState[getBaseHeightGrid(pX, pZ, level)];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }
}
