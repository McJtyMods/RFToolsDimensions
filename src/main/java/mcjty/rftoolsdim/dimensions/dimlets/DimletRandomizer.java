package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import net.minecraft.init.Blocks;

import java.util.Random;

public class DimletRandomizer {

    public static final int RARITY_0 = 0;
    public static final int RARITY_1 = 1;
    public static final int RARITY_2 = 2;
    public static final int RARITY_3 = 3;
    public static final int RARITY_4 = 4;
    public static final int RARITY_5 = 5;
    public static final int RARITY_6 = 6;

    public static DimletKey getRandomFeature(Random random, boolean allowWorldgen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_FEATURE, FeatureType.FEATURE_NONE.getId());
    }

    public static DimletKey getRandomFluidBlock(Random random, boolean allowWorldgen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_LIQUID, Blocks.water.getRegistryName()+"_0");
    }

    public static DimletKey getRandomMaterialBlock(Random random, boolean allowWorldgen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_MATERIAL, Blocks.stone.getRegistryName()+"_0");
    }

}
