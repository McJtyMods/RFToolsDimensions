package mcjty.rftoolsdim.proxy;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.proxy.AbstractCommonProxy;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WrenchChecker;
import mcjty.rftoolsdim.ForgeEventHandlers;
import mcjty.rftoolsdim.ModCrafting;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.*;
import mcjty.rftoolsdim.dimensions.DimensionTickEvent;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.io.File;

public abstract class CommonProxy extends AbstractCommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "rftools", "dimensions.cfg"));
        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(RFToolsDim.MODID, "rftoolsdim");
        RFToolsDimMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();
        ModDimensions.init();

        DimletRules.readRules(modConfigDir);
    }

    private void readMainConfig() {
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
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(RFToolsDim.instance, new GuiProxy());
        MinecraftForge.EVENT_BUS.register(new DimensionTickEvent());
        ModCrafting.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
//        MobConfiguration.readModdedMobConfig(mainConfig);
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }

        mainConfig = null;
        WrenchChecker.init();
    }

}
