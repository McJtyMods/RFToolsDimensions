package mcjty.rftoolsdim.modules.decorative;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.BLOCKS;
import static mcjty.rftoolsdim.setup.Registration.ITEMS;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class DecorativeModule implements IModule {

    public static final DeferredBlock<Block> DIMENSIONAL_BLANK = BLOCKS.register("dimensional_blank_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_BLANK_ITEM = ITEMS.register("dimensional_blank_block", tab(() -> new BlockItem(DIMENSIONAL_BLANK.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DIMENSIONAL_BLOCK = BLOCKS.register("dimensional_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_BLOCK_ITEM = ITEMS.register("dimensional_block", tab(() -> new BlockItem(DIMENSIONAL_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DIMENSIONAL_SMALL_BLOCK = BLOCKS.register("dimensional_small_blocks", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_SMALL_BLOCK_ITEM = ITEMS.register("dimensional_small_blocks", tab(() -> new BlockItem(DIMENSIONAL_SMALL_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DIMENSIONAL_CROSS_BLOCK = BLOCKS.register("dimensional_cross_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_CROSS_BLOCK_ITEM = ITEMS.register("dimensional_cross_block", tab(() -> new BlockItem(DIMENSIONAL_CROSS_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DIMENSIONAL_CROSS2_BLOCK = BLOCKS.register("dimensional_cross2_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_CROSS2_BLOCK_ITEM = ITEMS.register("dimensional_cross2_block", tab(() -> new BlockItem(DIMENSIONAL_CROSS2_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DIMENSIONAL_PATTERN1_BLOCK = BLOCKS.register("dimensional_pattern1_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_PATTERN1_BLOCK_ITEM = ITEMS.register("dimensional_pattern1_block", tab(() -> new BlockItem(DIMENSIONAL_PATTERN1_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DIMENSIONAL_PATTERN2_BLOCK = BLOCKS.register("dimensional_pattern2_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final DeferredItem<Item> DIMENSIONAL_PATTERN2_BLOCK_ITEM = ITEMS.register("dimensional_pattern2_block", tab(() -> new BlockItem(DIMENSIONAL_PATTERN2_BLOCK.get(), Registration.createStandardProperties())));

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
                Dob.blockBuilder(DIMENSIONAL_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_BLOCK.get(), BLOCK_FOLDER + "/dimensional_block", "block/decorative/dimblock_block"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "Ss", "ss"),
                Dob.blockBuilder(DIMENSIONAL_BLANK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_blank_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_BLANK.get(), BLOCK_FOLDER + "/dimensional_blank_block", "block/decorative/dimblock_blank_stone"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "ss", "ss"),
                Dob.blockBuilder(DIMENSIONAL_CROSS_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_cross_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_CROSS_BLOCK.get(), BLOCK_FOLDER + "/dimensional_cross_block", "block/decorative/dimblock_pattern3"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "Ss", "sS"),
                Dob.blockBuilder(DIMENSIONAL_CROSS2_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_cross2_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_CROSS2_BLOCK.get(), BLOCK_FOLDER + "/dimensional_cross2_block", "block/decorative/dimblock_pattern4"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "sS", "Ss"),
                Dob.blockBuilder(DIMENSIONAL_PATTERN1_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_pattern1_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_PATTERN1_BLOCK.get(), BLOCK_FOLDER + "/dimensional_pattern1_block", "block/decorative/dimblock_pattern7"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "sS", "sS"),
                Dob.blockBuilder(DIMENSIONAL_PATTERN2_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_pattern2_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_PATTERN2_BLOCK.get(), BLOCK_FOLDER + "/dimensional_pattern2_block", "block/decorative/dimblock_pattern8"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "Ss", "Ss"),
                Dob.blockBuilder(DIMENSIONAL_SMALL_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_small_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_SMALL_BLOCK.get(), BLOCK_FOLDER + "/dimensional_small_block", "block/decorative/dimblock_small_blocks"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "ss", "sS")
        );
    }
}
