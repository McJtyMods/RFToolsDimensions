package mcjty.rftoolsdim.modules.decorative;

import mcjty.lib.blocks.RBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.registerSimpleBlock;
import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class DecorativeModule implements IModule {

    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_BLANK = registerSimpleBlock("dimensional_blank_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_BLOCK = registerSimpleBlock("dimensional_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_SMALL_BLOCK = registerSimpleBlock("dimensional_small_blocks", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_CROSS_BLOCK = registerSimpleBlock("dimensional_cross_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_CROSS2_BLOCK = registerSimpleBlock("dimensional_cross2_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_PATTERN1_BLOCK = registerSimpleBlock("dimensional_pattern1_block", () -> new Block(BlockBuilder.STANDARD_IRON));
    public static final RBlock<Block, BlockItem, BlockEntity> DIMENSIONAL_PATTERN2_BLOCK = registerSimpleBlock("dimensional_pattern2_block", () -> new Block(BlockBuilder.STANDARD_IRON));

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
                Dob.blockBuilder(DIMENSIONAL_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_BLOCK.block().get(), BLOCK_FOLDER + "/dimensional_block", "block/decorative/dimblock_block"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "Ss", "ss"),
                Dob.blockBuilder(DIMENSIONAL_BLANK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_blank_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_BLANK.block().get(), BLOCK_FOLDER + "/dimensional_blank_block", "block/decorative/dimblock_blank_stone"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "ss", "ss"),
                Dob.blockBuilder(DIMENSIONAL_CROSS_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_cross_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_CROSS_BLOCK.block().get(), BLOCK_FOLDER + "/dimensional_cross_block", "block/decorative/dimblock_pattern3"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "Ss", "sS"),
                Dob.blockBuilder(DIMENSIONAL_CROSS2_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_cross2_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_CROSS2_BLOCK.block().get(), BLOCK_FOLDER + "/dimensional_cross2_block", "block/decorative/dimblock_pattern4"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "sS", "Ss"),
                Dob.blockBuilder(DIMENSIONAL_PATTERN1_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_pattern1_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_PATTERN1_BLOCK.block().get(), BLOCK_FOLDER + "/dimensional_pattern1_block", "block/decorative/dimblock_pattern7"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "sS", "sS"),
                Dob.blockBuilder(DIMENSIONAL_PATTERN2_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_pattern2_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_PATTERN2_BLOCK.block().get(), BLOCK_FOLDER + "/dimensional_pattern2_block", "block/decorative/dimblock_pattern8"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "Ss", "Ss"),
                Dob.blockBuilder(DIMENSIONAL_SMALL_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem("block/dimensional_small_block")
                        .simpleLoot()
                        .blockState(p -> p.singleTextureBlock(DIMENSIONAL_SMALL_BLOCK.block().get(), BLOCK_FOLDER + "/dimensional_small_block", "block/decorative/dimblock_small_blocks"))
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('S', Tags.Items.STONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "ss", "sS")
        );
    }
}
