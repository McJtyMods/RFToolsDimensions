package mcjty.rftoolsdim.config;

import net.minecraftforge.common.config.Configuration;

public class DimletConfiguration {
    public static final String CATEGORY_DIMLETS = "dimlets";

    public static float rarity0;
    public static float rarity1;
    public static float rarity2;
    public static float rarity3;
    public static float rarity4;
    public static float rarity5;
    public static float rarity6;

    public static void init(Configuration cfg) {
        rarity0 = (float) cfg.get(CATEGORY_DIMLETS, "level0", 500.0f, "Rarity factor for level 0").getDouble();
        rarity1 = (float) cfg.get(CATEGORY_DIMLETS, "level1", 250.0f, "Rarity factor for level 1").getDouble();
        rarity2 = (float) cfg.get(CATEGORY_DIMLETS, "level2", 150.0f, "Rarity factor for level 2").getDouble();
        rarity3 = (float) cfg.get(CATEGORY_DIMLETS, "level3", 90.0f, "Rarity factor for level 3").getDouble();
        rarity4 = (float) cfg.get(CATEGORY_DIMLETS, "level4", 40.0f, "Rarity factor for level 4").getDouble();
        rarity5 = (float) cfg.get(CATEGORY_DIMLETS, "level5", 20.0f, "Rarity factor for level 5").getDouble();
        rarity6 = (float) cfg.get(CATEGORY_DIMLETS, "level6", 1.0f, "Rarity factor for level 6").getDouble();
    }

}
