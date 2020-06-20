package mcjty.rftoolsdim.modules.dimensionbuilder;

import net.minecraftforge.common.ForgeConfigSpec;

public class DimensionBuilderConfig {

    public static final String SUB_CATEGORY_DIMENSION_BUILDER = "dimensionbuilder";

    public static ForgeConfigSpec.IntValue MAXENERGY;
    public static ForgeConfigSpec.IntValue RECEIVEPERTICK;

    public static void setup(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Dimension Builder settings").push(SUB_CATEGORY_DIMENSION_BUILDER);

        MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension builder can hold")
                .defineInRange("generatorMaxRF", 10000000, 0, Integer.MAX_VALUE);
        RECEIVEPERTICK = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension builder can receive per side")
                .defineInRange("generatorMaxRF", 20000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
