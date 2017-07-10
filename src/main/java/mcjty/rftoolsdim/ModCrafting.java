package mcjty.rftoolsdim;

import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModCrafting {

    public static void init() {
        initDimletRecipes();
        initEssenceRecipes();

        Item dimensionalShard = ForgeRegistries.ITEMS.getValue(new ResourceLocation("rftools", "dimensional_shard"));

        ItemStack inkSac = new ItemStack(Items.DYE, 1, 0);
    }

    private static void initEssenceRecipes() {
        String[] pickMatcher = new String[]{"ench"};

        ItemStack diamondPick = createEnchantedItem(Items.DIAMOND_PICKAXE, Enchantments.EFFICIENCY, 3);
        ForgeRegistries.RECIPES.register(new NBTMatchingRecipe(3, 3,
                new ItemStack[]{ItemStack.EMPTY, diamondPick, ItemStack.EMPTY,
                        new ItemStack(Items.ENDER_EYE), new ItemStack(Items.NETHER_STAR), new ItemStack(Items.ENDER_EYE),
                        ItemStack.EMPTY, new ItemStack(Items.ENDER_EYE), ItemStack.EMPTY},
                new String[][]{null, pickMatcher, null, null, null, null, null, null, null},
                new ItemStack(ModItems.efficiencyEssenceItem)).setRegistryName(new ResourceLocation(RFToolsDim.MODID, "efficiency")));

        ItemStack ironPick = createEnchantedItem(Items.IRON_PICKAXE, Enchantments.EFFICIENCY, 2);
        ForgeRegistries.RECIPES.register(new NBTMatchingRecipe(3, 3,
                new ItemStack[]{ItemStack.EMPTY, ironPick, ItemStack.EMPTY,
                        new ItemStack(Items.ENDER_EYE), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.ENDER_EYE),
                        ItemStack.EMPTY, new ItemStack(Items.ENDER_EYE), ItemStack.EMPTY},
                new String[][]{null, pickMatcher, null, null, null, null, null, null, null},
                new ItemStack(ModItems.mediocreEfficiencyEssenceItem)).setRegistryName(new ResourceLocation(RFToolsDim.MODID, "mediocre")));
    }

    public static ItemStack createEnchantedItem(Item item, Enchantment enchantment, int amount) {
        ItemStack stack = new ItemStack(item);
        Map<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(enchantment, amount);
        EnchantmentHelper.setEnchantments(enchant, stack);
        return stack;
    }

    private static void initDimletRecipes() {
        addDigitRecipe("0", "1");
        addDigitRecipe("1", "2");
        addDigitRecipe("2", "3");
        addDigitRecipe("3", "4");
        addDigitRecipe("4", "5");
        addDigitRecipe("5", "6");
        addDigitRecipe("6", "7");
        addDigitRecipe("7", "8");
        addDigitRecipe("8", "9");
        addDigitRecipe("9", "0");
    }

    private static void addDigitRecipe(String source, String dest) {
        ForgeRegistries.RECIPES.register(new NBTMatchingRecipe(1, 1,
                new ItemStack[]{KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, source)},
                new String[][]{new String[]{"dkey"}},
                KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, dest))
                .setRegistryName(new ResourceLocation(RFToolsDim.MODID, "digit" + dest)));
    }
}
