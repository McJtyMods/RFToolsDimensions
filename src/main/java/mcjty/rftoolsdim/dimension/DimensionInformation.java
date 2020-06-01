package mcjty.rftoolsdim.dimension;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is derived from the DimensionDescriptor and gives a more optimal in-game representation of that
 */
public class DimensionInformation {

    private List<BlockState> baseBlocks = new ArrayList<>();

    private DimensionInformation() {

    }

    public List<BlockState> getBaseBlocks() {
        return baseBlocks;
    }

    public static DimensionInformation createFrom(DimensionDescriptor descriptor) {
        DimensionInformation info = new DimensionInformation();

        for (ResourceLocation baseBlock : descriptor.getBaseBlocks()) {
            Block block = ForgeRegistries.BLOCKS.getValue(baseBlock);
            if (block == null) {
                // @todo proper logging
                System.out.println("Can't find base block '" + baseBlock.toString() + "'!");
            } else {
                info.baseBlocks.add(block.getDefaultState());
            }
        }

        return info;
    }
}
