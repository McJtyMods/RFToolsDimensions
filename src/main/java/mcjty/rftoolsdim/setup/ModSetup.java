package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.commands.ModCommands;
import mcjty.rftoolsdim.compat.LostCityCompat;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = RFToolsDim.MODID)
public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

//        e.enqueueWork(() -> {
//            RFTFeature.registerConfiguredFeatures();
//            TerrainPresets.init();
//        });
        NeoForge.EVENT_BUS.register(new ForgeEventHandlers());
    }

    @SubscribeEvent
    public static void serverLoad(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
        RFToolsUtilityCompat.register();
        LostCityCompat.register();
    }
}
