package mcjty.rftoolsdim;

import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.HashMap;
import java.util.Map;

public class ModCrafting {
    static {
        RecipeSorter.register("rftoolsdim:nbtmatchingrecipe", NBTMatchingRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
    }

    public static void init() {
        initDimletRecipes();
        initEssenceRecipes();

        Item dimensionalShard = ForgeRegistries.ITEMS.getValue(new ResourceLocation("rftools", "dimensional_shard"));

        ItemStack inkSac = new ItemStack(Items.DYE, 1, 0);
    }

    private static void initEssenceRecipes() {
        String[] pickMatcher = new String[] { "ench" };

        ItemStack diamondPick = createEnchantedItem(Items.DIAMOND_PICKAXE, Enchantments.EFFICIENCY, 3);
//        MyGameReg.addRecipe(new NBTMatchingRecipe(3, 3,
//                new ItemStack[] {ItemStackTools.getEmptyStack(), diamondPick, ItemStackTools.getEmptyStack(),
//                        new ItemStack(Items.ENDER_EYE), new ItemStack(Items.NETHER_STAR), new ItemStack(Items.ENDER_EYE),
//                        ItemStackTools.getEmptyStack(), new ItemStack(Items.ENDER_EYE), ItemStackTools.getEmptyStack()},
//                new String[][] {null, pickMatcher, null, null, null, null, null, null, null},
//                new ItemStack(ModItems.efficiencyEssenceItem)));

        ItemStack ironPick = createEnchantedItem(Items.IRON_PICKAXE, Enchantments.EFFICIENCY, 2);
//        MyGameReg.addRecipe(new NBTMatchingRecipe(3, 3,
//                new ItemStack[] {ItemStackTools.getEmptyStack(), ironPick, ItemStackTools.getEmptyStack(),
//                        new ItemStack(Items.ENDER_EYE), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.ENDER_EYE),
//                        ItemStackTools.getEmptyStack(), new ItemStack(Items.ENDER_EYE), ItemStackTools.getEmptyStack()},
//                new String[][] {null, pickMatcher, null, null, null, null, null, null, null},
//                new ItemStack(ModItems.mediocreEfficiencyEssenceItem)));
    }

    public static ItemStack createEnchantedItem(Item item, Enchantment enchantment, int amount) {
        ItemStack stack = new ItemStack(item);
        Map<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(enchantment, amount);
        EnchantmentHelper.setEnchantments(enchant, stack);
        return stack;
    }

    private static void initDimletRecipes() {
        Block redstoneTorch = Blocks.REDSTONE_TORCH;
        addRecipe(DimletType.DIMLET_EFFECT, DimletObjectMapping.NONE_ID, " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.APPLE, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_FEATURE, DimletObjectMapping.NONE_ID, " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.STRING, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_STRUCTURE, DimletObjectMapping.NONE_ID, " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.BONE, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_TERRAIN, "Void", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.BRICK, 'p', Items.PAPER);
        if (!GeneralConfiguration.voidOnly) {
            addRecipe(DimletType.DIMLET_TERRAIN, "Flat", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.BRICK, 'p', ModItems.dimletTemplateItem);
        }
        addRecipe(DimletType.DIMLET_CONTROLLER, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.COMPARATOR, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_CONTROLLER, "Single", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.COMPARATOR, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_MATERIAL, Blocks.STONE.getRegistryName() + "@0", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Blocks.DIRT, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_LIQUID, Blocks.WATER.getRegistryName() + "@0", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.BUCKET, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_SKY, "Normal", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.FEATHER, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_SKY, "Normal Day", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.GLOWSTONE_DUST, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_SKY, "Normal Night", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.COAL, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_MOB, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.ROTTEN_FLESH, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_TIME, "Normal", " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.CLOCK, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_WEATHER, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.REDSTONE, 'w', Items.SNOWBALL, 'p', Items.PAPER);
        addRecipe(DimletType.DIMLET_DIGIT, "0", " r ", "rtr", "ppp", 'r', Items.REDSTONE, 't', redstoneTorch, 'p', Items.PAPER);

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
//        MyGameReg.addRecipe(new NBTMatchingRecipe(1, 1,
//                                                     new ItemStack[] { KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, source) },
//                                                     new String[][] { new String[] { "dkey" } },
//                                                     KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, dest)));
    }

    private static void addRecipe(DimletType type, String id, Object... params) {
//        MyGameReg.addRecipe(KnownDimletConfiguration.getDimletStack(type, id), params);
    }
}
