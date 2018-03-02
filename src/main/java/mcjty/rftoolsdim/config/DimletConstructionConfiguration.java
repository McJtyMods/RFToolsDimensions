package mcjty.rftoolsdim.config;

import net.minecraftforge.common.config.Configuration;

public class DimletConstructionConfiguration {
    public static final String CATEGORY_DIMLET_CONSTRUCTION = "dimletconstruction";

    public static int WORKBENCH_MAXENERGY = 32000;
    public static int WORKBENCH_RECEIVEPERTICK = 80;
    public static int rfExtractOperation = 200;
    public static int maxBiomeAbsorbtion = 5000;    // Amount of ticks before a biome absorber is ready
    public static int maxTerrainAbsorbtion = 5000;  // Amount of ticks before a terrain absorber is ready
    public static int maxFeatureAbsorbtion = 5000;  // Amount of ticks before a feature absorber is ready
    public static int maxBlockAbsorbtion = 128;     // Amount of blocks to absorbe
    public static int maxTimeAbsorbtion = 10;       // Amount of time ticks to absorbe
    public static int maxLiquidAbsorbtion = 128;    // Amount of liquid blocks to absorbe

    public static void init(Configuration cfg) {
        WORKBENCH_MAXENERGY = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "dimletWorkbenchMaxRF", WORKBENCH_MAXENERGY,
                "Maximum RF storage that the dimlet workbench can hold").getInt();
        WORKBENCH_RECEIVEPERTICK = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "dimletWorkbenchRFPerTick", WORKBENCH_RECEIVEPERTICK,
                "RF per tick that the the dimlet workbench can receive").getInt();
        rfExtractOperation = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "dimletWorkbenchRFPerOperation", rfExtractOperation,
                "RF that the dimlet workbench needs for extracting one dimlet").getInt();

        maxBiomeAbsorbtion = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "maxBiomeAbsorbtion", maxBiomeAbsorbtion,
                                     "Amount of ticks needed to fully absorb a biome essence").getInt();
        maxTerrainAbsorbtion = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "maxTerrainAbsorbtion", maxTerrainAbsorbtion,
                                       "Amount of ticks needed to fully absorb a terrain essence").getInt();
        maxFeatureAbsorbtion = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "maxFeatureAbsorbtion", maxFeatureAbsorbtion,
                                       "Amount of ticks needed to fully absorb a feature essence").getInt();
        maxBlockAbsorbtion = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "maxBlockAbsorbtion", maxBlockAbsorbtion,
                "Amount of blocks needed to fully absorb material essence").getInt();
        maxLiquidAbsorbtion = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "maxLiquidAbsorbtion", maxLiquidAbsorbtion,
                "Amount of liquid blocks needed to fully absorb liquid essence").getInt();
        maxTimeAbsorbtion = cfg.get(CATEGORY_DIMLET_CONSTRUCTION, "maxTimeAbsorbtion", maxTimeAbsorbtion,
                "Amount of ticks needed to absorb the correct time").getInt();
    }
}
