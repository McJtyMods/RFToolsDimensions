package mcjty.rftoolsdim.config;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigSetup {
    public static Configuration mainConfig;

    public static void init() {
        mainConfig = new Configuration(new File(RFToolsDim.setup.getModConfigDir().getPath() + File.separator + "rftools", "dimensions.cfg"));
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "Dimension related settings");
            cfg.addCustomCategoryComment(MachineConfiguration.CATEGORY_MACHINES, "Machine related settings");
            cfg.addCustomCategoryComment(WorldgenConfiguration.CATEGORY_WORLDGEN, "Worldgen related settings");
            cfg.addCustomCategoryComment(PowerConfiguration.CATEGORY_POWER, "Power related settings");
            cfg.addCustomCategoryComment(DimletConfiguration.CATEGORY_DIMLETS, "Dimlet related settings");
            cfg.addCustomCategoryComment(MobConfiguration.CATEGORY_MOBS, "Mob related settings");
            cfg.addCustomCategoryComment(OresAPlentyConfiguration.CATEGORY_ORESAPLENTY, "Settings for the OresAPlenty dimlet");
            cfg.addCustomCategoryComment(DimletConstructionConfiguration.CATEGORY_DIMLET_CONSTRUCTION, "Dimlet construction related settings");
            cfg.addCustomCategoryComment(LostCityConfiguration.CATEGORY_LOSTCITY, "Settings related to the Lost City dimlet");

            GeneralConfiguration.init(cfg);
            MachineConfiguration.init(cfg);
            WorldgenConfiguration.init(cfg);
            PowerConfiguration.init(cfg);
            DimletConfiguration.init(cfg);
            MobConfiguration.init(cfg);
            OresAPlentyConfiguration.init(cfg);
            LostCityConfiguration.init(cfg);
            DimletConstructionConfiguration.init(cfg);
        } catch (Exception e1) {
            Logging.logError("Problem loading config file!", e1);
        }
    }

    public static void postInit() {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
    }
}
