package mcjty.rftoolsdim.proxy;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.varia.WrenchChecker;
import mcjty.rftoolsdim.ForgeEventHandlers;
import mcjty.rftoolsdim.ModCrafting;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.dimensions.DimletConfiguration;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

public abstract class CommonProxy {

    public static File modConfigDir;
    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        GeneralConfig.preInit(e);

        modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "rftools", "dimensions.cfg"));
        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(RFToolsDim.MODID, "rftoolsdim");
        RFToolsDimMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();
        ModDimensions.init();
        KnownDimletConfiguration.setupChestLoot();
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(DimletConfiguration.CATEGORY_DIMLETS, "Dimension related settings");
//            cfg.addCustomCategoryComment(CoalGeneratorConfiguration.CATEGORY_COALGEN, "Settings for the coal generator");
//            cfg.addCustomCategoryComment(CrafterConfiguration.CATEGORY_CRAFTER, "Settings for the crafter");
//            cfg.addCustomCategoryComment(ModularStorageConfiguration.CATEGORY_STORAGE, "Settings for the modular storage system");
//            cfg.addCustomCategoryComment(ModularStorageConfiguration.CATEGORY_STORAGE_CONFIG, "Generic item module categories for various items");
//            cfg.addCustomCategoryComment(ScreenConfiguration.CATEGORY_SCREEN, "Settings for the screen system");

            DimletConfiguration.init(cfg);
//            CoalGeneratorConfiguration.init(cfg);
//            CrafterConfiguration.init(cfg);
//            ModularStorageConfiguration.init(cfg);
//            ScreenConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(RFToolsDim.instance, new GuiProxy());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        ModCrafting.init();
    }

    public void postInit(FMLPostInitializationEvent e) {
//        MobConfiguration.readModdedMobConfig(mainConfig);
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }


        mainConfig = null;
        WrenchChecker.init();
    }

}
