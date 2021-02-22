package mcjty.rftoolsdim.modules.knowledge;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.ITEMS;

public class KnowledgeModule implements IModule {

    public static final RegistryObject<LostKnowledgeItem> COMMON_LOST_KNOWLEDGE = ITEMS.register("common_lost_knowledge", () -> new LostKnowledgeItem(DimletRarity.COMMON));
    public static final RegistryObject<LostKnowledgeItem> UNCOMMON_LOST_KNOWLEDGE = ITEMS.register("uncommon_lost_knowledge", () -> new LostKnowledgeItem(DimletRarity.UNCOMMON));
    public static final RegistryObject<LostKnowledgeItem> RARE_LOST_KNOWLEDGE = ITEMS.register("rare_lost_knowledge", () -> new LostKnowledgeItem(DimletRarity.RARE));
    public static final RegistryObject<LostKnowledgeItem> LEGENDARY_LOST_KNOWLEDGE = ITEMS.register("legendary_lost_knowledge", () -> new LostKnowledgeItem(DimletRarity.LEGENDARY));

    public KnowledgeModule() {
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
    }

    private void onWorldLoad(WorldEvent.Load event) {
        KnowledgeManager.get().clear();
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {

    }
}
