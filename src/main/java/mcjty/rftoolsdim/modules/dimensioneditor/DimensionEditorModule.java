package mcjty.rftoolsdim.modules.dimensioneditor;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.client.DimensionBuilderRenderer;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.client.GuiDimensionEditor;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.*;

public class DimensionEditorModule implements IModule {

    public static final RegistryObject<BaseBlock> DIMENSION_EDITOR = BLOCKS.register("dimension_editor", DimensionEditorTileEntity::createBlock);
    public static final RegistryObject<Item> DIMENSION_EDITOR_ITEM = ITEMS.register("dimension_editor", () -> new BlockItem(DIMENSION_EDITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<DimensionEditorTileEntity>> TYPE_DIMENSION_EDITOR = TILES.register("dimension_editor", () -> TileEntityType.Builder.of(DimensionEditorTileEntity::new, DIMENSION_EDITOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_DIMENSION_EDITOR = CONTAINERS.register("dimension_editor", GenericContainer::createContainerType);

    public DimensionEditorModule() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GenericGuiContainer.register(CONTAINER_DIMENSION_EDITOR.get(), GuiDimensionEditor::new);
        });
        DimensionBuilderRenderer.register();
    }

    @Override
    public void initConfig() {
        DimensionEditorConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

}
