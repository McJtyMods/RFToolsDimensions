package mcjty.rftoolsdim.dimensions.dimlets;

import com.google.common.collect.Lists;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KnownDimletConfiguration {

    private static Set<DimletKey> craftableDimlets = new HashSet<>();

    public static DimletEntry getEntry(DimletKey key) {
        // @todo
        return new DimletEntry(key, 10, 1, 10, 1, false, false);
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

    public static boolean isSeedDimlet(DimletEntry entry) {
        if (entry == null) {
            return false;
        }
        // @todo
        return entry.getKey().getType() == DimletType.DIMLET_SPECIAL && "Seed".equals(entry.getKey().getId());
    }

    public static String getDisplayName(DimletKey key) {
        // @todo
        return key.getId();
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
