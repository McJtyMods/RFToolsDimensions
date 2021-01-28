package mcjty.rftoolsdim.modules.decorative;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.BLOCKS;
import static mcjty.rftoolsdim.setup.Registration.ITEMS;

public class DecorativeModule implements IModule {

    public static final RegistryObject<BaseBlock> DIMENSIONAL_BLANK = BLOCKS.register("dimensional_blank_block", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_BLANK_ITEM = ITEMS.register("dimensional_blank_block", () -> new BlockItem(DIMENSIONAL_BLANK.get(), Registration.createStandardProperties()));

    public static final RegistryObject<BaseBlock> DIMENSIONAL_BLOCK = BLOCKS.register("dimensional_block", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_BLOCK_ITEM = ITEMS.register("dimensional_block", () -> new BlockItem(DIMENSIONAL_BLOCK.get(), Registration.createStandardProperties()));

    public static final RegistryObject<BaseBlock> DIMENSIONAL_SMALL_BLOCK = BLOCKS.register("dimensional_small_blocks", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_SMALL_BLOCK_ITEM = ITEMS.register("dimensional_small_blocks", () -> new BlockItem(DIMENSIONAL_SMALL_BLOCK.get(), Registration.createStandardProperties()));

    public static final RegistryObject<BaseBlock> DIMENSIONAL_CROSS_BLOCK = BLOCKS.register("dimensional_cross_block", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_CROSS_BLOCK_ITEM = ITEMS.register("dimensional_cross_block", () -> new BlockItem(DIMENSIONAL_CROSS_BLOCK.get(), Registration.createStandardProperties()));

    public static final RegistryObject<BaseBlock> DIMENSIONAL_CROSS2_BLOCK = BLOCKS.register("dimensional_cross2_block", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_CROSS2_BLOCK_ITEM = ITEMS.register("dimensional_cross2_block", () -> new BlockItem(DIMENSIONAL_CROSS2_BLOCK.get(), Registration.createStandardProperties()));

    public static final RegistryObject<BaseBlock> DIMENSIONAL_PATTERN1_BLOCK = BLOCKS.register("dimensional_pattern1_block", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_PATTERN1_BLOCK_ITEM = ITEMS.register("dimensional_pattern1_block", () -> new BlockItem(DIMENSIONAL_PATTERN1_BLOCK.get(), Registration.createStandardProperties()));

    public static final RegistryObject<BaseBlock> DIMENSIONAL_PATTERN2_BLOCK = BLOCKS.register("dimensional_pattern2_block", () -> new BaseBlock(new BlockBuilder()));
    public static final RegistryObject<Item> DIMENSIONAL_PATTERN2_BLOCK_ITEM = ITEMS.register("dimensional_pattern2_block", () -> new BlockItem(DIMENSIONAL_PATTERN2_BLOCK.get(), Registration.createStandardProperties()));

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
