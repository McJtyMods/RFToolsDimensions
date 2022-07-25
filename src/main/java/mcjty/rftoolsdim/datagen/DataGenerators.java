package mcjty.rftoolsdim.datagen;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        DimletModule.registerLootHelpers();
        generator.addProvider(event.includeServer(), new Recipes(generator));
        generator.addProvider(event.includeServer(), new LootTables(generator));
        generator.addProvider(event.includeServer(), new BlockTags(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new BlockStates(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new Items(generator, event.getExistingFileHelper()));
    }
}