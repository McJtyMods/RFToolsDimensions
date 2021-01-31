package mcjty.rftoolsdim.dimension;

import net.minecraftforge.common.ForgeConfigSpec;

import static mcjty.rftoolsdim.setup.Config.SERVER_BUILDER;

public class DimensionConfig {

    public static final String SUB_CATEGORY_DIMENSION = "dimensions";

    public static ForgeConfigSpec.LongValue MAX_DIMENSION_POWER;
    public static ForgeConfigSpec.BooleanValue ENABLE_DYNAMIC_PHASECOST;
    public static ForgeConfigSpec.DoubleValue DYNAMIC_PHASECOST_AMOUNT;
    public static ForgeConfigSpec.BooleanValue PHASED_FIELD_GENERATOR_DEBUF;

    public static ForgeConfigSpec.LongValue DIMPOWER_WARN0;     // This is only used for darkness calculations.
    public static ForgeConfigSpec.LongValue DIMPOWER_WARN1;
    public static ForgeConfigSpec.LongValue DIMPOWER_WARN2;
    public static ForgeConfigSpec.LongValue DIMPOWER_WARN3;


    public static void init() {
        SERVER_BUILDER.comment("Dimension settings").push(SUB_CATEGORY_DIMENSION);

        MAX_DIMENSION_POWER = SERVER_BUILDER
                .comment("Maximum power in a dimension")
                .defineInRange("maxDimensionPower", 40000000L, 0, Long.MAX_VALUE);

        DIMPOWER_WARN0 = SERVER_BUILDER
                .comment("The zero level at which power warning signs are starting to happen. This is only used for lighting level. No other debuffs occur at this level.")
                .defineInRange("dimensionPowerWarn0", 6000000L, 0, Long.MAX_VALUE);

        DIMPOWER_WARN1 = SERVER_BUILDER
                .comment("The first level at which power warning signs are starting to happen")
                .defineInRange("dimensionPowerWarn1", 4000000L, 0, Long.MAX_VALUE);

        DIMPOWER_WARN2 = SERVER_BUILDER
                .comment("The second level at which power warning signs are starting to become worse")
                .defineInRange("dimensionPowerWarn2", 1000000L, 0, Long.MAX_VALUE);

        DIMPOWER_WARN3 = SERVER_BUILDER
                .comment("The third level at which power warning signs are starting to be very bad")
                .defineInRange("dimensionPowerWarn3", 500000L, 0, Long.MAX_VALUE);


        ENABLE_DYNAMIC_PHASECOST = SERVER_BUILDER
                .comment("Enable dynamic scaling of the Phase Field Generator cost based on world tick cost")
                .define("enableDynamicPhaseCost", false);

        DYNAMIC_PHASECOST_AMOUNT = SERVER_BUILDER
                .comment("How much of the tick cost of the world is applied to the PFG cost, as a ratio from 0 to 1")
                .defineInRange("dynamicPhaseCostAmount", 0.05f, 0, 1);

        PHASED_FIELD_GENERATOR_DEBUF = SERVER_BUILDER
                .comment("If true you will get some debufs when the PFG is in use. If false there will be no debufs")
                .define("phasedFieldGeneratorDebuf", true);


        SERVER_BUILDER.pop();
    }

}
