package mcjty.rftoolsdim;

import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

public class ModCrafting {
    static {
        RecipeSorter.register("rftoolsdim:nbtmatchingrecipe", NBTMatchingRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
    }

    public static void init() {
        initDimletRecipes();
        initMachineRecipes();
        initCosmeticRecipes();
        initAbsorberRecipes();

        Item dimensionalShard = GameRegistry.findItem("rftools", "dimensional_shard");
        GameRegistry.addRecipe(new ItemStack(ModItems.emptyDimensionTabItem), "prp", "rpr", "prp", 'p', Items.PAPER, 'r', Items.REDSTONE);
        GameRegistry.addRecipe(new ItemStack(ModItems.dimletTemplateItem), "sss", "sps", "sss", 's', dimensionalShard, 'p', Items.PAPER);

        GameRegistry.addRecipe(new ItemStack(ModItems.rfToolsDimensionManualItem), "r r", " b ", "r r", 'r', Items.REDSTONE, 'b', Items.BOOK);
        GameRegistry.addRecipe(new ItemStack(ModItems.dimensionMonitorItem), " u ", "rCr", " r ", 'u', dimensionalShard, 'r', Items.REDSTONE, 'C', Items.COMPARATOR);
        GameRegistry.addRecipe(new ItemStack(ModItems.phasedFieldGeneratorItem), "rsr", "sEs", "rsr", 'E', Items.ENDER_EYE, 'r', Items.REDSTONE, 's', dimensionalShard);

        ItemStack inkSac = new ItemStack(Items.DYE, 1, 0);
        GameRegistry.addRecipe(new ItemStack(ModItems.dimensionModuleItem), " c ", "rir", " b ", 'c', Items.ENDER_PEARL, 'r', Items.REDSTONE, 'i', Items.IRON_INGOT,
                               'b', inkSac);
    }

    private static void initMachineRecipes() {
        Block machineFrame = GameRegistry.findBlock("rftools", "machine_frame");
        ItemStack inkSac = new ItemStack(Items.DYE, 1, 0);

        Item dimensionalShard = GameRegistry.findItem("rftools", "dimensional_shard");
        GameRegistry.addRecipe(new ItemStack(ModBlocks.activityProbeBlock), "sss", "oMo", "sss", 'o', Items.ENDER_PEARL, 's', dimensionalShard,
                               'M', machineFrame);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionEnscriberBlock), "rpr", "bMb", "iii", 'r', Items.REDSTONE, 'p', Items.PAPER, 'b', inkSac,
                'M', machineFrame, 'i', Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimletWorkbenchBlock), "gug", "cMc", "grg", 'M', machineFrame, 'u', ModItems.dimletBaseItem, 'c', Blocks.CRAFTING_TABLE,
                               'r', Items.REDSTONE, 'g', Items.GOLD_NUGGET);
        if (GeneralConfiguration.enableDimensionBuilderRecipe) {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionBuilderBlock), "oEo", "DMD", "ggg", 'o', Items.ENDER_PEARL, 'E', Items.EMERALD, 'D', Items.DIAMOND,
                                   'M', machineFrame, 'g', Items.GOLD_INGOT);
        }
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionEditorBlock), "oEo", "DMD", "ggg", 'o', Items.REDSTONE, 'E', Items.EMERALD, 'D', Items.DIAMOND,
                'M', machineFrame, 'g', Items.GOLD_INGOT);

//        GameRegistry.addRecipe(new ItemStack(ModBlocks.essencePainterBlock), "ppp", "iMi", "ppp", 'p', Items.PAPER, 'i', inkSac, 'M', machineFrame);
    }

    private static void initAbsorberRecipes() {
        Block machineFrame = GameRegistry.findBlock("rftools", "machine_frame");
        GameRegistry.addRecipe(new ItemStack(ModBlocks.featureAbsorberBlock), "dws", "wMw", "swd", 'M', machineFrame, 'd', Items.DIAMOND, 's', Items.EMERALD, 'w', Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.featureAbsorberBlock), new ItemStack(ModBlocks.featureAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.terrainAbsorberBlock), "dws", "wMw", "swd", 'M', machineFrame, 'd', Blocks.STONE, 's', Blocks.DIRT, 'w', Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.terrainAbsorberBlock), new ItemStack(ModBlocks.terrainAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.biomeAbsorberBlock), "dws", "wMw", "swd", 'M', machineFrame, 'd', Blocks.DIRT, 's', Blocks.SAPLING, 'w', Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.biomeAbsorberBlock), new ItemStack(ModBlocks.biomeAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.materialAbsorberBlock), "dwc", "wMw", "swg", 'M', machineFrame, 'd', Blocks.DIRT, 'c', Blocks.COBBLESTONE, 's', Blocks.SAND,
                               'g', Blocks.GRAVEL, 'w', Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.materialAbsorberBlock), new ItemStack(ModBlocks.materialAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.liquidAbsorberBlock), "bwb", "wMw", "bwb", 'M', machineFrame, 'b', Items.BUCKET, 'w', Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.liquidAbsorberBlock), new ItemStack(ModBlocks.liquidAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.timeAbsorberBlock), "cwc", "wMw", "cwc", 'M', machineFrame, 'c', Items.CLOCK, 'w', Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.timeAbsorberBlock), new ItemStack(ModBlocks.timeAbsorberBlock));
    }

    private static void initCosmeticRecipes() {
        Item dimensionalShard = GameRegistry.findItem("rftools", "dimensional_shard");
        ItemStack inkSac = new ItemStack(Items.DYE, 1, 0);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalBlankBlock, 8), "bbb", "b*b", "bbb", 'b', Blocks.STONE, '*', dimensionalShard);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.dimensionalBlock), new ItemStack(ModBlocks.dimensionalBlankBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalSmallBlocks, 4), "bb ", "bb ", "   ", 'b', ModBlocks.dimensionalBlankBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalCrossBlock, 5), " b ", "bbb", " b ", 'b', ModBlocks.dimensionalBlankBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalCross2Block, 5), "b b", " b ", "b b", 'b', ModBlocks.dimensionalBlankBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalPattern1Block, 7), "bxb", "bbb", "bxb", 'b', ModBlocks.dimensionalBlankBlock, 'x', inkSac);
        ItemStack bonemealStack = new ItemStack(Items.DYE, 1, 15);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalPattern2Block, 7), "bxb", "bbb", "bxb", 'b', ModBlocks.dimensionalBlankBlock, 'x', bonemealStack);
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
        GameRegistry.addRecipe(new NBTMatchingRecipe(1, 1,
                                                     new ItemStack[] { KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, source) },
                                                     new String[][] { new String[] { "dkey" } },
                                                     KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, dest)));
    }

    private static void addRecipe(DimletType type, String id, Object... params) {
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(type, id), params);
    }
}
