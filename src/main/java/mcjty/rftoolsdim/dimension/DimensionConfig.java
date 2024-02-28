package mcjty.rftoolsdim.dimension;

import net.neoforged.neoforge.common.ForgeConfigSpec;

import static mcjty.rftoolsdim.setup.Config.SERVER_BUILDER;

public class DimensionConfig {

    public static final String SUB_CATEGORY_DIMENSION = "dimensions";

    public static ForgeConfigSpec.LongValue POWER_MULTIPLES;
    public static ForgeConfigSpec.IntValue MIN_POWER_THRESSHOLD;
    public static ForgeConfigSpec.IntValue MAX_POWER_THRESSHOLD;
    public static ForgeConfigSpec.LongValue MAX_DIMENSION_POWER_MIN;
    public static ForgeConfigSpec.LongValue MAX_DIMENSION_POWER_MAX;

    public static ForgeConfigSpec.BooleanValue ENABLE_DYNAMIC_PHASECOST;
    public static ForgeConfigSpec.DoubleValue DYNAMIC_PHASECOST_AMOUNT;
    public static ForgeConfigSpec.BooleanValue PHASED_FIELD_GENERATOR_DEBUF;

    public static ForgeConfigSpec.IntValue DIMPOWER_WARN0;     // This is only used for darkness calculations.
    public static ForgeConfigSpec.IntValue DIMPOWER_WARN1;
    public static ForgeConfigSpec.IntValue DIMPOWER_WARN2;
    public static ForgeConfigSpec.IntValue DIMPOWER_WARN3;

    public static ForgeConfigSpec.BooleanValue OWNER_DIMLET_REQUIRED;

    public static ForgeConfigSpec.DoubleValue RANDOMIZED_DIMLET_COST_FACTOR;
    public static ForgeConfigSpec.DoubleValue DIMLET_HUT_CHANCE;


    public static void init() {
        SERVER_BUILDER.comment("Dimension settings").push(SUB_CATEGORY_DIMENSION);

        RANDOMIZED_DIMLET_COST_FACTOR = SERVER_BUILDER
                .comment("The maintenance cost of randomized dimlets is multiplied with this value before applying to the dimension")
                .defineInRange("randomizedDimletCostFactor", 0.1, 0, 10.0);

        DIMLET_HUT_CHANCE = SERVER_BUILDER
                .comment("The chance of a dimlet hut for a given chunk")
                .defineInRange("dimletHutChance", 0.005, 0, 1.0);

        MIN_POWER_THRESSHOLD = SERVER_BUILDER
                .comment("At this maintenance cost thresshold and below the minimum dimension power (dimensionPowerMinimum is used")
                .defineInRange("minPowerThresshold", 100, 0, Integer.MAX_VALUE);
        MAX_POWER_THRESSHOLD = SERVER_BUILDER
                .comment("At this maintenance cost thresshold and above the maximum dimension power (dimensionPowerMaximum is used")
                .defineInRange("maxPowerThresshold", 5000, 0, Integer.MAX_VALUE);
        MAX_DIMENSION_POWER_MIN = SERVER_BUILDER
                .comment("Maximum power in a dimension. This is the minimum value used by dimensions that don't consume a lot of power")
                .defineInRange("dimensionPowerMinimum", 20000000L, 0, Long.MAX_VALUE);
        MAX_DIMENSION_POWER_MAX = SERVER_BUILDER
                .comment("Maximum power in a dimension. This is the maximum value used by dimensions that consume a lot of power")
                .defineInRange("dimensionPowerMaximum", 40000000L, 0, Long.MAX_VALUE);
        POWER_MULTIPLES = SERVER_BUILDER
                .comment("Maximum power of a dimension is always a multiple of this value")
                .defineInRange("powerMultiples", 500000L, 1, Long.MAX_VALUE);

        DIMPOWER_WARN0 = SERVER_BUILDER
                .comment("The zero power percentage at which power warning signs are starting to happen. This is only used for lighting level. No other debuffs occur at this level.")
                .defineInRange("dimensionPowerWarn0", 12, 0, 100);

        DIMPOWER_WARN1 = SERVER_BUILDER
                .comment("The first power percentage at which power warning signs are starting to happen")
                .defineInRange("dimensionPowerWarn1", 9, 0, 100);

        DIMPOWER_WARN2 = SERVER_BUILDER
                .comment("The second power percentage at which power warning signs are starting to become worse")
                .defineInRange("dimensionPowerWarn2", 2, 0, 100);

        DIMPOWER_WARN3 = SERVER_BUILDER
                .comment("The third power percentage at which power warning signs are starting to be very bad")
                .defineInRange("dimensionPowerWarn3", 1, 0, 100);


        ENABLE_DYNAMIC_PHASECOST = SERVER_BUILDER
                .comment("Enable dynamic scaling of the Phase Field Generator cost based on world tick cost")
                .define("enableDynamicPhaseCost", false);

        DYNAMIC_PHASECOST_AMOUNT = SERVER_BUILDER
                .comment("How much of the tick cost of the world is applied to the PFG cost, as a ratio from 0 to 1")
                .defineInRange("dynamicPhaseCostAmount", 0.05f, 0, 1);

        PHASED_FIELD_GENERATOR_DEBUF = SERVER_BUILDER
                .comment("If true you will get some debufs when the PFG is in use. If false there will be no debufs")
                .define("phasedFieldGeneratorDebuf", true);

        OWNER_DIMLET_REQUIRED = SERVER_BUILDER
                .comment("If true creating dimensions requires an owner dimlet")
                .define("ownerDimletRequired", false);

        SERVER_BUILDER.pop();
    }

}
