package mcjty.rftoolsdim.modules.workbench;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.workbench.blocks.KnowledgeHolderTileEntity;
import mcjty.rftoolsdim.modules.workbench.blocks.ResearcherTileEntity;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import mcjty.rftoolsdim.modules.workbench.client.GuiHolder;
import mcjty.rftoolsdim.modules.workbench.client.GuiResearcher;
import mcjty.rftoolsdim.modules.workbench.client.GuiWorkbench;
import mcjty.rftoolsdim.modules.workbench.client.ResearcherRenderer;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.*;

public class WorkbenchModule implements IModule {

    public static final RegistryObject<BaseBlock> WORKBENCH = BLOCKS.register("dimlet_workbench", WorkbenchTileEntity::createBlock);
    public static final RegistryObject<Item> WORKBENCH_ITEM = ITEMS.register("dimlet_workbench", () -> new BlockItem(WORKBENCH.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<WorkbenchTileEntity>> TYPE_WORKBENCH = TILES.register("dimlet_workbench", () -> BlockEntityType.Builder.of(WorkbenchTileEntity::new, WORKBENCH.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_WORKBENCH = CONTAINERS.register("dimlet_workbench", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> HOLDER = BLOCKS.register("knowledge_holder", KnowledgeHolderTileEntity::createBlock);
    public static final RegistryObject<Item> HOLDER_ITEM = ITEMS.register("knowledge_holder", () -> new BlockItem(HOLDER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<KnowledgeHolderTileEntity>> TYPE_HOLDER = TILES.register("knowledge_holder", () -> BlockEntityType.Builder.of(KnowledgeHolderTileEntity::new, HOLDER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_HOLDER = CONTAINERS.register("knowledge_holder", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> RESEARCHER = BLOCKS.register("researcher", ResearcherTileEntity::createBlock);
    public static final RegistryObject<Item> RESEARCHER_ITEM = ITEMS.register("researcher", () -> new BlockItem(RESEARCHER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<ResearcherTileEntity>> TYPE_RESEARCHER = TILES.register("researcher", () -> BlockEntityType.Builder.of(ResearcherTileEntity::new, RESEARCHER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_RESEARCHER = CONTAINERS.register("researcher", GenericContainer::createContainerType);

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(ResearcherRenderer.LIGHT);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GenericGuiContainer.register(CONTAINER_WORKBENCH.get(), GuiWorkbench::new);
            GenericGuiContainer.register(CONTAINER_HOLDER.get(), GuiHolder::new);
            GenericGuiContainer.register(CONTAINER_RESEARCHER.get(), GuiResearcher::new);
        });
        ResearcherRenderer.register();
    }

    @Override
    public void initConfig() {
        WorkbenchConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
