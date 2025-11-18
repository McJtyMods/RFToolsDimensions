package mcjty.rftoolsdim.modules.essences;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.StructureAbsorberTileEntity;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.setup.Registration.*;
import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class EssencesModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, BlockAbsorberTileEntity> BLOCK_ABSORBER = RBLOCKS.registerBlock("block_absorber",
            BlockAbsorberTileEntity.class,
            BlockAbsorberTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            BlockAbsorberTileEntity::new);
    public static final DeferredItem<BlockItem> BLOCK_ABSORBER_ITEM = BLOCK_ABSORBER.item();
    public static final Supplier<BlockEntityType<BlockAbsorberTileEntity>> TYPE_BLOCK_ABSORBER = BLOCK_ABSORBER.be();

    public static final RBlock<BaseBlock, BlockItem, FluidAbsorberTileEntity> FLUID_ABSORBER = RBLOCKS.registerBlock("fluid_absorber",
            FluidAbsorberTileEntity.class,
            FluidAbsorberTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            FluidAbsorberTileEntity::new);
    public static final DeferredItem<BlockItem> FLUID_ABSORBER_ITEM = FLUID_ABSORBER.item();
    public static final Supplier<BlockEntityType<FluidAbsorberTileEntity>> TYPE_FLUID_ABSORBER = FLUID_ABSORBER.be();

    public static final RBlock<BaseBlock, BlockItem, BiomeAbsorberTileEntity> BIOME_ABSORBER = RBLOCKS.registerBlock("biome_absorber",
            BiomeAbsorberTileEntity.class,
            BiomeAbsorberTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            BiomeAbsorberTileEntity::new);
    public static final DeferredItem<BlockItem> BIOME_ABSORBER_ITEM = BIOME_ABSORBER.item();
    public static final Supplier<BlockEntityType<BiomeAbsorberTileEntity>> TYPE_BIOME_ABSORBER = BIOME_ABSORBER.be();

    public static final RBlock<BaseBlock, BlockItem, StructureAbsorberTileEntity> STRUCTURE_ABSORBER = RBLOCKS.registerBlock("structure_absorber",
            StructureAbsorberTileEntity.class,
            StructureAbsorberTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            StructureAbsorberTileEntity::new);
    public static final DeferredItem<BlockItem> STRUCTURE_ABSORBER_ITEM = STRUCTURE_ABSORBER.item();
    public static final Supplier<BlockEntityType<StructureAbsorberTileEntity>> TYPE_STRUCTURE_ABSORBER = STRUCTURE_ABSORBER.be();

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    @Override
    public void initConfig(IEventBus bus) {
        EssencesConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(BLOCK_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/block_absorber")
//                        .standardLoot(TYPE_BLOCK_ABSORBER) @todo 1.21
                        .blockState(p -> p.singleTextureBlockC(BLOCK_ABSORBER.block().get(), BLOCK_FOLDER + "/block_absorber", "block/blockabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', Blocks.SLIME_BLOCK)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "usu", "sCs", "usu"),
                Dob.blockBuilder(FLUID_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/fluid_absorber")
//                        .standardLoot(TYPE_FLUID_ABSORBER)    @todo 1.21
                        .blockState(p -> p.singleTextureBlockC(FLUID_ABSORBER.block().get(), BLOCK_FOLDER + "/fluid_absorber", "block/fluidabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', Blocks.SLIME_BLOCK)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "uWu", "sCs", "usu"),
                Dob.blockBuilder(BIOME_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/biome_absorber")
//                        .standardLoot(TYPE_BIOME_ABSORBER)    @todo 1.21
                        .blockState(p -> p.singleTextureBlockC(BIOME_ABSORBER.block().get(), BLOCK_FOLDER + "/biome_absorber", "block/biomeabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', ItemTags.LEAVES)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "usu", "sCs", "usu"),
                Dob.blockBuilder(STRUCTURE_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/structure_absorber")
//                        .standardLoot(TYPE_STRUCTURE_ABSORBER)    @todo 1.21
                        .blockState(p -> p.singleTextureBlockC(STRUCTURE_ABSORBER.block().get(), BLOCK_FOLDER + "/structure_absorber", "block/structureabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', ItemTags.STONE_BRICKS)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "usu", "sCs", "usu")
        );
    }
}
