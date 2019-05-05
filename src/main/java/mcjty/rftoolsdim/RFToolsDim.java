package mcjty.rftoolsdim;

import mcjty.lib.base.ModBase;
import mcjty.lib.proxy.IProxy;
import mcjty.lib.varia.Logging;
import mcjty.rftools.api.teleportation.ITeleportationManager;
import mcjty.rftoolsdim.api.dimension.IDimensionManager;
import mcjty.rftoolsdim.api.dimlet.IDimletConfigurationManager;
import mcjty.rftoolsdim.apiimpl.DimensionManager;
import mcjty.rftoolsdim.apiimpl.DimletConfigurationManager;
import mcjty.rftoolsdim.commands.CommandRftDb;
import mcjty.rftoolsdim.commands.CommandRftDim;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.items.manual.GuiRFToolsManual;
import mcjty.rftoolsdim.setup.ModSetup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

@Mod(modid = RFToolsDim.MODID, name="RFTools Dimensions",
        dependencies =
                        "required-after:mcjtylib_ng@[" + RFToolsDim.MIN_MCJTYLIB_VER + ",);" +
                        "required-after:rftools@[" + RFToolsDim.MIN_RFTOOLS_VER + ",);" +
                        "after:forge@[" + RFToolsDim.MIN_FORGE11_VER + ",)",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = RFToolsDim.VERSION)
public class RFToolsDim implements ModBase {
    public static final String MODID = "rftoolsdim";
    public static final String MIN_RFTOOLS_VER = "7.58";
    public static final String VERSION = "5.71";
    public static final String MIN_FORGE11_VER = "14.23.2.2645";
    public static final String MIN_MCJTYLIB_VER = "3.5.0";

    @SidedProxy(clientSide="mcjty.rftoolsdim.setup.ClientProxy", serverSide="mcjty.rftoolsdim.setup.ServerProxy")
    public static IProxy proxy;
    public static ModSetup setup = new ModSetup();

    @Mod.Instance("rftoolsdim")
    public static RFToolsDim instance;

    public static ITeleportationManager teleportationManager;

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        setup.preInit(e);
        proxy.preInit(e);
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        setup.init(e);
        proxy.init(e);
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        setup.postInit(e);
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandRftDim());
        event.registerServerCommand(new CommandRftDb());
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        Logging.log("RFTools: server is starting");
        ModDimensions.initDimensions();
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        Logging.log("RFTools Dimensions: server is stopping. Shutting down gracefully");
        RfToolsDimensionManager.cleanupDimensionInformation();
        RfToolsDimensionManager.clearInstance();
        KnownDimletConfiguration.init();
        DimletRandomizer.init();
    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if ("getDimletConfigurationManager".equalsIgnoreCase(message.key)) {
                Optional<Function<IDimletConfigurationManager, Void>> value = message.getFunctionValue(IDimletConfigurationManager.class, Void.class);
                String mod = message.getSender();
                Logging.log("Received RFTools Dimensions dimlet reconfiguration request from mod '" + mod + "'");
                value.get().apply(new DimletConfigurationManager(mod));
            } else if ("getDimensionManager".equalsIgnoreCase(message.key)) {
                Optional<Function<IDimensionManager, Void>> value = message.getFunctionValue(IDimensionManager.class, Void.class);
                String mod = message.getSender();
                Logging.log("Received RFTools dimension manager request from mod '" + mod + "'");
                value.get().apply(new DimensionManager());
            }
        }
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
        GuiRFToolsManual.locatePage = page;
        player.openGui(RFToolsDim.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    public static class GetTeleportationManager implements com.google.common.base.Function<ITeleportationManager, Void> {
        @Nullable
        @Override
        public Void apply(ITeleportationManager manager) {
            teleportationManager = manager;
            return null;
        }
    }
}
