package mcjty.rftoolsdim.dimensions.dimlets;

import com.sun.istack.internal.NotNull;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class DimletObjectMapping {

    @NotNull
    public static FeatureType getFeature(DimletKey dimlet) {
        if (DimletType.DIMLET_FEATURE.equals(dimlet.getType())) {
            FeatureType type = FeatureType.getFeatureById(dimlet.getId());
            return type == null ? FeatureType.FEATURE_NONE : type;
        }
        return FeatureType.FEATURE_NONE;
    }

    public static IBlockState getBlock(DimletKey dimlet) {
        if (DimletType.DIMLET_MATERIAL.equals(dimlet.getType())) {
            String id = dimlet.getId();
            int lastIndexOf = StringUtils.lastIndexOf(id, "_");
            String blockid = id.substring(0, lastIndexOf-1);
            int meta = Integer.parseInt(id.substring(lastIndexOf+1));

            Block block = Block.blockRegistry.getObject(new ResourceLocation(blockid));
            if (block == null) {
                return null;
            }
            return block.getStateFromMeta(meta);
        }
        return null;
    }

    public static Block getFluid(DimletKey dimlet) {
        if (DimletType.DIMLET_LIQUID.equals(dimlet.getType())) {
            String id = dimlet.getId();
            int lastIndexOf = StringUtils.lastIndexOf(id, "_");
            String blockid = id.substring(0, lastIndexOf-1);
//            int meta = Integer.parseInt(id.substring(lastIndexOf+1));

            Block block = Block.blockRegistry.getObject(new ResourceLocation(blockid));
            if (block == null) {
                return null;
            }
            return block;
        }
        return null;
    }

}
