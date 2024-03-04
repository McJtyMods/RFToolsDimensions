package mcjty.rftoolsdim.modules.dimensioneditor;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.client.GuiDimensionEditor;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.*;

public class DimensionEditorModule implements IModule {

    public static final DeferredBlock<BaseBlock> DIMENSION_EDITOR = BLOCKS.register("dimension_editor", DimensionEditorTileEntity::createBlock);
    public static final DeferredItem<Item> DIMENSION_EDITOR_ITEM = ITEMS.register("dimension_editor", tab(() -> new BlockItem(DIMENSION_EDITOR.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<DimensionEditorTileEntity>> TYPE_DIMENSION_EDITOR = TILES.register("dimension_editor", () -> BlockEntityType.Builder.of(DimensionEditorTileEntity::new, DIMENSION_EDITOR.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIMENSION_EDITOR = CONTAINERS.register("dimension_editor", GenericContainer::createContainerType);

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
    public void initConfig(IEventBus bus) {
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
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('g', Items.GOLD_INGOT)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "oio", "iFi", "ggg")
        );
    }
}
