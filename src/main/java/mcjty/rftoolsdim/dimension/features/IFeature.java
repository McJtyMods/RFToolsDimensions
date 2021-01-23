package mcjty.rftoolsdim.dimension.features;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.List;
import java.util.Random;

public interface IFeature {
    boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos,
                     List<BlockState> states, long prime);

    static BlockState select(List<BlockState> states, Random random) {
        if (states.size() == 1) {
            return states.get(0);
        } else {
            return states.get(random.nextInt(states.size()));
        }
    }
}
