package mcjty.rftoolsdim.modules.knowledge;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class KnowledgeModule implements IModule {

    public static final RegistryObject<Item> COMMON_LOST_KNOWLEDGE = Registration.ITEMS.register("common_lost_knowledge", LostKnowledgeItem::new);
    public static final RegistryObject<Item> UNCOMMON_LOST_KNOWLEDGE = Registration.ITEMS.register("uncommon_lost_knowledge", LostKnowledgeItem::new);
    public static final RegistryObject<Item> RARE_LOST_KNOWLEDGE = Registration.ITEMS.register("rare_lost_knowledge", LostKnowledgeItem::new);
    public static final RegistryObject<Item> LEGENDARY_LOST_KNOWLEDGE = Registration.ITEMS.register("legendary_lost_knowledge", LostKnowledgeItem::new);

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
