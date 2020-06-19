package mcjty.rftoolsdim.modules.dimensionbuilder;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsdim.setup.Registration.*;

public class DimensionBuilderSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<BaseBlock> DIMENSION_BUILDER = BLOCKS.register("dimension_builder", DimensionBuilderTileEntity::createBlock);
    public static final RegistryObject<Item> DIMENSION_BUILDER_ITEM = ITEMS.register("dimension_builder", () -> new BlockItem(DIMENSION_BUILDER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<DimensionBuilderTileEntity>> TYPE_DIMENSION_BUILDER = TILES.register("dimension_builder", () -> TileEntityType.Builder.create(DimensionBuilderTileEntity::new, DIMENSION_BUILDER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_DIMENSION_BUILDER = CONTAINERS.register("dimension_builder", GenericContainer::createContainerType);

}
