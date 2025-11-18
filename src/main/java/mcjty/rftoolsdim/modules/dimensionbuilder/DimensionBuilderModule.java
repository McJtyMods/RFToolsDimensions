package mcjty.rftoolsdim.modules.dimensionbuilder;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.ClientHelpers;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.DimensionBuilderRenderer;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.GuiDimensionBuilder;
import mcjty.rftoolsdim.modules.dimensionbuilder.data.PhasedFieldGeneratorData;
import mcjty.rftoolsdim.modules.dimensionbuilder.data.RealizedTabData;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.DimensionMonitorItem;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.EmptyDimensionTab;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.PhasedFieldGenerator;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.RealizedDimensionTab;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.*;

public class DimensionBuilderModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, DimensionBuilderTileEntity> DIMENSION_BUILDER = RBLOCKS.registerBlock("dimension_builder",
            DimensionBuilderTileEntity.class,
            DimensionBuilderTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            DimensionBuilderTileEntity::new);
    public static final Supplier<BlockEntityType<DimensionBuilderTileEntity>> TYPE_DIMENSION_BUILDER = DIMENSION_BUILDER.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIMENSION_BUILDER = CONTAINERS.register("dimension_builder", GenericContainer::createContainerType);

    public static final DeferredItem<EmptyDimensionTab> EMPTY_DIMENSION_TAB = ITEMS.register("empty_dimension_tab", tab(EmptyDimensionTab::new));
    public static final DeferredItem<RealizedDimensionTab> REALIZED_DIMENSION_TAB = ITEMS.register("realized_dimension_tab", tab(RealizedDimensionTab::new));

    public static final DeferredItem<DimensionMonitorItem> DIMENSION_MONITOR = ITEMS.register("dimension_monitor", tab(DimensionMonitorItem::new));
    public static final DeferredItem<PhasedFieldGenerator> PHASED_FIELD_GENERATOR = ITEMS.register("phased_field_generator", tab(PhasedFieldGenerator::new));

    // Data components for items in this module
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RealizedTabData>> ITEM_REALIZED_TAB_DATA = COMPONENTS.registerComponentType(
            "realized_tab_data",
            builder -> builder.persistent(RealizedTabData.CODEC).networkSynchronized(RealizedTabData.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PhasedFieldGeneratorData>> ITEM_PHASED_FIELD_GENERATOR_DATA = COMPONENTS.registerComponentType(
            "phased_field_generator_data",
            builder -> builder.persistent(PhasedFieldGeneratorData.CODEC).networkSynchronized(PhasedFieldGeneratorData.STREAM_CODEC)
    );

    public DimensionBuilderModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientHelpers.initOverrides(DIMENSION_MONITOR.get());
            ClientHelpers.initOverrides(PHASED_FIELD_GENERATOR.get());
        });
        DimensionBuilderRenderer.register();
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(CONTAINER_DIMENSION_BUILDER.get(), GuiDimensionBuilder::new);
    }

    @Override
    public void initConfig(IEventBus bus) {
        DimensionBuilderConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(DIMENSION_BUILDER)
                        .ironPickaxeTags()
                        .parentedItem("block/dimension_builder")
                        .standardLoot() // @todo 1.21
                        .blockState(p -> p.orientedBlock(DIMENSION_BUILDER.block().get(), p.frontBasedModel("dimension_builder", p.modLoc("block/dimensionbuilder"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('g', Items.GOLD_INGOT)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "oeo", "dFd", "ggg"),
                Dob.itemBuilder(EMPTY_DIMENSION_TAB)
                        .generatedItem("item/empty_dimension_tab")
                        .shaped(builder -> builder
                                        .unlockedBy("redstone", has(Items.REDSTONE)),
                                "prp", "rpr", "prp"),
                Dob.itemBuilder(REALIZED_DIMENSION_TAB)
                        .generatedItem("item/realized_dimension_tab"),
                Dob.itemBuilder(DIMENSION_MONITOR)
                        .itemModel(p -> {
                            ResourceLocation powerId = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "power");
                            p.getBuilder(DIMENSION_MONITOR.getId().getPath())
                                    .parent(p.getExistingFile(p.mcLoc("item/handheld")))
                                    .texture("layer0", "item/monitor/monitoritem0")
                                    .override().predicate(powerId, 0).model(createMonitorModel(p, 0)).end()
                                    .override().predicate(powerId, 1).model(createMonitorModel(p, 1)).end()
                                    .override().predicate(powerId, 2).model(createMonitorModel(p, 2)).end()
                                    .override().predicate(powerId, 3).model(createMonitorModel(p, 3)).end()
                                    .override().predicate(powerId, 4).model(createMonitorModel(p, 4)).end()
                                    .override().predicate(powerId, 5).model(createMonitorModel(p, 5)).end()
                                    .override().predicate(powerId, 6).model(createMonitorModel(p, 6)).end()
                                    .override().predicate(powerId, 7).model(createMonitorModel(p, 7)).end()
                                    .override().predicate(powerId, 8).model(createMonitorModel(p, 8)).end()
                            ;
                        })
                        .shaped(builder -> builder
                                        .define('C', Blocks.COMPARATOR)
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("redstone", has(Items.REDSTONE)),
                                " s ", "rCr", " s "),
                Dob.itemBuilder(PHASED_FIELD_GENERATOR)
                        .itemModel(p -> {
                            ResourceLocation powerId = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, "power");
                            p.getBuilder(PHASED_FIELD_GENERATOR.getId().getPath())
                                    .parent(p.getExistingFile(p.mcLoc("item/handheld")))
                                    .texture("layer0", "item/pfg/phasedfieldgeneratoriteml0")
                                    .override().predicate(powerId, 0).model(createPFGModel(p, 0)).end()
                                    .override().predicate(powerId, 1).model(createPFGModel(p, 1)).end()
                                    .override().predicate(powerId, 2).model(createPFGModel(p, 2)).end()
                                    .override().predicate(powerId, 3).model(createPFGModel(p, 3)).end()
                                    .override().predicate(powerId, 4).model(createPFGModel(p, 4)).end()
                                    .override().predicate(powerId, 5).model(createPFGModel(p, 5)).end()
                                    .override().predicate(powerId, 6).model(createPFGModel(p, 6)).end()
                                    .override().predicate(powerId, 7).model(createPFGModel(p, 7)).end()
                                    .override().predicate(powerId, 8).model(createPFGModel(p, 8)).end()
                            ;
                        })
                        .shaped(builder -> builder
                                        .define('C', Items.ENDER_EYE)
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("redstone", has(Items.REDSTONE)),
                                "rsr", "sCs", "rsr")
        );
    }

    private static ItemModelBuilder createMonitorModel(BaseItemModelProvider provider, int suffix) {
        return provider.getBuilder("monitoritem" + suffix).parent(provider.getExistingFile(provider.mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/monitoritem" + suffix);
    }

    private static ItemModelBuilder createPFGModel(BaseItemModelProvider provider, int suffix) {
        return provider.getBuilder("phasedfieldgenerator" + suffix).parent(provider.getExistingFile(provider.mcLoc("item/handheld")))
                .texture("layer0", "item/pfg/phasedfieldgeneratoriteml" + suffix);
    }

}
