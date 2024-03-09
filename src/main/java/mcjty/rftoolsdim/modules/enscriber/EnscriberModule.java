package mcjty.rftoolsdim.modules.enscriber;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.blocks.EnscriberTileEntity;
import mcjty.rftoolsdim.modules.enscriber.client.GuiEnscriber;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.*;

public class EnscriberModule implements IModule {

    public static final DeferredBlock<BaseBlock> ENSCRIBER = BLOCKS.register("enscriber", EnscriberTileEntity::createBlock);
    public static final DeferredItem<Item> ENSCRIBER_ITEM = ITEMS.register("enscriber", tab(() -> new BlockItem(ENSCRIBER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<EnscriberTileEntity>> TYPE_ENSCRIBER = TILES.register("enscriber", () -> BlockEntityType.Builder.of(EnscriberTileEntity::new, ENSCRIBER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENSCRIBER = CONTAINERS.register("enscriber", GenericContainer::createContainerType);

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
    public void initConfig(IEventBus bus) {
        EnscriberConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(ENSCRIBER)
                        .ironPickaxeTags()
                        .parentedItem("block/enscriber")
                        .standardLoot(TYPE_ENSCRIBER)
                        .blockState(p -> p.orientedBlock(ENSCRIBER.get(), p.frontBasedModel("enscriber", p.modLoc("block/dimensionenscriber"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('u', DimletModule.EMPTY_DIMLET.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "pup", "CFC", "pup")
        );
    }
}
