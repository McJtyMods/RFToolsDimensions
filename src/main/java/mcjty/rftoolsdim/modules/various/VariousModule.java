package mcjty.rftoolsdim.modules.various;

import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.various.blocks.ActivityProbeBlock;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.BLOCKS;
import static mcjty.rftoolsdim.setup.Registration.ITEMS;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class VariousModule implements IModule {

    public static final RegistryObject<ActivityProbeBlock> ACTIVITY_PROBE = BLOCKS.register("activity_probe", () -> new ActivityProbeBlock(BlockBuilder.STANDARD_IRON));
    public static final RegistryObject<Item> ACTIVITY_PROBE_ITEM = ITEMS.register("activity_probe", () -> new BlockItem(ACTIVITY_PROBE.get(), Registration.createStandardProperties()));

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(ACTIVITY_PROBE)
                        .ironPickaxeTags()
                        .parentedItem("block/activity_probe")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(ACTIVITY_PROBE.get(), BLOCK_FOLDER + "/activity_probe", "block/activity_probe"))
                        .shaped(builder -> builder
                                        .define('F', mcjty.rftoolsbase.modules.various.VariousModule.MACHINE_FRAME.get())
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', mcjty.rftoolsbase.modules.various.VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("shard", has(mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())),
                                "sCs", "CFC", "sCs")
        );
    }
}
