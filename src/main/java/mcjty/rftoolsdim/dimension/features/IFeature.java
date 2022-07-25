package mcjty.rftoolsdim.dimension.features;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.List;

public interface IFeature {
    boolean generate(WorldGenLevel reader, ChunkGenerator generator, RandomSource rand, BlockPos pos,
                     List<BlockState> states, List<BlockState> liquids, long prime);

    static BlockState select(List<BlockState> states, RandomSource random) {
        if (states.size() == 1) {
            return states.get(0);
        } else {
            return states.get(random.nextInt(states.size()));
        }
    }
}
