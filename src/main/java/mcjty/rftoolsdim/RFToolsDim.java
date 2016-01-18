package mcjty.rftoolsdim;

import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.varia.Logging;
import mcjty.rftools.api.teleportation.ITeleportationManager;
import mcjty.rftoolsdim.commands.CommandRftDim;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.network.DimensionSyncChannelHandler;
import mcjty.rftoolsdim.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.function.Function;

@Mod(modid = RFToolsDim.MODID, name="RFTools Dimensions", dependencies =
        "required-after:Forge@["+ RFToolsDim.MIN_FORGE_VER+
//                ",);required-after:CoFHLib@["+RFTools.MIN_COFHLIB_VER+
        ",);required-after:rftools@["+RFToolsDim.MIN_RFTOOLS_VER+
        ",);required-after:McJtyLib@["+ RFToolsDim.MIN_MCJTYLIB_VER+",)",
        version = RFToolsDim.VERSION)
public class RFToolsDim implements ModBase {
    public static final String MODID = "rftoolsdim";
    public static final String VERSION = "4.20beta8";
    public static final String MIN_FORGE_VER = "11.15.0.1686";
//    public static final String MIN_COFHLIB_VER = "1.0.3";
    public static final String MIN_MCJTYLIB_VER = "1.8.9-1.8.1beta4";
    public static final String MIN_RFTOOLS_VER = "1.8.9-4.20beta10";

    @SidedProxy(clientSide="mcjty.rftoolsdim.proxy.ClientProxy", serverSide="mcjty.rftoolsdim.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("rftoolsdim")
    public static RFToolsDim instance;

    public static ITeleportationManager teleportationManager;

    // Are some mods loaded?.

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    /** This is used to keep track of GUIs that we make*/
    private static int modGuiIndex = 0;
    public static final int GUI_DIMENSION_ENSCRIBER = modGuiIndex++;

    public static CreativeTabs tabRfToolsDim = new CreativeTabs("RfToolsDim") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Items.diamond; /*ModItems.rfToolsManualItem;*/
        }
    };

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    /** Set our custom inventory Gui index to the next available Gui index */
//    public static final int GUI_MANUAL_MAIN = modGuiIndex++;


    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        this.proxy.preInit(e);
        MainCompatHandler.registerWaila();

        FMLInterModComms.sendFunctionMessage("rftools", "getApi", "mcjty.rftoolsdim.RFToolsDim$GetTeleportationManager");
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        this.proxy.init(e);

        channels = NetworkRegistry.INSTANCE.newChannel("RFToolsChannel", DimensionSyncChannelHandler.instance);

//        Achievements.init();
        // @todo
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandRftDim());
//        event.registerServerCommand(new CommandRftTp());
//        event.registerServerCommand(new CommandRftDb());
//        event.registerServerCommand(new CommandRftCfg());
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
//        GuiRFToolsManual.locatePage = page;
        player.openGui(RFToolsDim.instance, bookIndex, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
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
