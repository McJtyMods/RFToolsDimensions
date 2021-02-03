package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.commands.ModCommands;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.features.RFTFeature;
import mcjty.rftoolsdim.dimension.terraintypes.*;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.dimension.DimensionRegistry.*;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup extends DefaultModSetup {


    public ModSetup() {
        createTab("rftoolsdim", () -> new ItemStack(DimletModule.EMPTY_DIMLET.get()));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsDimMessages.registerMessages("rftoolsdim");

        RFTFeature.registerConfiguredFeatures();
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        e.enqueueWork(() -> {
            Registry.register(Registry.CHUNK_GENERATOR_CODEC, VOID_ID, VoidChunkGenerator.CODEC);
            Registry.register(Registry.CHUNK_GENERATOR_CODEC, WAVES_ID, WavesChunkGenerator.CODEC);
            Registry.register(Registry.CHUNK_GENERATOR_CODEC, FLAT_ID, FlatChunkGenerator.CODEC);
            Registry.register(Registry.CHUNK_GENERATOR_CODEC, NORMAL_ID, NormalChunkGenerator.CODEC);
            Registry.register(Registry.CHUNK_GENERATOR_CODEC, ISLANDS_ID, IslandsChunkGenerator.CODEC);
            Registry.register(Registry.BIOME_PROVIDER_CODEC, DimensionRegistry.BIOMES_ID, RFTBiomeProvider.CODEC);
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
    }
}
