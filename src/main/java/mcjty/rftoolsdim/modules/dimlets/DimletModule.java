package mcjty.rftoolsdim.modules.dimlets;

import com.mojang.serialization.Codec;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.PartItem;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.DimletLootEntry;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.EndermanLootModifier;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.LootTableCondition;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletCycleRecipeBuilder;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletCycleRecipeSerializer;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletRecipeBuilder;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletRecipeSerializer;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsdim.RFToolsDim.tab;
import static mcjty.rftoolsdim.modules.knowledge.KnowledgeModule.*;
import static mcjty.rftoolsdim.setup.Registration.*;

public class DimletModule implements IModule {

    public static final DeferredItem<DimletItem> EMPTY_DIMLET = ITEMS.register("empty_dimlet", tab(() -> new DimletItem(null, false)));

    public static final DeferredItem<DimletItem> EMPTY_TERRAIN_DIMLET = ITEMS.register("empty_terrain_dimlet", tab(() -> new DimletItem(DimletType.TERRAIN, false)));
    public static final DeferredItem<DimletItem> EMPTY_ATTRIBUTE_DIMLET = ITEMS.register("empty_attribute_dimlet", tab(() -> new DimletItem(DimletType.ATTRIBUTE, false)));
    public static final DeferredItem<DimletItem> EMPTY_FEATURE_DIMLET = ITEMS.register("empty_feature_dimlet", tab(() -> new DimletItem(DimletType.FEATURE, false)));
    public static final DeferredItem<DimletItem> EMPTY_STRUCTURE_DIMLET = ITEMS.register("empty_structure_dimlet", tab(() -> new DimletItem(DimletType.STRUCTURE, false)));
    public static final DeferredItem<DimletItem> EMPTY_BIOME_DIMLET = ITEMS.register("empty_biome_dimlet", tab(() -> new DimletItem(DimletType.BIOME, false)));
    public static final DeferredItem<DimletItem> EMPTY_BIOME_CONTROLLER_DIMLET = ITEMS.register("empty_biome_controller_dimlet", tab(() -> new DimletItem(DimletType.BIOME_CONTROLLER, false)));
    public static final DeferredItem<DimletItem> EMPTY_BIOME_CATEGORY_DIMLET = ITEMS.register("empty_biome_category_dimlet", tab(() -> new DimletItem(DimletType.BIOME_CATEGORY, false)));
    public static final DeferredItem<DimletItem> EMPTY_BLOCK_DIMLET = ITEMS.register("empty_block_dimlet", tab(() -> new DimletItem(DimletType.BLOCK, false)));
    public static final DeferredItem<DimletItem> EMPTY_FLUID_DIMLET = ITEMS.register("empty_fluid_dimlet", tab(() -> new DimletItem(DimletType.FLUID, false)));
    public static final DeferredItem<DimletItem> EMPTY_TIME_DIMLET = ITEMS.register("empty_time_dimlet", tab(() -> new DimletItem(DimletType.TIME, false)));
    public static final DeferredItem<DimletItem> EMPTY_TAG_DIMLET = ITEMS.register("empty_tag_dimlet", tab(() -> new DimletItem(DimletType.TAG, false)));
    public static final DeferredItem<DimletItem> EMPTY_SKY_DIMLET = ITEMS.register("empty_sky_dimlet", tab(() -> new DimletItem(DimletType.SKY, false)));

    public static final DeferredItem<DimletItem> TERRAIN_DIMLET = ITEMS.register("terrain_dimlet", tab(() -> new DimletItem(DimletType.TERRAIN, true)));
    public static final DeferredItem<DimletItem> ATTRIBUTE_DIMLET = ITEMS.register("attribute_dimlet", tab(() -> new DimletItem(DimletType.ATTRIBUTE, true)));
    public static final DeferredItem<DimletItem> FEATURE_DIMLET = ITEMS.register("feature_dimlet", tab(() -> new DimletItem(DimletType.FEATURE, true)));
    public static final DeferredItem<DimletItem> STRUCTURE_DIMLET = ITEMS.register("structure_dimlet", tab(() -> new DimletItem(DimletType.STRUCTURE, true)));
    public static final DeferredItem<DimletItem> BIOME_DIMLET = ITEMS.register("biome_dimlet", tab(() -> new DimletItem(DimletType.BIOME, true)));
    public static final DeferredItem<DimletItem> BIOME_CONTROLLER_DIMLET = ITEMS.register("biome_controller_dimlet", tab(() -> new DimletItem(DimletType.BIOME_CONTROLLER, true)));
    public static final DeferredItem<DimletItem> BIOME_CATEGORY_DIMLET = ITEMS.register("biome_category_dimlet", tab(() -> new DimletItem(DimletType.BIOME_CATEGORY, true)));
    public static final DeferredItem<DimletItem> BLOCK_DIMLET = ITEMS.register("block_dimlet", tab(() -> new DimletItem(DimletType.BLOCK, true)));
    public static final DeferredItem<DimletItem> FLUID_DIMLET = ITEMS.register("fluid_dimlet", tab(() -> new DimletItem(DimletType.FLUID, true)));
    public static final DeferredItem<DimletItem> TIME_DIMLET = ITEMS.register("time_dimlet", tab(() -> new DimletItem(DimletType.TIME, true)));
    public static final DeferredItem<DimletItem> DIGIT_DIMLET = ITEMS.register("digit_dimlet", tab(() -> new DimletItem(DimletType.DIGIT, true)));
    public static final DeferredItem<DimletItem> TAG_DIMLET = ITEMS.register("tag_dimlet", tab(() -> new DimletItem(DimletType.TAG, true)));
    public static final DeferredItem<DimletItem> SKY_DIMLET = ITEMS.register("sky_dimlet", tab(() -> new DimletItem(DimletType.SKY, true)));
    public static final DeferredItem<DimletItem> ADMIN_DIMLET = ITEMS.register("admin_dimlet", tab(() -> new DimletItem(DimletType.ADMIN, true)));

    public static final DeferredItem<PartItem> PART_ENERGY_0 = ITEMS.register("part_energy_0", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_ENERGY_1 = ITEMS.register("part_energy_1", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_ENERGY_2 = ITEMS.register("part_energy_2", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_ENERGY_3 = ITEMS.register("part_energy_3", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_MEMORY_0 = ITEMS.register("part_memory_0", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_MEMORY_1 = ITEMS.register("part_memory_1", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_MEMORY_2 = ITEMS.register("part_memory_2", tab(PartItem::new));
    public static final DeferredItem<PartItem> PART_MEMORY_3 = ITEMS.register("part_memory_3", tab(PartItem::new));

    public static final DeferredItem<Item> COMMON_ESSENCE = ITEMS.register("common_essence", tab(() -> new Item(Registration.createStandardProperties())));
    public static final DeferredItem<Item> RARE_ESSENCE = ITEMS.register("rare_essence", tab(() -> new Item(Registration.createStandardProperties())));
    public static final DeferredItem<Item> LEGENDARY_ESSENCE = ITEMS.register("legendary_essence", tab(() -> new Item(Registration.createStandardProperties())));

    public static final Supplier<Codec<? extends IGlobalLootModifier>> ENDERMAN_LOOT_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register("enderman_extra", () -> EndermanLootModifier.CODEC);
    public static final Supplier<DimletRecipeSerializer> DIMLET_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("dimlet_recipe", DimletRecipeSerializer::new);
    public static final Supplier<DimletCycleRecipeSerializer> DIMLET_CYCLE_SERIALIZER = RECIPE_SERIALIZERS.register("dimlet_cycle_recipe", DimletCycleRecipeSerializer::new);

    public static LootItemConditionType LOOT_TABLE_CONDITION;
    public static LootPoolEntryType DIMLET_LOOT_ENTRY;

    @Override
    public void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerLootHelpers();
        });
    }

    public static void registerLootHelpers() {
        LOOT_TABLE_CONDITION = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(RFToolsDim.MODID, "check_tables"), new LootItemConditionType(new LootTableCondition.Serializer()));
        DIMLET_LOOT_ENTRY = Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(RFToolsDim.MODID, "dimlet_loot"), new LootPoolEntryType(new DimletLootEntry.Serializer()));
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {
        DimletConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        registerLootHelpers();
        dataGen.add(
                Dob.itemBuilder(EMPTY_DIMLET)
                        .generatedItem("item/dimlets/empty_dimlet")
                        .shaped(builder -> builder
                                        .define('s', mcjty.rftoolsbase.modules.various.VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                " p ", "psp", " p ")
                        .loot(p -> {
                            LootPool.Builder builder = LootPool.lootPool()
                                    .name(DimensionRegistry.HUT_LOOT.getPath())
                                    .setRolls(UniformGenerator.between(1, 5))
                                    .add(DimletLootEntry.builder(DimletRarity.COMMON)
                                            .setWeight(14)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 2))))
                                    .add(DimletLootEntry.builder(DimletRarity.UNCOMMON)
                                            .setWeight(4)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 2))))
                                    .add(DimletLootEntry.builder(DimletRarity.RARE)
                                            .setWeight(2)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 1))))
                                    .add(DimletLootEntry.builder(DimletRarity.LEGENDARY)
                                            .setWeight(1)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 1))))
                                    .add(LootItem.lootTableItem(COMMON_LOST_KNOWLEDGE.get())
                                            .setWeight(14)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 3))))
                                    .add(LootItem.lootTableItem(UNCOMMON_LOST_KNOWLEDGE.get())
                                            .setWeight(4)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 3))))
                                    .add(LootItem.lootTableItem(RARE_LOST_KNOWLEDGE.get())
                                            .setWeight(2)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 2))))
                                    .add(LootItem.lootTableItem(LEGENDARY_LOST_KNOWLEDGE.get())
                                            .setWeight(1)
                                            .apply(SetItemCountFunction
                                                    .setCount(UniformGenerator.between(0, 1))))
                                    ;
                            p.addChestLootTable(DimensionRegistry.HUT_LOOT, LootTable.lootTable().withPool(builder));

                        }),
                Dob.itemBuilder(EMPTY_TERRAIN_DIMLET)
                        .generatedItem("item/dimlets/empty_terrain_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Tags.Items.COBBLESTONE)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "CDC", "DED", "CDC"),
                Dob.itemBuilder(EMPTY_ATTRIBUTE_DIMLET)
                        .generatedItem("item/dimlets/empty_attribute_dimlet")
                        .shaped(builder -> builder
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "ppp", "pEp", "ppp"),
                Dob.itemBuilder(EMPTY_FEATURE_DIMLET)
                        .generatedItem("item/dimlets/empty_feature_dimlet")
                        .shaped(builder -> builder
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rcr", "cEc", "rcr"),
                Dob.itemBuilder(EMPTY_STRUCTURE_DIMLET)
                        .generatedItem("item/dimlets/empty_structure_dimlet")
                        .shaped(builder -> builder
                                        .define('C', ItemTags.STONE_BRICKS)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rCr", "CEC", "rCr"),
                Dob.itemBuilder(EMPTY_BIOME_DIMLET)
                        .generatedItem("item/dimlets/empty_biome_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Items.CLAY_BALL)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rCr", "CEC", "rCr"),
                Dob.itemBuilder(EMPTY_BIOME_CONTROLLER_DIMLET)
                        .generatedItem("item/dimlets/empty_biome_controller_dimlet")
                        .shaped(builder -> builder
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "DDD", "DED", "DOD"),
                Dob.itemBuilder(EMPTY_BIOME_CATEGORY_DIMLET)
                        .generatedItem("item/dimlets/empty_biome_category_dimlet")
                        .shaped(builder -> builder
                                        .define('C', ItemTags.SAPLINGS)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rCr", "CEC", "rCr"),
                Dob.itemBuilder(EMPTY_BLOCK_DIMLET)
                        .generatedItem("item/dimlets/empty_block_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Items.CLAY_BALL)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "CCC", "CEC", "CCC"),
                Dob.itemBuilder(EMPTY_FLUID_DIMLET)
                        .generatedItem("item/dimlets/empty_fluid_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Items.CLAY_BALL)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "CWC", "CEC", "CCC"),
                Dob.itemBuilder(EMPTY_TIME_DIMLET)
                        .generatedItem("item/dimlets/empty_time_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Items.CLOCK)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rCr", "CEC", "rCr"),
                Dob.itemBuilder(EMPTY_TAG_DIMLET)
                        .generatedItem("item/dimlets/empty_tag_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Items.PAPER)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rCr", "CEC", "rCr"),
                Dob.itemBuilder(EMPTY_SKY_DIMLET)
                        .generatedItem("item/dimlets/empty_sky_dimlet")
                        .shaped(builder -> builder
                                        .define('C', Items.BLUE_WOOL)
                                        .define('E', EMPTY_DIMLET.get())
                                        .unlockedBy("empty_dimlet", has(EMPTY_DIMLET.get())),
                                "rCr", "CEC", "rCr"),
                Dob.itemBuilder(TERRAIN_DIMLET)
                        .generatedItem("item/dimlets/terrain_dimlet"),
                Dob.itemBuilder(ATTRIBUTE_DIMLET)
                        .generatedItem("item/dimlets/attribute_dimlet"),
                Dob.itemBuilder(FEATURE_DIMLET)
                        .generatedItem("item/dimlets/feature_dimlet"),
                Dob.itemBuilder(STRUCTURE_DIMLET)
                        .generatedItem("item/dimlets/structure_dimlet"),
                Dob.itemBuilder(BIOME_DIMLET)
                        .generatedItem("item/dimlets/biome_dimlet"),
                Dob.itemBuilder(BIOME_CONTROLLER_DIMLET)
                        .generatedItem("item/dimlets/biome_controller_dimlet"),
                Dob.itemBuilder(BIOME_CATEGORY_DIMLET)
                        .generatedItem("item/dimlets/biome_category_dimlet"),
                Dob.itemBuilder(BLOCK_DIMLET)
                        .generatedItem("item/dimlets/block_dimlet"),
                Dob.itemBuilder(FLUID_DIMLET)
                        .generatedItem("item/dimlets/fluid_dimlet"),
                Dob.itemBuilder(TIME_DIMLET)
                        .generatedItem("item/dimlets/time_dimlet"),
                Dob.itemBuilder(DIGIT_DIMLET)
                        .generatedItem("item/dimlets/digit_dimlet")
                        .recipe(() -> DimletRecipeBuilder.shapedRecipe(DimletModule.DIGIT_DIMLET.get())
                                .define('E', EMPTY_DIMLET.get())
                                .define('C', Items.REDSTONE_TORCH)
                                .patternLine(" C ")
                                .patternLine("CEC")
                                .patternLine(" C ")
                                .dimletKey(new DimletKey(DimletType.DIGIT, "0"))
                                .addCriterion("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())))
                        .recipe("digit0", () -> DimletCycleRecipeBuilder.shapedRecipe(DimletModule.DIGIT_DIMLET.get())
                                .define('C', Ingredient.of(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "1"))))
                                .patternLine("C")
                                .input("9")
                                .output("0")
                                .addCriterion("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get()))),
                Dob.itemBuilder(TAG_DIMLET)
                        .generatedItem("item/dimlets/tag_dimlet"),
                Dob.itemBuilder(SKY_DIMLET)
                        .generatedItem("item/dimlets/sky_dimlet"),
                Dob.itemBuilder(ADMIN_DIMLET)
                        .generatedItem("item/dimlets/admin_dimlet"),
                Dob.itemBuilder(PART_ENERGY_0)
                        .generatedItem("item/parts/part_energy_0")
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('g', Tags.Items.DUSTS_GLOWSTONE)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "rRr", "RsR", "rgr"),
                Dob.itemBuilder(PART_ENERGY_1)
                        .generatedItem("item/parts/part_energy_1")
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('u', COMMON_ESSENCE.get())
                                        .define('M', PART_ENERGY_0.get())
                                        .unlockedBy("energy0", has(PART_ENERGY_0.get())),
                                "uRu", "RMR", "usu"),
                Dob.itemBuilder(PART_ENERGY_2)
                        .generatedItem("item/parts/part_energy_2")
                        .shaped(builder -> builder
                                        .define('u', RARE_ESSENCE.get())
                                        .define('U', VariousModule.INFUSED_ENDERPEARL.get())
                                        .define('M', PART_ENERGY_1.get())
                                        .unlockedBy("energy1", has(PART_ENERGY_1.get())),
                                "uRu", "RMR", "uUu"),
                Dob.itemBuilder(PART_ENERGY_3)
                        .generatedItem("item/parts/part_energy_3")
                        .shaped(builder -> builder
                                        .define('u', LEGENDARY_ESSENCE.get())
                                        .define('U', VariousModule.INFUSED_DIAMOND.get())
                                        .define('M', PART_ENERGY_2.get())
                                        .unlockedBy("energy2", has(PART_ENERGY_2.get())),
                                "uRu", "RMR", "uUu"),
                Dob.itemBuilder(PART_MEMORY_0)
                        .generatedItem("item/parts/part_memory_0")
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('g', Tags.Items.DUSTS_GLOWSTONE)
                                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                                "rlr", "lsl", "rgr"),
                Dob.itemBuilder(PART_MEMORY_1)
                        .generatedItem("item/parts/part_memory_1")
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('u', COMMON_ESSENCE.get())
                                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                                        .define('M', PART_MEMORY_0.get())
                                        .unlockedBy("memory0", has(PART_MEMORY_0.get())),
                                "ulu", "lMl", "usu"),
                Dob.itemBuilder(PART_MEMORY_2)
                        .generatedItem("item/parts/part_memory_2")
                        .shaped(builder -> builder
                                        .define('u', RARE_ESSENCE.get())
                                        .define('U', VariousModule.INFUSED_ENDERPEARL.get())
                                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                                        .define('M', PART_MEMORY_1.get())
                                        .unlockedBy("memory1", has(PART_MEMORY_1.get())),
                                "ulu", "lMl", "uUu"),
                Dob.itemBuilder(PART_MEMORY_3)
                        .generatedItem("item/parts/part_memory_3")
                        .shaped(builder -> builder
                                        .define('u', LEGENDARY_ESSENCE.get())
                                        .define('U', VariousModule.INFUSED_DIAMOND.get())
                                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                                        .define('M', PART_MEMORY_2.get())
                                        .unlockedBy("memory2", has(PART_MEMORY_2.get())),
                                "ulu", "lMl", "uUu"),
                Dob.itemBuilder(COMMON_ESSENCE)
                        .generatedItem("item/parts/common_essence"),
                Dob.itemBuilder(RARE_ESSENCE)
                        .generatedItem("item/parts/rare_essence"),
                Dob.itemBuilder(LEGENDARY_ESSENCE)
                        .generatedItem("item/parts/legendary_essence")
        );
        for (int i = 1; i <= 9; i++) {
            int finalI = i;
            dataGen.add(
                    Dob.itemBuilder(DIGIT_DIMLET)
                            .recipe("digit" + i, () -> DimletCycleRecipeBuilder.shapedRecipe(DimletModule.DIGIT_DIMLET.get())
                                    .define('C', Ingredient.of(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "0"))))
                                    .patternLine("C")
                                    .input(String.valueOf(finalI - 1))
                                    .output(String.valueOf(finalI))
                                    .addCriterion("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())))
            );
        }

    }
}
