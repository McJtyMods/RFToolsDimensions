package mcjty.rftoolsdim.dimension;

import net.minecraftforge.common.ForgeConfigSpec;

import static mcjty.rftoolsdim.setup.Config.SERVER_BUILDER;

public class DimensionConfig {

    public static final String SUB_CATEGORY_DIMENSION = "dimensions";

    public static ForgeConfigSpec.LongValue MAX_DIMENSION_POWER;

    public static void init() {
        SERVER_BUILDER.comment("Dimension settings").push(SUB_CATEGORY_DIMENSION);

        MAX_DIMENSION_POWER = SERVER_BUILDER
                .comment("Maximum power in a dimension")
                .defineInRange("maxDimensionPower", 40000000L, 0, Long.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
