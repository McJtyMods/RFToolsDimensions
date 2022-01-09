package mcjty.rftoolsdim.modules.enscriber;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.enscriber.blocks.EnscriberTileEntity;
import mcjty.rftoolsdim.modules.enscriber.client.GuiEnscriber;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.*;

public class EnscriberModule implements IModule {

    public static final RegistryObject<BaseBlock> ENSCRIBER = BLOCKS.register("enscriber", EnscriberTileEntity::createBlock);
    public static final RegistryObject<Item> ENSCRIBER_ITEM = ITEMS.register("enscriber", () -> new BlockItem(ENSCRIBER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<EnscriberTileEntity>> TYPE_ENSCRIBER = TILES.register("enscriber", () -> BlockEntityType.Builder.of(EnscriberTileEntity::new, ENSCRIBER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_ENSCRIBER = CONTAINERS.register("enscriber", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GenericGuiContainer.register(CONTAINER_ENSCRIBER.get(), GuiEnscriber::new);
        });
    }

    @Override
    public void initConfig() {
        EnscriberConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
