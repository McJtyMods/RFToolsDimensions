package mcjty.rftoolsdim.modules.dimensionbuilder;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.ClientHelpers;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.DimensionBuilderRenderer;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.GuiDimensionBuilder;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.DimensionMonitorItem;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.EmptyDimensionTab;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.PhasedFieldGenerator;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.RealizedDimensionTab;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolsdim.setup.Registration.*;

public class DimensionBuilderModule implements IModule {

    public static final RegistryObject<BaseBlock> DIMENSION_BUILDER = BLOCKS.register("dimension_builder", DimensionBuilderTileEntity::createBlock);
    public static final RegistryObject<Item> DIMENSION_BUILDER_ITEM = ITEMS.register("dimension_builder", () -> new BlockItem(DIMENSION_BUILDER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<DimensionBuilderTileEntity>> TYPE_DIMENSION_BUILDER = TILES.register("dimension_builder", () -> TileEntityType.Builder.of(DimensionBuilderTileEntity::new, DIMENSION_BUILDER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_DIMENSION_BUILDER = CONTAINERS.register("dimension_builder", GenericContainer::createContainerType);

    public static final RegistryObject<EmptyDimensionTab> EMPTY_DIMENSION_TAB = ITEMS.register("empty_dimension_tab", EmptyDimensionTab::new);
    public static final RegistryObject<RealizedDimensionTab> REALIZED_DIMENSION_TAB = ITEMS.register("realized_dimension_tab", RealizedDimensionTab::new);

    public static final RegistryObject<DimensionMonitorItem> DIMENSION_MONITOR = ITEMS.register("dimension_monitor", DimensionMonitorItem::new);
    public static final RegistryObject<PhasedFieldGenerator> PHASED_FIELD_GENERATOR = ITEMS.register("phased_field_generator", PhasedFieldGenerator::new);

    public DimensionBuilderModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GenericGuiContainer.register(CONTAINER_DIMENSION_BUILDER.get(), GuiDimensionBuilder::new);
            ClientHelpers.initOverrides(DIMENSION_MONITOR.get());
            ClientHelpers.initOverrides(PHASED_FIELD_GENERATOR.get());
        });
        DimensionBuilderRenderer.register();
    }

    @Override
    public void initConfig() {
        DimensionBuilderConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

}
