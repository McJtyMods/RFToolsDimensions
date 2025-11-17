package mcjty.rftoolsdim.modules.various;

import mcjty.lib.blocks.RBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.various.blocks.ActivityProbeBlock;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.registerSimpleBlock;
import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class VariousModule implements IModule {

    public static final RBlock<ActivityProbeBlock, BlockItem, BlockEntity> ACTIVITY_PROBE = registerSimpleBlock("activity_probe", () -> new ActivityProbeBlock(BlockBuilder.STANDARD_IRON));

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(ACTIVITY_PROBE)
                        .ironPickaxeTags()
                        .parentedItem("block/activity_probe")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(ACTIVITY_PROBE.block().get(), BLOCK_FOLDER + "/activity_probe", "block/activity_probe"))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', mcjty.rftoolsbase.modules.various.VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("shard", has(mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())),
                                "sCs", "CFC", "sCs")
        );
    }
}
