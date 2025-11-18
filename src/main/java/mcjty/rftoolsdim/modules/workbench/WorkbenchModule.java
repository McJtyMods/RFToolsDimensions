package mcjty.rftoolsdim.modules.workbench;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.*;

public class WorkbenchModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, WorkbenchTileEntity> WORKBENCH = RBLOCKS.registerBlock("dimlet_workbench",
            WorkbenchTileEntity.class,
            WorkbenchTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            WorkbenchTileEntity::new);
    public static final Supplier<BlockEntityType<WorkbenchTileEntity>> TYPE_WORKBENCH = WORKBENCH.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_WORKBENCH = CONTAINERS.register("dimlet_workbench", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, KnowledgeHolderTileEntity> HOLDER = RBLOCKS.registerBlock("knowledge_holder",
            KnowledgeHolderTileEntity.class,
            KnowledgeHolderTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            KnowledgeHolderTileEntity::new);
    public static final Supplier<BlockEntityType<KnowledgeHolderTileEntity>> TYPE_HOLDER = HOLDER.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_HOLDER = CONTAINERS.register("knowledge_holder", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, ResearcherTileEntity> RESEARCHER = RBLOCKS.registerBlock("researcher",
            ResearcherTileEntity.class,
            ResearcherTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            ResearcherTileEntity::new);
    public static final Supplier<BlockEntityType<ResearcherTileEntity>> TYPE_RESEARCHER = RESEARCHER.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESEARCHER = CONTAINERS.register("researcher", GenericContainer::createContainerType);

    public WorkbenchModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    public static List<ResourceLocation> onTextureStitch() {
        return Collections.singletonList(ResearcherRenderer.LIGHT);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ResearcherRenderer.register();
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(CONTAINER_WORKBENCH.get(), GuiWorkbench::new);
        event.register(CONTAINER_HOLDER.get(), GuiHolder::new);
        event.register(CONTAINER_RESEARCHER.get(), GuiResearcher::new);
    }

    @Override
    public void initConfig(IEventBus bus) {
        WorkbenchConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(WORKBENCH)
                        .ironPickaxeTags()
                        .parentedItem("block/dimlet_workbench")
//                        .standardLoot(TYPE_WORKBENCH) @todo 1.21
                        .blockState(p -> p.orientedBlock(WORKBENCH.block().get(), p.topBasedModel("dimlet_workbench", p.modLoc("block/dimletworkbenchtop"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('u', DimletModule.EMPTY_DIMLET.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "rur", "CFC", "rur"),
                Dob.blockBuilder(HOLDER)
                        .ironPickaxeTags()
                        .parentedItem("block/knowledge_holder")
//                        .standardLoot(TYPE_HOLDER)    @todo 1.21
                        .blockState(p -> p.orientedBlock(HOLDER.block().get(), p.frontBasedModel("knowledge_holder", p.modLoc("block/knowledge_holder"))))
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
//                        .standardLoot(TYPE_RESEARCHER)    @todo 1.21
                        .blockState(p -> p.simpleBlock(WorkbenchModule.RESEARCHER.block().get(), p.models().slab("researcher",
                                p.modLoc("block/researcher_side"),
                                ResourceLocation.fromNamespaceAndPath("rftoolsbase", "block/base/machinebottom"),
                                ResourceLocation.fromNamespaceAndPath("rftoolsbase", "block/base/machinetop"))))
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
