package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.commands.ModCommands;
import mcjty.rftoolsdim.compat.LostCityCompat;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.dimension.DimensionRegistry.RFTOOLS_CHUNKGEN_ID;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsDimMessages.registerMessages();

//        e.enqueueWork(() -> {
//            RFTFeature.registerConfiguredFeatures();
//            TerrainPresets.init();
//        });
        NeoForge.EVENT_BUS.register(new ForgeEventHandlers());

        e.enqueueWork(() -> {
            Registry.register(BuiltInRegistries.CHUNK_GENERATOR, RFTOOLS_CHUNKGEN_ID, RFToolsChunkGenerator.CODEC);
            Registry.register(BuiltInRegistries.BIOME_SOURCE, DimensionRegistry.RFTOOLS_BIOMES_ID, RFTBiomeProvider.CODEC);
        });
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
