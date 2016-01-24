package mcjty.rftoolsdim.dimensions.dimlets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.*;
import mcjty.rftoolsdim.dimensions.description.MobDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.types.StructureType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

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

        for (ControllerType type : ControllerType.values()) {
            initDimlet(new DimletKey(DimletType.DIMLET_CONTROLLER, type.getId()), RFToolsDim.MODID);
        }
        for (FeatureType type : FeatureType.values()) {
            initDimlet(new DimletKey(DimletType.DIMLET_FEATURE, type.getId()), RFToolsDim.MODID);
        }
        for (EffectType type : EffectType.values()) {
            initDimlet(new DimletKey(DimletType.DIMLET_EFFECT, type.getId()), RFToolsDim.MODID);
        }
        for (StructureType type : StructureType.values()) {
            initDimlet(new DimletKey(DimletType.DIMLET_STRUCTURE, type.getId()), RFToolsDim.MODID);
        }
        for (int i = 0 ; i <= 9 ; i++) {
            initDimlet(new DimletKey(DimletType.DIMLET_DIGIT, Integer.toString(i)), RFToolsDim.MODID);
        }

        BiomeGenBase[] biomeGenArray = BiomeGenBase.getBiomeGenArray();
        for (BiomeGenBase biome : biomeGenArray) {
            if (biome != null) {
                String name = biome.biomeName;
                if (name != null && !name.isEmpty()) {
                    DimletKey key = new DimletKey(DimletType.DIMLET_BIOME, Integer.toString(biome.biomeID));
                    initDimlet(key, "minecraft");
                }
            }
        }

        Map<String,Fluid> fluidMap = FluidRegistry.getRegisteredFluids();
        for (Map.Entry<String,Fluid> me : fluidMap.entrySet()) {
            if (me.getValue().canBePlacedInWorld()) {
                String name = me.getKey();
                if (name != null && !name.isEmpty()) {
                    Block block = me.getValue().getBlock();
                    if (block != null) {
                        ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(block);
                        String mod = nameForObject.getResourceDomain();
                        DimletKey key = new DimletKey(DimletType.DIMLET_LIQUID, block.getRegistryName() + "@0");
                        initDimlet(key, mod);
                    }
                }
            }
        }

        Block.blockRegistry.forEach(KnownDimletConfiguration::initMaterialDimlet);
    }

    public static void dumpBlocks() {
        Block.blockRegistry.forEach(KnownDimletConfiguration::dumpBlock);
    }

    private static void dumpBlock(Block block) {
        if (block instanceof BlockLiquid) {
            return;
        }
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
        if (!block.isFullBlock()) {
            features.add(Filter.Feature.NOFULLBLOCK);
        }

        String mod = Block.blockRegistry.getNameForObject(block).getResourceDomain();

        for (IBlockState state : block.getBlockState().getValidStates()) {
            int meta = state.getBlock().getMetaFromState(state);
            List<IProperty> propertyNames = new ArrayList<>(state.getPropertyNames());
            propertyNames.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

            ImmutableMap<IProperty, Comparable> properties = state.getProperties();
            Map<String, String> props = new HashMap<>();
            for (Map.Entry<IProperty, Comparable> entry : properties.entrySet()) {
                props.put(entry.getKey().getName(), entry.getValue().toString());
            }
            DimletKey key = new DimletKey(DimletType.DIMLET_MATERIAL, block.getRegistryName() + "@" + meta);
            Settings settings = DimletRules.getSettings(key, mod, features, props);
            Logging.log(key + " (" + state.toString()+ "): " + settings.toString());
        }

    }

    public static void dumpDimlets() {
        initDimlets();
        List<DimletKey> keys = new ArrayList<>(knownDimlets.keySet());
        keys.sort((o1, o2) -> {
            int i = o1.getType().compareTo(o2.getType());
            if (i != 0) {
                return i;
            }
            if (o1.getId() != null && o2.getId() != null) {
                return o1.getId().compareTo(o2.getId());
            }
            if (o1.getId() == null && o2.getId() == null) {
                return 0;
            }
            if (o1.getId() == null) {
                return -1;
            }
            return 1;
        });
        for (DimletKey key : keys) {
            Settings value = knownDimlets.get(key);
            Logging.log(key + ": " + value);
        }

    }

    private static void initDimlet(DimletKey key, String mod) {
        Settings settings = DimletRules.getSettings(key, mod);
        if (!settings.isBlacklisted()) {
            knownDimlets.put(key, settings);// new DimletEntry(key, settings));
        }
    }

    private static void initMaterialDimlet(Block block) {
        if (block instanceof BlockLiquid) {
            return;
        }
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
        if (!block.isFullBlock()) {
            features.add(Filter.Feature.NOFULLBLOCK);
        }

        ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(block);
        String mod = nameForObject.getResourceDomain();

        for (IBlockState state : block.getBlockState().getValidStates()) {
            int meta = state.getBlock().getMetaFromState(state);
            List<IProperty> propertyNames = new ArrayList<>(state.getPropertyNames());
            propertyNames.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

            ImmutableMap<IProperty, Comparable> properties = state.getProperties();
            Map<String, String> props = new HashMap<>();
            for (Map.Entry<IProperty, Comparable> entry : properties.entrySet()) {
                props.put(entry.getKey().getName(), entry.getValue().toString());
            }
            DimletKey key = new DimletKey(DimletType.DIMLET_MATERIAL, block.getRegistryName() + "@" + meta);
            Settings settings = DimletRules.getSettings(key, mod, features, props);
            if (!settings.isBlacklisted()) {
                knownDimlets.put(key, settings);//new DimletEntry(key, settings));
            }
        }
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
        // @todo
        return false;
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
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_MATERIAL, DimletObjectMapping.DEFAULT_ID));
        craftableDimlets.add(new DimletKey(DimletType.DIMLET_LIQUID, DimletObjectMapping.DEFAULT_ID));
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
                BiomeGenBase biome = DimletObjectMapping.getBiome(key);
                if (biome != null) {
                    return biome.biomeName;
                }
                break;
            case DIMLET_LIQUID:
                Block fluid = DimletObjectMapping.getFluid(key);
                if (fluid != null) {
                    return new ItemStack(fluid).getDisplayName();
                }
                break;
            case DIMLET_MATERIAL:
                IBlockState state = DimletObjectMapping.getBlock(key);
                if (state != null) {
                    return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)).getDisplayName();
                }
                break;
            case DIMLET_MOB:
                MobDescriptor mob = DimletObjectMapping.getMob(key);
                if (mob != null) {
                    return "mob"; //@todo
                }
                break;
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
        setupChestLoot(ChestGenHooks.DUNGEON_CHEST);
        setupChestLoot(ChestGenHooks.MINESHAFT_CORRIDOR);
        setupChestLoot(ChestGenHooks.PYRAMID_DESERT_CHEST);
        setupChestLoot(ChestGenHooks.PYRAMID_JUNGLE_CHEST);
        setupChestLoot(ChestGenHooks.STRONGHOLD_CORRIDOR);
        setupChestLoot(ChestGenHooks.STRONGHOLD_CROSSING);
        setupChestLoot(ChestGenHooks.STRONGHOLD_LIBRARY);
        setupChestLoot(ChestGenHooks.VILLAGE_BLACKSMITH);
        setupChestLoot(ChestGenHooks.NETHER_FORTRESS);
    }

    private static void setupChestLoot(String category) {
        List<List<ItemStack>> items = getRandomPartLists();

        ChestGenHooks chest = ChestGenHooks.getInfo(category);
        for (int i = 0 ; i <= 6 ; i++) {
            if (WorldgenConfiguration.dimletPartChestLootRarity[i] > 0) {
                for (ItemStack stack : items.get(i)) {
                    chest.addItem(new WeightedRandomChestContent(stack,
                            WorldgenConfiguration.dimletPartChestLootMinimum,
                            WorldgenConfiguration.dimletPartChestLootMaximum,
                            WorldgenConfiguration.dimletPartChestLootRarity[i]));
                }
            }
        }
    }

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
