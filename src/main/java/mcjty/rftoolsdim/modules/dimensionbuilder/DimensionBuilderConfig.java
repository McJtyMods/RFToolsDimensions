package mcjty.rftoolsdim.modules.dimensionbuilder;

import net.neoforged.neoforge.common.ForgeConfigSpec;

public class DimensionBuilderConfig {

    public static final String SUB_CATEGORY_DIMENSION_BUILDER = "dimensionbuilder";

    public static ForgeConfigSpec.IntValue BUILDER_MAXENERGY;
    public static ForgeConfigSpec.IntValue BUILDER_RECEIVEPERTICK;

    public static ForgeConfigSpec.LongValue PHASEDFIELD_MAXENERGY;
    public static ForgeConfigSpec.LongValue PHASEDFIELD_RECEIVEPERTICK;
    public static ForgeConfigSpec.LongValue PHASEDFIELD_CONSUMEPERTICK;


    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimension Builder settings").push(SUB_CATEGORY_DIMENSION_BUILDER);

        BUILDER_MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension builder can hold")
                .defineInRange("generatorMaxRF", 10000000, 0, Integer.MAX_VALUE);
        BUILDER_RECEIVEPERTICK = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension builder can receive per side")
                .defineInRange("generatorMaxRF", 20000, 0, Integer.MAX_VALUE);

        PHASEDFIELD_MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the phased field generator item can hold")
                .defineInRange("phasedFieldMaxRF", 1000000L, 0, Long.MAX_VALUE);
        PHASEDFIELD_RECEIVEPERTICK = SERVER_BUILDER
                .comment("RF per tick that the phased field generator item can receive")
                .defineInRange("phasedFieldRFPerTick", 1000L, 0, Long.MAX_VALUE);
        PHASEDFIELD_CONSUMEPERTICK = SERVER_BUILDER
                .comment("RF per tick that the phased field generator item will consume")
                .defineInRange("phasedFieldConsumePerTick", 100L, 0, Long.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
