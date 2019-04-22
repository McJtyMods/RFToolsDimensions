package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsdim.ForgeEventHandlers;
import mcjty.rftoolsdim.ModCrafting;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.ConfigSetup;
import mcjty.rftoolsdim.config.DimletRules;
import mcjty.rftoolsdim.dimensions.DimensionTickEvent;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.DimensionSyncChannelHandler;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumMap;

public class ModSetup extends DefaultModSetup {

    public static boolean chisel = false;
    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        MinecraftForge.EVENT_BUS.register(new DimensionTickEvent());
        NetworkRegistry.INSTANCE.registerGuiHandler(RFToolsDim.instance, new GuiProxy());

        RFToolsDimMessages.registerMessages("rftoolsdim");

        DimletRules.readRules(getModConfigDir());

        ModItems.init();
        ModBlocks.init();
        ModDimensions.init();
    }

    @Override
    protected void setupModCompat() {
        chisel = Loader.isModLoaded("chisel");

        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();

        FMLInterModComms.sendFunctionMessage("rftools", "getTeleportationManager", "mcjty.rftoolsdim.RFToolsDim$GetTeleportationManager");
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftoolsdim.compat.theoneprobe.TheOneProbeSupport");
    }

    @Override
    protected void setupConfig() {
        ConfigSetup.init();
    }

    @Override
    public void createTabs() {
        createTab("RfToolsDim", () -> new ItemStack(ModItems.realizedDimensionTabItem));
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ModCrafting.init();

        channels = NetworkRegistry.INSTANCE.newChannel("RFToolsChannel", DimensionSyncChannelHandler.instance);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
    }

}
