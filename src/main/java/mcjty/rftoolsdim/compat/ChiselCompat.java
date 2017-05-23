package mcjty.rftoolsdim.compat;

import net.minecraft.block.state.IBlockState;
import team.chisel.api.block.ICarvable;
import team.chisel.api.block.VariationData;

public class ChiselCompat {

    public static String getReadableName(IBlockState state) {
        if (state.getBlock() instanceof ICarvable) {
            ICarvable carvable = (ICarvable) state.getBlock();
            int variationIndex = carvable.getVariationIndex(state);
            VariationData variationData = carvable.getVariationData(variationIndex);
            return variationData.name;
        }
        return null;
    }

}
