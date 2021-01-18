package mcjty.rftoolsdim.modules.dimlets;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.PartItem;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class DimletModule implements IModule {

    public static final RegistryObject<DimletItem> EMPTY_DIMLET = Registration.ITEMS.register("empty_dimlet", () -> new DimletItem(null, false));

    public static final RegistryObject<DimletItem> EMPTY_TERRAIN_DIMLET = Registration.ITEMS.register("empty_terrain_dimlet", () -> new DimletItem(DimletType.TERRAIN, false));
    public static final RegistryObject<DimletItem> EMPTY_FEATURE_DIMLET = Registration.ITEMS.register("empty_feature_dimlet", () -> new DimletItem(DimletType.FEATURE, false));
    public static final RegistryObject<DimletItem> EMPTY_BIOME_DIMLET = Registration.ITEMS.register("empty_biome_dimlet", () -> new DimletItem(DimletType.BIOME, false));
    public static final RegistryObject<DimletItem> EMPTY_BIOME_CONTROLLER_DIMLET = Registration.ITEMS.register("empty_biome_controller_dimlet", () -> new DimletItem(DimletType.BIOME_CONTROLLER, false));
    public static final RegistryObject<DimletItem> EMPTY_BLOCK_DIMLET = Registration.ITEMS.register("empty_block_dimlet", () -> new DimletItem(DimletType.BLOCK, false));

    public static final RegistryObject<DimletItem> TERRAIN_DIMLET = Registration.ITEMS.register("terrain_dimlet", () -> new DimletItem(DimletType.TERRAIN, true));
    public static final RegistryObject<DimletItem> FEATURE_DIMLET = Registration.ITEMS.register("feature_dimlet", () -> new DimletItem(DimletType.FEATURE, true));
    public static final RegistryObject<DimletItem> BIOME_DIMLET = Registration.ITEMS.register("biome_dimlet", () -> new DimletItem(DimletType.BIOME, true));
    public static final RegistryObject<DimletItem> BIOME_CONTROLLER_DIMLET = Registration.ITEMS.register("biome_controller_dimlet", () -> new DimletItem(DimletType.BIOME_CONTROLLER, true));
    public static final RegistryObject<DimletItem> BLOCK_DIMLET = Registration.ITEMS.register("block_dimlet", () -> new DimletItem(DimletType.BLOCK, true));

    public static final RegistryObject<PartItem> PART_ENERGY_0 = Registration.ITEMS.register("part_energy_0", PartItem::new);
    public static final RegistryObject<PartItem> PART_ENERGY_1 = Registration.ITEMS.register("part_energy_1", PartItem::new);
    public static final RegistryObject<PartItem> PART_ENERGY_2 = Registration.ITEMS.register("part_energy_2", PartItem::new);
    public static final RegistryObject<PartItem> PART_ENERGY_3 = Registration.ITEMS.register("part_energy_3", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_0 = Registration.ITEMS.register("part_memory_0", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_1 = Registration.ITEMS.register("part_memory_1", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_2 = Registration.ITEMS.register("part_memory_2", PartItem::new);
    public static final RegistryObject<PartItem> PART_MEMORY_3 = Registration.ITEMS.register("part_memory_3", PartItem::new);

    public static final RegistryObject<Item> COMMON_ESSENCE = Registration.ITEMS.register("common_essence", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> RARE_ESSENCE = Registration.ITEMS.register("rare_essence", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> LEGENDARY_ESSENCE = Registration.ITEMS.register("legendary_essence", () -> new Item(Registration.createStandardProperties()));

    @Override
    public void init(FMLCommonSetupEvent event) {
        DimletDictionary.get().readPackage("base.json");
        DimletDictionary.get().readPackage("vanilla_blocks.json");
        DimletDictionary.get().readPackage("vanilla_biomes.json");
        DimletDictionary.get().readPackage("rftools.json");
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {

    }
}
