package mcjty.rftoolsdim.modules.various;

import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.various.blocks.ActivityProbeBlock;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.rftoolsdim.setup.Registration.BLOCKS;
import static mcjty.rftoolsdim.setup.Registration.ITEMS;

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
}
