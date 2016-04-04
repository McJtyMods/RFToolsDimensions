package mcjty.rftoolsdim.dimensions.dimlets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.DimletRules;
import mcjty.rftoolsdim.config.Filter;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.*;
import mcjty.rftoolsdim.dimensions.world.BiomeControllerMapping;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.varia.RFToolsTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Modifier;
import java.util.*;

public class KnownDimletConfiguration {

    private static Set<DimletKey> craftableDimlets = new HashSet<>();
    private static Map<DimletKey, Settings> knownDimlets = new HashMap<>();

    public static Settings getSettings(DimletKey key) {
        initDimlets();
        return knownDimlets.get(key);
    }

    public static Map<DimletKey, Settings> getKnownDimlets() {
        initDimlets();
        return knownDimlets;
    }

    public static void init() {
        knownDimlets.clear();
        craftableDimlets.clear();
    }

    private static void initDimlets() {
        if (!knownDimlets.isEmpty()) {
            return;
        }

        for (int i = 0 ; i <= 9 ; i++) {
            initDimlet(new DimletKey(DimletType.DIMLET_DIGIT, Integer.toString(i)), RFToolsDim.MODID);
        }
        Arrays.stream(TerrainType.values()).forEach(t -> initDimlet(new DimletKey(DimletType.DIMLET_TERRAIN, t.getId()), RFToolsDim.MODID));
        Arrays.stream(ControllerType.values()).forEach(t -> initDimlet(new DimletKey(DimletType.DIMLET_CONTROLLER, t.getId()), RFToolsDim.MODID));
        Arrays.stream(FeatureType.values()).forEach(t -> initDimlet(new DimletKey(DimletType.DIMLET_FEATURE, t.getId()), RFToolsDim.MODID));
        Arrays.stream(EffectType.values()).forEach(t -> initDimlet(new DimletKey(DimletType.DIMLET_EFFECT, t.getId()), RFToolsDim.MODID));
        Arrays.stream(StructureType.values()).forEach(t -> initDimlet(new DimletKey(DimletType.DIMLET_STRUCTURE, t.getId()), RFToolsDim.MODID));

        BiomeGenBase.biomeRegistry.iterator().forEachRemaining(KnownDimletConfiguration::initBiomeDimlet);

        EntityList.stringToClassMapping.entrySet().stream().forEach(KnownDimletConfiguration::initMobDimlet);
        FluidRegistry.getRegisteredFluids().entrySet().stream().forEach(KnownDimletConfiguration::initFluidDimlet);
        Block.blockRegistry.forEach(KnownDimletConfiguration::initMaterialDimlet);

        initDimlet(new DimletKey(DimletType.DIMLET_MATERIAL, Blocks.stone.getRegistryName() + "@0"), "minecraft");
        initDimlet(new DimletKey(DimletType.DIMLET_LIQUID, Blocks.water.getRegistryName() + "@0"), "minecraft");

        BiomeControllerMapping.setupControllerBiomes();
    }

    private static void initBiomeDimlet(BiomeGenBase biome) {
        String name = biome.getBiomeName();
        if (name != null && !name.isEmpty()) {
            DimletKey key = new DimletKey(DimletType.DIMLET_BIOME, biome.getRegistryName().toString());
            initDimlet(key, RFToolsTools.findModID(biome));
        }
    }

    private static void initMobDimlet(Map.Entry<String, Class<? extends Entity>> entry) {
        Class<? extends Entity> entityClass = entry.getValue();
        if (isValidMobClass(entityClass)) {
            DimletKey key = new DimletKey(DimletType.DIMLET_MOB, entry.getKey());
            initDimlet(key, RFToolsTools.findModID(entityClass));
        }
    }

    private static void initFluidDimlet(Map.Entry<String, Fluid> me) {
        if (me.getValue().canBePlacedInWorld()) {
            String name = me.getKey();
            if (name != null && !name.isEmpty()) {
                Block block = me.getValue().getBlock();
                if (block != null) {
                    ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(block);
                    if (nameForObject != null) {
                        String mod = nameForObject.getResourceDomain();
                        DimletKey key = new DimletKey(DimletType.DIMLET_LIQUID, block.getRegistryName() + "@0");
                        initDimlet(key, mod);
                    }
                }
            }
        }
    }

    private static void initDimlet(DimletKey key, String mod) {
        Settings settings = DimletRules.getSettings(key, mod);
        if (!settings.isBlacklisted()) {
            knownDimlets.put(key, settings);
        }
    }

    private static void initMaterialDimlet(Block block) {
        if (block instanceof BlockLiquid) {
            return;
        }

        Set<Filter.Feature> features = getBlockFeatures(block);

        ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(block);
        String mod = nameForObject.getResourceDomain();

        for (IBlockState state : block.getBlockState().getValidStates()) {
            int meta = state.getBlock().getMetaFromState(state);
            List<IProperty> propertyNames = new ArrayList<>(state.getPropertyNames());
            propertyNames.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

            ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getProperties();
            Map<String, String> props = new HashMap<>();
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet()) {
                props.put(entry.getKey().getName(), entry.getValue().toString());
            }
            DimletKey key = new DimletKey(DimletType.DIMLET_MATERIAL, block.getRegistryName() + "@" + meta);
            Settings settings = DimletRules.getSettings(key, mod, features, props);
            if (!settings.isBlacklisted()) {
                knownDimlets.put(key, settings);
            }
        }
    }

    public static Set<Filter.Feature> getBlockFeatures(Block block) {
        Set<Filter.Feature> features = EnumSet.noneOf(Filter.Feature.class);

        ItemStack stack = new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
        int[] iDs = null;
        if (stack.getItem() != null) {
            iDs = OreDictionary.getOreIDs(stack);
        }
        if (iDs != null && iDs.length > 0) {
            features.add(Filter.Feature.OREDICT);
        }
        if (block instanceof BlockFalling) {
            features.add(Filter.Feature.FALLING);
        }
        if (block.hasTileEntity(block.getDefaultState())) {
            features.add(Filter.Feature.TILEENTITY);
        }
        if (block instanceof IPlantable) {
            features.add(Filter.Feature.PLANTABLE);
        }
        if (!block.isFullBlock(block.getDefaultState())) {
            features.add(Filter.Feature.NOFULLBLOCK);
        }
        return features;
    }

    public static void dumpMobs() {
        EntityList.stringToClassMapping.entrySet().stream().forEach(KnownDimletConfiguration::dumpMob);
    }

    private static void dumpMob(Map.Entry<String, Class<? extends Entity>> entry) {
        Class<? extends Entity> entityClass = entry.getValue();
        if (isValidMobClass(entityClass)) {
            DimletKey key = new DimletKey(DimletType.DIMLET_MOB, entry.getKey());
            String mod = RFToolsTools.findModID(entityClass);
            Settings settings = DimletRules.getSettings(key, mod);
            String name = EntityList.classToStringMapping.get(entityClass);
            if (name == null) {
                name = "generic";
            }
            String readableName = I18n.format("entity." + name + ".name");
            Logging.log(key + " (" + name + ", " + readableName + "): " + settings.toString());
        }
    }

    private static boolean isValidMobClass(Class<? extends Entity> entityClass) {
        if (!EntityLivingBase.class.isAssignableFrom(entityClass)) {
            return false;
        }
        if (Modifier.isAbstract(entityClass.getModifiers())) {
            return false;
        }
        return true;
    }


    public static ItemStack getDimletStack(DimletKey key) {
        ItemStack stack = new ItemStack(ModItems.knownDimletItem, 1, key.getType().ordinal());
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("dkey", key.getId());
        stack.setTagCompound(compound);
        return stack;
    }

    public static ItemStack getDimletStack(DimletType type, String id) {
        return getDimletStack(new DimletKey(type, id));
    }

    public static DimletKey getDimletKey(ItemStack dimletStack) {
        DimletType type = DimletType.values()[dimletStack.getItemDamage()];
        NBTTagCompound tagCompound = dimletStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("dkey")) {
            return new DimletKey(type, tagCompound.getString("dkey"));
        } else {
            return new DimletKey(type, null);
        }
    }

    public static boolean isBlacklisted(DimletKey key) {
        return KnownDimletConfiguration.getSettings(key) == null;
    }

    public static boolean isCraftable(DimletKey key) {
        if (craftableDimlets.isEmpty()) {
            registerCraftables();
        }
        return craftableDimlets.contains(key);
    }

    private static void registerCraftables() {
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_EFFECT, "None"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_FEATURE, "None"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_STRUCTURE, "None"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_TERRAIN, "Void"));
        if (!GeneralConfiguration.voidOnly) {
            craftableDimlets.add(new DimletKey(DimletType.DIMLET_TERRAIN, "Flat"));
        }
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_CONTROLLER, DimletObjectMapping.DEFAULT_ID));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_CONTROLLER, "Single"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_MATERIAL, Blocks.stone.getRegistryName() + "@0"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_LIQUID, Blocks.water.getRegistryName() + "@0"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_SKY, "Normal"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_SKY, "Normal Day"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_SKY, "Normal Night"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_MOB, DimletObjectMapping.DEFAULT_ID));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_TIME, "Normal"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_WEATHER, DimletObjectMapping.DEFAULT_ID));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "0"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "1"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "2"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "3"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "4"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "5"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "6"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "7"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "8"));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_DIGIT, "9"));
    }

    public static Set<DimletKey> getCraftableDimlets() {
        if (craftableDimlets.isEmpty()) {
            registerCraftables();
        }
        return craftableDimlets;
    }

    public static boolean isSeedDimlet(DimletKey key) {
        return key.getType() == DimletType.DIMLET_SPECIAL && "Seed".equals(key.getId());
    }

    public static String getDisplayName(DimletKey key) {
        switch (key.getType()) {
            case DIMLET_BIOME:
                BiomeGenBase biome = BiomeGenBase.biomeRegistry.getObject(new ResourceLocation(key.getId()));
                return biome == null ? "<invalid>" : biome.getBiomeName();
            case DIMLET_LIQUID:
                Block fluid = DimletObjectMapping.getFluid(key);
                if (fluid != null) {
                    return fluid.getLocalizedName();
                }
                break;
            case DIMLET_MATERIAL:
                IBlockState state = DimletObjectMapping.getBlock(key);
                if (state != null) {
                    ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
                    try {
                        return stack.getItem() == null ? "?" : stack.getDisplayName();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "<Bug>";
                    }
                }
                break;
            case DIMLET_MOB:
                Class<? extends Entity> entityClass = EntityList.stringToClassMapping.get(key.getId());
                if (entityClass == null) {
                    return "<Unknown>";
                }
                return I18n.format("entity." + key.getId() + ".name");
            case DIMLET_SKY:
                return "sky"; //@todo
            case DIMLET_STRUCTURE:
                return key.getId();
            case DIMLET_TERRAIN:
                return key.getId();
            case DIMLET_FEATURE:
                return key.getId();
            case DIMLET_TIME:
                return "time";//@todo
            case DIMLET_DIGIT:
                return key.getId();
            case DIMLET_EFFECT:
                return key.getId();
            case DIMLET_SPECIAL:
                return key.getId();
            case DIMLET_CONTROLLER:
                return key.getId();
            case DIMLET_WEATHER:
                return "weather"; //@todo
            case DIMLET_PATREON:
                return key.getId();
        }
        return "Unknown";
    }

    public static void setupChestLoot() {
        //@todo
//        setupChestLoot(ChestGenHooks.DUNGEON_CHEST);
//        setupChestLoot(ChestGenHooks.MINESHAFT_CORRIDOR);
//        setupChestLoot(ChestGenHooks.PYRAMID_DESERT_CHEST);
//        setupChestLoot(ChestGenHooks.PYRAMID_JUNGLE_CHEST);
//        setupChestLoot(ChestGenHooks.STRONGHOLD_CORRIDOR);
//        setupChestLoot(ChestGenHooks.STRONGHOLD_CROSSING);
//        setupChestLoot(ChestGenHooks.STRONGHOLD_LIBRARY);
//        setupChestLoot(ChestGenHooks.VILLAGE_BLACKSMITH);
//        setupChestLoot(ChestGenHooks.NETHER_FORTRESS);
    }

//    private static void setupChestLoot(String category) {
//        List<List<ItemStack>> items = getRandomPartLists();
//
//        ChestGenHooks chest = ChestGenHooks.getInfo(category);
//        chest.addItem(new WeightedRandomChestContent(ModItems.dimletParcelItem, 0, 1, 2, WorldgenConfiguration.dimletParcelRarity));
//    }
//
    private static List<List<ItemStack>> randomPartLists = null;

    public static List<List<ItemStack>> getRandomPartLists() {
        if (randomPartLists == null) {
            randomPartLists = new ArrayList<>();
            randomPartLists.add(Lists.newArrayList(new ItemStack(ModItems.dimletBaseItem), new ItemStack(ModItems.dimletControlCircuitItem, 1, 0), new ItemStack(ModItems.dimletEnergyModuleItem)));
            randomPartLists.add(Lists.newArrayList(new ItemStack(ModItems.dimletControlCircuitItem, 1, 1), new ItemStack(ModItems.dimletEnergyModuleItem, 1, 0), new ItemStack(ModItems.dimletMemoryUnitItem, 1, 0)));
            randomPartLists.add(Lists.newArrayList(new ItemStack(ModItems.dimletControlCircuitItem, 1, 2)));
            ArrayList<ItemStack> list3 = Lists.newArrayList(new ItemStack(ModItems.dimletControlCircuitItem, 1, 3), new ItemStack(ModItems.dimletEnergyModuleItem, 1, 1), new ItemStack(ModItems.dimletMemoryUnitItem, 1, 1));
            for (DimletType type : DimletType.values()) {
                list3.add(new ItemStack(ModItems.dimletTypeControllerItem, 1, type.ordinal()));
            }
            randomPartLists.add(list3);
            randomPartLists.add(Lists.newArrayList(new ItemStack(ModItems.dimletControlCircuitItem, 1, 4)));
            randomPartLists.add(Lists.newArrayList(new ItemStack(ModItems.dimletControlCircuitItem, 1, 5), new ItemStack(ModItems.dimletEnergyModuleItem, 1, 2), new ItemStack(ModItems.dimletMemoryUnitItem, 1, 2)));
            randomPartLists.add(Lists.newArrayList(new ItemStack(ModItems.dimletControlCircuitItem, 1, 6)));
        }
        return randomPartLists;
    }

}
