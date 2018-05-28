package mcjty.rftoolsdim.config;

import net.minecraftforge.common.config.Configuration;

public class PowerConfiguration {
    public static final String CATEGORY_POWER = "power";

    public static long MAX_DIMENSION_POWER = 40000000;
    public static int DIMPOWER_WARN0 = 6000000;     // This is only used for darkness calculations.
    public static int DIMPOWER_WARN1 = 4000000;
    public static int DIMPOWER_WARN2 = 1000000;
    public static int DIMPOWER_WARN3 = 500000;
    public static int DIMPOWER_WARN_TP = 500000;    // Warn level for teleporter device.
    public static int dimensionDifficulty = 1;      // -1 == whimpy, 0 == easy, 1 == normal
    public static boolean freezeUnpowered = true;   // Freeze all entities and TE's in an unpowered dimension.
    public static boolean preventSpawnUnpowered = true; // Prevent spawns in unpowered dimensions
    public static float afterCreationCostFactor = 0.1f;
    public static float maintenanceCostPercentage = 0.0f;   // Bonus percentage in the dimlet cost.
    public static int minimumCostPercentage = 10;   // Bonus dimlets (efficiency and related) can at most reduce cost to 10% by default
    public static int PHASEDFIELD_MAXENERGY = 1000000;
    public static int PHASEDFIELD_RECEIVEPERTICK = 1000;
    public static int PHASEDFIELD_CONSUMEPERTICK = 100;
    public static int phasedFieldGeneratorRange = 5;
    public static boolean phasedFieldGeneratorDebuf = true;


    public static void init(Configuration cfg) {
        PHASEDFIELD_MAXENERGY = cfg.get(CATEGORY_POWER, "phasedFieldMaxRF", PowerConfiguration.PHASEDFIELD_MAXENERGY,
                                                           "Maximum RF storage that the phased field generator item can hold").getInt();
        PHASEDFIELD_RECEIVEPERTICK = cfg.get(CATEGORY_POWER, "phasedFieldRFPerTick", PowerConfiguration.PHASEDFIELD_RECEIVEPERTICK,
                                                                "RF per tick that the phased field generator item can receive").getInt();
        PHASEDFIELD_CONSUMEPERTICK = cfg.get(CATEGORY_POWER, "phasedFieldConsumePerTick", PowerConfiguration.PHASEDFIELD_CONSUMEPERTICK,
                                                                "RF per tick that the phased field generator item will consume").getInt();
        phasedFieldGeneratorRange = cfg.get(CATEGORY_POWER, "phasedFieldGeneratorRange", PowerConfiguration.phasedFieldGeneratorRange,
                                                               "In this range the PFG will keep entities active (set to 0 to disable this feature)").getInt();
        phasedFieldGeneratorDebuf = cfg.get(CATEGORY_POWER, "phasedFieldGeneratorDebuf", PowerConfiguration.phasedFieldGeneratorDebuf,
                                                               "If true you will get some debufs when the PFG is in use. If false there will be no debufs").getBoolean();

        MAX_DIMENSION_POWER = cfg.get(CATEGORY_POWER, "dimensionPower", PowerConfiguration.MAX_DIMENSION_POWER,
                                                         "The internal RF buffer for every dimension").getLong();
        DIMPOWER_WARN0 = cfg.get(CATEGORY_POWER, "dimensionPowerWarn0", PowerConfiguration.DIMPOWER_WARN0,
                                                    "The zero level at which power warning signs are starting to happen. This is only used for lighting level. No other debuffs occur at this level.").getInt();
        DIMPOWER_WARN1 = cfg.get(CATEGORY_POWER, "dimensionPowerWarn1", PowerConfiguration.DIMPOWER_WARN1,
                                                    "The first level at which power warning signs are starting to happen").getInt();
        DIMPOWER_WARN2 = cfg.get(CATEGORY_POWER, "dimensionPowerWarn2", PowerConfiguration.DIMPOWER_WARN2,
                                                    "The second level at which power warning signs are starting to become worse").getInt();
        DIMPOWER_WARN3 = cfg.get(CATEGORY_POWER, "dimensionPowerWarn3", PowerConfiguration.DIMPOWER_WARN3,
                                                    "The third level at which power warning signs are starting to be very bad").getInt();
        DIMPOWER_WARN_TP = cfg.get(CATEGORY_POWER, "dimensionPowerWarnTP", PowerConfiguration.DIMPOWER_WARN_TP,
                                                      "The level at which the teleportation system will consider a destination to be dangerous").getInt();

        afterCreationCostFactor = (float) cfg.get(CATEGORY_POWER, "afterCreationCostFactor", PowerConfiguration.afterCreationCostFactor,
                                                                     "If the dimension turns out to be more expensive after creation you get a factor of the actual cost extra to the RF/tick maintenance cost. If this is 0 there is no such cost. If this is 1 then you get the full cost").getDouble();
        maintenanceCostPercentage = (float) cfg.get(CATEGORY_POWER, "maintenanceCostPercentage", PowerConfiguration.maintenanceCostPercentage,
                                                                       "Percentage to add or subtract to the maintenance cost of all dimlets (100 would double the cost, -100 would set the cost to almost zero (complete zero is not allowed))").getDouble();
        minimumCostPercentage = cfg.get(CATEGORY_POWER, "minimumCostPercentage", PowerConfiguration.minimumCostPercentage,
                                                           "Bonus dimlets can never get the maintenance cost of a dimension below this percentage of the nominal cost without bonus dimlets").getInt();

        dimensionDifficulty = cfg.get(CATEGORY_POWER, "difficulty", PowerConfiguration.dimensionDifficulty,
                                                         "Difficulty level for the dimension system. -1 means dimensions don't consume power. 0 means that you will not get killed but kicked out of the dimension when it runs out of power. 1 means certain death").getInt();
        freezeUnpowered = cfg.get(CATEGORY_POWER, "freezeUnpoweredDimension", PowerConfiguration.freezeUnpowered,
                                                     "If this flag is true RFTools will freeze all entities and machines in a dimension when the power runs out").getBoolean();
        preventSpawnUnpowered = cfg.get(CATEGORY_POWER, "preventSpawnUnpoweredDimension", PowerConfiguration.preventSpawnUnpowered,
                                                           "If this flag is true all spawns will be disabled in an unpowered dimension").getBoolean();
    }

}
