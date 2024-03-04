package mcjty.rftoolsdim.modules.knowledge;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.ITEMS;

public class KnowledgeModule implements IModule {

    public static final DeferredItem<LostKnowledgeItem> COMMON_LOST_KNOWLEDGE = ITEMS.register("common_lost_knowledge", tab(() -> new LostKnowledgeItem(DimletRarity.COMMON)));
    public static final DeferredItem<LostKnowledgeItem> UNCOMMON_LOST_KNOWLEDGE = ITEMS.register("uncommon_lost_knowledge", tab(() -> new LostKnowledgeItem(DimletRarity.UNCOMMON)));
    public static final DeferredItem<LostKnowledgeItem> RARE_LOST_KNOWLEDGE = ITEMS.register("rare_lost_knowledge", tab(() -> new LostKnowledgeItem(DimletRarity.RARE)));
    public static final DeferredItem<LostKnowledgeItem> LEGENDARY_LOST_KNOWLEDGE = ITEMS.register("legendary_lost_knowledge", tab(() -> new LostKnowledgeItem(DimletRarity.LEGENDARY)));

    public KnowledgeModule() {
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
    }

    private void onWorldLoad(LevelEvent.Load event) {
        KnowledgeManager.get().clear();
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {

    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.itemBuilder(COMMON_LOST_KNOWLEDGE)
                        .generatedItem("item/common_lost_knowledge"),
                Dob.itemBuilder(UNCOMMON_LOST_KNOWLEDGE)
                        .generatedItem("item/uncommon_lost_knowledge")
                        .shaped(builder -> builder
                                        .define('u', COMMON_LOST_KNOWLEDGE.get())
                                        .unlockedBy("knowledge", has(COMMON_LOST_KNOWLEDGE.get())),
                                "uuu", "uuu", "uuu"),
                Dob.itemBuilder(RARE_LOST_KNOWLEDGE)
                        .generatedItem("item/rare_lost_knowledge")
                        .shaped(builder -> builder
                                        .define('u', UNCOMMON_LOST_KNOWLEDGE.get())
                                        .unlockedBy("knowledge", has(COMMON_LOST_KNOWLEDGE.get())),
                                "uuu", "uuu", "uuu"),
                Dob.itemBuilder(LEGENDARY_LOST_KNOWLEDGE)
                        .generatedItem("item/legendary_lost_knowledge")
                        .shaped(builder -> builder
                                        .define('u', RARE_LOST_KNOWLEDGE.get())
                                        .unlockedBy("knowledge", has(COMMON_LOST_KNOWLEDGE.get())),
                                "uuu", "uuu", "uuu")
        );
    }
}
