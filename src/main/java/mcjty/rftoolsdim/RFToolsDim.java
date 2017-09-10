package mcjty.rftoolsdim;

import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.varia.Logging;
import mcjty.rftools.api.teleportation.ITeleportationManager;
import mcjty.rftoolsdim.api.dimension.IDimensionManager;
import mcjty.rftoolsdim.api.dimlet.IDimletConfigurationManager;
import mcjty.rftoolsdim.apiimpl.DimensionManager;
import mcjty.rftoolsdim.apiimpl.DimletConfigurationManager;
import mcjty.rftoolsdim.commands.CommandRftDb;
import mcjty.rftoolsdim.commands.CommandRftDim;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.items.manual.GuiRFToolsManual;
import mcjty.rftoolsdim.network.DimensionSyncChannelHandler;
import mcjty.rftoolsdim.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.EnumMap;
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
    public static final String VERSION = "5.04";
    public static final String MIN_RFTOOLS_VER = "6.10";
    public static final String VERSION = "5.05";
    public static final String MIN_RFTOOLS_VER = "5.81";
    public static final String MIN_FORGE10_VER = "12.18.1.2082";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "2.4.3";

    @SidedProxy(clientSide="mcjty.rftoolsdim.proxy.ClientProxy", serverSide="mcjty.rftoolsdim.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("rftoolsdim")
    public static RFToolsDim instance;

    public static boolean chisel = false;

    public static ITeleportationManager teleportationManager;

    // Are some mods loaded?.

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    /** This is used to keep track of GUIs that we make*/
    private static int modGuiIndex = 0;
    public static final int GUI_DIMENSION_ENSCRIBER = modGuiIndex++;
    public static final int GUI_DIMENSION_BUILDER = modGuiIndex++;
    public static final int GUI_DIMENSION_EDITOR = modGuiIndex++;
    public static final int GUI_MANUAL_DIMENSION = modGuiIndex++;
    public static final int GUI_DIMLET_WORKBENCH = modGuiIndex++;
    public static final int GUI_ESSENCE_PAINTER = modGuiIndex++;

    public static CreativeTabs tabRfToolsDim = new CreativeTabs("RfToolsDim") {

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.realizedDimensionTabItem);
        }
    };

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        this.proxy.preInit(e);
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();

        FMLInterModComms.sendFunctionMessage("rftools", "getTeleportationManager", "mcjty.rftoolsdim.RFToolsDim$GetTeleportationManager");
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftoolsdim.theoneprobe.TheOneProbeSupport");
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

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        this.proxy.init(e);

        chisel = Loader.isModLoaded("chisel");
        channels = NetworkRegistry.INSTANCE.newChannel("RFToolsChannel", DimensionSyncChannelHandler.instance);

//        Achievements.init();
        // @todo
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
        Logging.log("RFTools: server is stopping. Shutting down gracefully");
        RfToolsDimensionManager.cleanupDimensionInformation();
        RfToolsDimensionManager.clearInstance();
        DimensionStorage.clearInstance();
        KnownDimletConfiguration.init();
        DimletRandomizer.init();
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        this.proxy.postInit(e);
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
