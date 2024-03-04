package mcjty.rftoolsdim.modules.workbench;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.workbench.blocks.KnowledgeHolderTileEntity;
import mcjty.rftoolsdim.modules.workbench.blocks.ResearcherTileEntity;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import mcjty.rftoolsdim.modules.workbench.client.GuiHolder;
import mcjty.rftoolsdim.modules.workbench.client.GuiResearcher;
import mcjty.rftoolsdim.modules.workbench.client.GuiWorkbench;
import mcjty.rftoolsdim.modules.workbench.client.ResearcherRenderer;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.*;

public class WorkbenchModule implements IModule {

    public static final DeferredBlock<BaseBlock> WORKBENCH = BLOCKS.register("dimlet_workbench", WorkbenchTileEntity::createBlock);
    public static final DeferredItem<Item> WORKBENCH_ITEM = ITEMS.register("dimlet_workbench", tab(() -> new BlockItem(WORKBENCH.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<WorkbenchTileEntity>> TYPE_WORKBENCH = TILES.register("dimlet_workbench", () -> BlockEntityType.Builder.of(WorkbenchTileEntity::new, WORKBENCH.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_WORKBENCH = CONTAINERS.register("dimlet_workbench", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> HOLDER = BLOCKS.register("knowledge_holder", KnowledgeHolderTileEntity::createBlock);
    public static final DeferredItem<Item> HOLDER_ITEM = ITEMS.register("knowledge_holder", tab(() -> new BlockItem(HOLDER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<KnowledgeHolderTileEntity>> TYPE_HOLDER = TILES.register("knowledge_holder", () -> BlockEntityType.Builder.of(KnowledgeHolderTileEntity::new, HOLDER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_HOLDER = CONTAINERS.register("knowledge_holder", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> RESEARCHER = BLOCKS.register("researcher", ResearcherTileEntity::createBlock);
    public static final DeferredItem<Item> RESEARCHER_ITEM = ITEMS.register("researcher", tab(() -> new BlockItem(RESEARCHER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<ResearcherTileEntity>> TYPE_RESEARCHER = TILES.register("researcher", () -> BlockEntityType.Builder.of(ResearcherTileEntity::new, RESEARCHER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESEARCHER = CONTAINERS.register("researcher", GenericContainer::createContainerType);

    public static List<ResourceLocation> onTextureStitch() {
        return Collections.singletonList(ResearcherRenderer.LIGHT);
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
    public void initConfig(IEventBus bus) {
        WorkbenchConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(WORKBENCH)
                        .ironPickaxeTags()
                        .parentedItem("block/dimlet_workbench")
                        .standardLoot(TYPE_WORKBENCH)
                        .blockState(p -> p.orientedBlock(WORKBENCH.get(), p.topBasedModel("dimlet_workbench", p.modLoc("block/dimletworkbenchtop"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('u', DimletModule.EMPTY_DIMLET.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "rur", "CFC", "rur"),
                Dob.blockBuilder(HOLDER)
                        .ironPickaxeTags()
                        .parentedItem("block/knowledge_holder")
                        .standardLoot(TYPE_HOLDER)
                        .blockState(p -> p.orientedBlock(HOLDER.get(), p.frontBasedModel("knowledge_holder", p.modLoc("block/knowledge_holder"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.CHEST)
                                        .define('u', DimletModule.EMPTY_DIMLET.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "sus", "CFC", "sus"),
                Dob.blockBuilder(RESEARCHER)
                        .ironPickaxeTags()
                        .parentedItem("block/researcher")
                        .standardLoot(TYPE_RESEARCHER)
                        .blockState(p -> p.simpleBlock(WorkbenchModule.RESEARCHER.get(), p.models().slab("researcher",
                                p.modLoc("block/researcher_side"),
                                new ResourceLocation("rftoolsbase", "block/base/machinebottom"),
                                new ResourceLocation("rftoolsbase", "block/base/machinetop"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('C', Blocks.ENCHANTING_TABLE)
                                        .define('X', Blocks.COMPARATOR)
                                        .define('u', DimletModule.EMPTY_DIMLET.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "rur", "XFC", "rur")
        );
    }
}
