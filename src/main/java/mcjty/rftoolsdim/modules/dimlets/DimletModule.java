package mcjty.rftoolsdim.modules.dimlets;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.PartItem;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.DimletLootEntry;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.EndermanLootModifier;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.LootTableCondition;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletCycleRecipeSerializer;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletRecipeSerializer;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.*;

public class DimletModule implements IModule {

    public static final RegistryObject<DimletItem> EMPTY_DIMLET = ITEMS.register("empty_dimlet", () -> new DimletItem(null, false));

    public static final RegistryObject<DimletItem> EMPTY_TERRAIN_DIMLET = ITEMS.register("empty_terrain_dimlet", () -> new DimletItem(DimletType.TERRAIN, false));
    public static final RegistryObject<DimletItem> EMPTY_ATTRIBUTE_DIMLET = ITEMS.register("empty_attribute_dimlet", () -> new DimletItem(DimletType.ATTRIBUTE, false));
    public static final RegistryObject<DimletItem> EMPTY_FEATURE_DIMLET = ITEMS.register("empty_feature_dimlet", () -> new DimletItem(DimletType.FEATURE, false));
    public static final RegistryObject<DimletItem> EMPTY_STRUCTURE_DIMLET = ITEMS.register("empty_structure_dimlet", () -> new DimletItem(DimletType.STRUCTURE, false));
    public static final RegistryObject<DimletItem> EMPTY_BIOME_DIMLET = ITEMS.register("empty_biome_dimlet", () -> new DimletItem(DimletType.BIOME, false));
    public static final RegistryObject<DimletItem> EMPTY_BIOME_CONTROLLER_DIMLET = ITEMS.register("empty_biome_controller_dimlet", () -> new DimletItem(DimletType.BIOME_CONTROLLER, false));
    public static final RegistryObject<DimletItem> EMPTY_BIOME_CATEGORY_DIMLET = ITEMS.register("empty_biome_category_dimlet", () -> new DimletItem(DimletType.BIOME_CATEGORY, false));
    public static final RegistryObject<DimletItem> EMPTY_BLOCK_DIMLET = ITEMS.register("empty_block_dimlet", () -> new DimletItem(DimletType.BLOCK, false));
    public static final RegistryObject<DimletItem> EMPTY_FLUID_DIMLET = ITEMS.register("empty_fluid_dimlet", () -> new DimletItem(DimletType.FLUID, false));
    public static final RegistryObject<DimletItem> EMPTY_TIME_DIMLET = ITEMS.register("empty_time_dimlet", () -> new DimletItem(DimletType.TIME, false));
    public static final RegistryObject<DimletItem> EMPTY_TAG_DIMLET = ITEMS.register("empty_tag_dimlet", () -> new DimletItem(DimletType.TAG, false));
    public static final RegistryObject<DimletItem> EMPTY_SKY_DIMLET = ITEMS.register("empty_sky_dimlet", () -> new DimletItem(DimletType.SKY, false));

    public static final RegistryObject<DimletItem> TERRAIN_DIMLET = ITEMS.register("terrain_dimlet", () -> new DimletItem(DimletType.TERRAIN, true));
    public static final RegistryObject<DimletItem> ATTRIBUTE_DIMLET = ITEMS.register("attribute_dimlet", () -> new DimletItem(DimletType.ATTRIBUTE, true));
    public static final RegistryObject<DimletItem> FEATURE_DIMLET = ITEMS.register("feature_dimlet", () -> new DimletItem(DimletType.FEATURE, true));
    public static final RegistryObject<DimletItem> STRUCTURE_DIMLET = ITEMS.register("structure_dimlet", () -> new DimletItem(DimletType.STRUCTURE, true));
    public static final RegistryObject<DimletItem> BIOME_DIMLET = ITEMS.register("biome_dimlet", () -> new DimletItem(DimletType.BIOME, true));
    public static final RegistryObject<DimletItem> BIOME_CONTROLLER_DIMLET = ITEMS.register("biome_controller_dimlet", () -> new DimletItem(DimletType.BIOME_CONTROLLER, true));
    public static final RegistryObject<DimletItem> BIOME_CATEGORY_DIMLET = ITEMS.register("biome_category_dimlet", () -> new DimletItem(DimletType.BIOME_CATEGORY, true));
    public static final RegistryObject<DimletItem> BLOCK_DIMLET = ITEMS.register("block_dimlet", () -> new DimletItem(DimletType.BLOCK, true));
    public static final RegistryObject<DimletItem> FLUID_DIMLET = ITEMS.register("fluid_dimlet", () -> new DimletItem(DimletType.FLUID, true));
    public static final RegistryObject<DimletItem> TIME_DIMLET = ITEMS.register("time_dimlet", () -> new DimletItem(DimletType.TIME, true));
    public static final RegistryObject<DimletItem> DIGIT_DIMLET = ITEMS.register("digit_dimlet", () -> new DimletItem(DimletType.DIGIT, true));
    public static final RegistryObject<DimletItem> TAG_DIMLET = ITEMS.register("tag_dimlet", () -> new DimletItem(DimletType.TAG, true));
    public static final RegistryObject<DimletItem> SKY_DIMLET = ITEMS.register("sky_dimlet", () -> new DimletItem(DimletType.SKY, true));
    public static final RegistryObject<DimletItem> ADMIN_DIMLET = ITEMS.register("admin_dimlet", () -> new DimletItem(DimletType.ADMIN, true));

    public static final RegistryObject<PartItem> PART_ENERGY_0 = ITEMS.register("part_energy_0", PartItem::new);
    public static final RegistryObject<PartItem> PART_ENERGY_1 = ITEMS.register("part_energy_1", PartItem::new);
    public static final RegistryObject<PartItem> PART_ENERGY_2 = ITEMS.register("part_energy_2", PartItem::new);
    public static final RegistryObject<PartItem> PART_ENERGY_3 = ITEMS.register("part_energy_3", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_0 = ITEMS.register("part_memory_0", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_1 = ITEMS.register("part_memory_1", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_2 = ITEMS.register("part_memory_2", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_3 = ITEMS.register("part_memory_3", PartItem::new);

    public static final RegistryObject<Item> COMMON_ESSENCE = ITEMS.register("common_essence", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> RARE_ESSENCE = ITEMS.register("rare_essence", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> LEGENDARY_ESSENCE = ITEMS.register("legendary_essence", () -> new Item(Registration.createStandardProperties()));

    public static final RegistryObject<EndermanLootModifier.Serializer> ENDERMAN_LOOT_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register("enderman_extra", EndermanLootModifier.Serializer::new);
    public static final RegistryObject<DimletRecipeSerializer> DIMLET_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("dimlet_recipe", DimletRecipeSerializer::new);
    public static final RegistryObject<DimletCycleRecipeSerializer> DIMLET_CYCLE_SERIALIZER = RECIPE_SERIALIZERS.register("dimlet_cycle_recipe", DimletCycleRecipeSerializer::new);

    public static LootItemConditionType LOOT_TABLE_CONDITION;
    public static LootPoolEntryType DIMLET_LOOT_ENTRY;

    @Override
    public void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerLootHelpers();
        });
    }

    public static void registerLootHelpers() {
        LOOT_TABLE_CONDITION = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(RFToolsDim.MODID, "check_tables"), new LootItemConditionType(new LootTableCondition.Serializer()));
        DIMLET_LOOT_ENTRY = Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(RFToolsDim.MODID, "dimlet_loot"), new LootPoolEntryType(new DimletLootEntry.Serializer()));
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        DimletConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
