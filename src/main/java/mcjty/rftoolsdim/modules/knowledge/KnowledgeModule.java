package mcjty.rftoolsdim.modules.knowledge;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import mcjty.rftoolsdim.modules.knowledge.items.PatternRecipeTablet;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class KnowledgeModule implements IModule {

    public static final RegistryObject<Item> LOST_KNOWLEDGE = Registration.ITEMS.register("lost_knowledge", LostKnowledgeItem::new);
    public static final RegistryObject<Item> PATTERN_RECIPE_TABLET = Registration.ITEMS.register("pattern_recipe_tablet", PatternRecipeTablet::new);

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
