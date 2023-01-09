package mcjty.rftoolsdim.modules.dimensioneditor;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.client.GuiDimensionEditor;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.*;

public class DimensionEditorModule implements IModule {

    public static final RegistryObject<BaseBlock> DIMENSION_EDITOR = BLOCKS.register("dimension_editor", DimensionEditorTileEntity::createBlock);
    public static final RegistryObject<Item> DIMENSION_EDITOR_ITEM = ITEMS.register("dimension_editor", () -> new BlockItem(DIMENSION_EDITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<DimensionEditorTileEntity>> TYPE_DIMENSION_EDITOR = TILES.register("dimension_editor", () -> BlockEntityType.Builder.of(DimensionEditorTileEntity::new, DIMENSION_EDITOR.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_DIMENSION_EDITOR = CONTAINERS.register("dimension_editor", GenericContainer::createContainerType);

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
    }

    @Override
    public void initConfig() {
        DimensionEditorConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(DIMENSION_EDITOR)
                        .ironPickaxeTags()
                        .parentedItem( "block/dimensioneditor")
                        .standardLoot(TYPE_DIMENSION_EDITOR)
                        .blockState(DataGenHelper::registerDimensionEditor)
                        .shaped(builder -> builder
                                        .define('g', Items.GOLD_INGOT)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "oio", "iFi", "ggg")
        );
    }
}
