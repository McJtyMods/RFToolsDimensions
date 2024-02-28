package mcjty.rftoolsdim.modules.essences;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.StructureAbsorberTileEntity;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.setup.Registration.*;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class EssencesModule implements IModule {

    public static final DeferredBlock<BaseBlock> BLOCK_ABSORBER = BLOCKS.register("block_absorber", BlockAbsorberTileEntity::createBlock);
    public static final DeferredItem<Item> BLOCK_ABSORBER_ITEM = ITEMS.register("block_absorber", tab(() -> new BlockItem(BLOCK_ABSORBER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<BlockAbsorberTileEntity>> TYPE_BLOCK_ABSORBER = TILES.register("block_absorber", () -> BlockEntityType.Builder.of(BlockAbsorberTileEntity::new, BLOCK_ABSORBER.get()).build(null));

    public static final DeferredBlock<BaseBlock> FLUID_ABSORBER = BLOCKS.register("fluid_absorber", FluidAbsorberTileEntity::createBlock);
    public static final DeferredItem<Item> FLUID_ABSORBER_ITEM = ITEMS.register("fluid_absorber", tab(() -> new BlockItem(FLUID_ABSORBER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<FluidAbsorberTileEntity>> TYPE_FLUID_ABSORBER = TILES.register("fluid_absorber", () -> BlockEntityType.Builder.of(FluidAbsorberTileEntity::new, FLUID_ABSORBER.get()).build(null));

    public static final DeferredBlock<BaseBlock> BIOME_ABSORBER = BLOCKS.register("biome_absorber", BiomeAbsorberTileEntity::createBlock);
    public static final DeferredItem<Item> BIOME_ABSORBER_ITEM = ITEMS.register("biome_absorber", tab(() -> new BlockItem(BIOME_ABSORBER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<BiomeAbsorberTileEntity>> TYPE_BIOME_ABSORBER = TILES.register("biome_absorber", () -> BlockEntityType.Builder.of(BiomeAbsorberTileEntity::new, BIOME_ABSORBER.get()).build(null));

    public static final DeferredBlock<BaseBlock> STRUCTURE_ABSORBER = BLOCKS.register("structure_absorber", StructureAbsorberTileEntity::createBlock);
    public static final DeferredItem<Item> STRUCTURE_ABSORBER_ITEM = ITEMS.register("structure_absorber", tab(() -> new BlockItem(STRUCTURE_ABSORBER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<StructureAbsorberTileEntity>> TYPE_STRUCTURE_ABSORBER = TILES.register("structure_absorber", () -> BlockEntityType.Builder.of(StructureAbsorberTileEntity::new, STRUCTURE_ABSORBER.get()).build(null));

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
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(BLOCK_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/block_absorber")
                        .standardLoot(TYPE_BLOCK_ABSORBER)
                        .blockState(p -> p.singleTextureBlockC(BLOCK_ABSORBER.get(), BLOCK_FOLDER + "/block_absorber", "block/blockabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', Blocks.SLIME_BLOCK)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "usu", "sCs", "usu"),
                Dob.blockBuilder(FLUID_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/fluid_absorber")
                        .standardLoot(TYPE_FLUID_ABSORBER)
                        .blockState(p -> p.singleTextureBlockC(FLUID_ABSORBER.get(), BLOCK_FOLDER + "/fluid_absorber", "block/fluidabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', Blocks.SLIME_BLOCK)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "uWu", "sCs", "usu"),
                Dob.blockBuilder(BIOME_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/biome_absorber")
                        .standardLoot(TYPE_BIOME_ABSORBER)
                        .blockState(p -> p.singleTextureBlockC(BIOME_ABSORBER.get(), BLOCK_FOLDER + "/biome_absorber", "block/biomeabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', ItemTags.LEAVES)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "usu", "sCs", "usu"),
                Dob.blockBuilder(STRUCTURE_ABSORBER)
                        .ironPickaxeTags()
                        .parentedItem("block/structure_absorber")
                        .standardLoot(TYPE_STRUCTURE_ABSORBER)
                        .blockState(p -> p.singleTextureBlockC(STRUCTURE_ABSORBER.get(), BLOCK_FOLDER + "/structure_absorber", "block/structureabsorber", builder -> builder.renderType("cutout")))
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .define('C', Blocks.SPONGE)
                                        .define('u', ItemTags.STONE_BRICKS)
                                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                                "usu", "sCs", "usu")
        );
    }
}
