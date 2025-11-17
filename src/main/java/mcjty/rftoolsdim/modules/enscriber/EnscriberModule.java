package mcjty.rftoolsdim.modules.enscriber;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
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
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.*;

public class EnscriberModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, EnscriberTileEntity> ENSCRIBER = RBLOCKS.registerBlock("enscriber",
            EnscriberTileEntity.class,
            EnscriberTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            EnscriberTileEntity::new);
    public static final Supplier<BlockEntityType<EnscriberTileEntity>> TYPE_ENSCRIBER = ENSCRIBER.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENSCRIBER = CONTAINERS.register("enscriber", GenericContainer::createContainerType);

    public EnscriberModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(CONTAINER_ENSCRIBER.get(), GuiEnscriber::new);
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
                        .blockState(p -> p.orientedBlock(ENSCRIBER.block().get(), p.frontBasedModel("enscriber", p.modLoc("block/dimensionenscriber"))))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('u', DimletModule.EMPTY_DIMLET.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "pup", "CFC", "pup")
        );
    }
}
