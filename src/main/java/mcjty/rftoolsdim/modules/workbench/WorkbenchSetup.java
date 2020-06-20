package mcjty.rftoolsdim.modules.workbench;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsdim.setup.Registration.*;

public class WorkbenchSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<BaseBlock> WORKBENCH = BLOCKS.register("dimlet_workbench", WorkbenchTileEntity::createBlock);
    public static final RegistryObject<Item> WORKBENCH_ITEM = ITEMS.register("dimlet_workbench", () -> new BlockItem(WORKBENCH.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<WorkbenchTileEntity>> TYPE_WORKBENCH = TILES.register("dimlet_workbench", () -> TileEntityType.Builder.create(WorkbenchTileEntity::new, WORKBENCH.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_WORKBENCH = CONTAINERS.register("dimlet_workbench", GenericContainer::createContainerType);

}
