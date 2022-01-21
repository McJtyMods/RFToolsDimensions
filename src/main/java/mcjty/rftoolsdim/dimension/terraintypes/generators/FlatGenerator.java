package mcjty.rftoolsdim.dimension.terraintypes.generators;

import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class FlatGenerator {

    public static final int FLATHEIGHT = 120;

    @NotNull
    public static CompletableFuture<ChunkAccess> fillFromNoise(ChunkAccess chunkAccess, RFToolsChunkGenerator generator) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockState defaultBlock = generator.getDefaultBlock();

        for (int y = chunkAccess.getMinBuildHeight(); y < FLATHEIGHT; ++y) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    chunkAccess.setBlockState(mpos.set(x, y, z), defaultBlock, false);
                    heightmap.update(x, y, z, defaultBlock);
                    heightmap1.update(x, y, z, defaultBlock);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @NotNull
    public static NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor level, RFToolsChunkGenerator generator) {
        BlockState[] states = new BlockState[FLATHEIGHT -1];
        Arrays.fill(states, generator.getDefaultBlock());
        return new NoiseColumn(level.getMinBuildHeight(), states);
    }
}
