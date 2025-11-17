package mcjty.rftoolsdim.setup;


import com.mojang.serialization.Codec;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.features.RFTFeature;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

import static mcjty.rftoolsdim.RFToolsDim.MODID;
import static mcjty.rftoolsdim.RFToolsDim.tab;

public class Registration {

    public static final Supplier<Item> DIMENSIONAL_SHARD = () -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("rftoolsbase", "dimensionalshard"));

    public static final RBlockRegistry RBLOCKS = new RBlockRegistry(MODID, RFToolsDim.setup::addTabItem);
    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(MODID);
    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, MODID);

    public static void register(IEventBus bus) {
        RBLOCKS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        SOUNDS.register(bus);
        ENTITIES.register(bus);
        LOOT_MODIFIER_SERIALIZERS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        FEATURES.register(bus);
        TABS.register(bus);
    }


    public static final Supplier<RFTFeature> RFTFEATURE = FEATURES.register(
            RFTFeature.RFTFEATURE_ID.getPath(),
            () -> new RFTFeature(NoneFeatureConfiguration.CODEC));

    public static Item.Properties createStandardProperties() {
        return RFToolsDim.setup.defaultProperties();
    }

    public static Supplier<CreativeModeTab> TAB = TABS.register("rftoolsdim", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(DimletModule.EMPTY_DIMLET.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                RFToolsDim.setup.populateTab(output);
            })
            .build());

    public static <B extends Block> RBlock<B, BlockItem, BlockEntity> registerSimpleBlock(String name, Supplier<B> blockSupplier) {
        return registerSimpleBlock(name, blockSupplier, block -> new BlockItem(block.get(), createStandardProperties()));
    }

    public static <B extends Block, I extends BlockItem> RBlock<B, I, BlockEntity> registerSimpleBlock(String name,
                                                                                                      Supplier<B> blockSupplier,
                                                                                                      Function<Supplier<? extends Block>, I> itemFactory) {
        var block = BLOCKS.register(name, blockSupplier);
        var item = ITEMS.register(name, tab(() -> itemFactory.apply(block)));
        return new RBlock<>(block, item, null);
    }
}
