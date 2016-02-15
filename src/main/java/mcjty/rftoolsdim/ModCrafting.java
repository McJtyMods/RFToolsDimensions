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

public class ModCrafting {

    public static void init() {
        initDimletRecipes();
        initMachineRecipes();
        initCosmeticRecipes();
        initAbsorberRecipes();

        Item dimensionalShard = GameRegistry.findItem("rftools", "dimensional_shard");
        GameRegistry.addRecipe(new ItemStack(ModItems.emptyDimensionTabItem), "prp", "rpr", "prp", 'p', Items.paper, 'r', Items.redstone);
        GameRegistry.addRecipe(new ItemStack(ModItems.dimletTemplateItem), "sss", "sps", "sss", 's', dimensionalShard, 'p', Items.paper);

        GameRegistry.addRecipe(new ItemStack(ModItems.rfToolsDimensionManualItem), "r r", " b ", "r r", 'r', Items.redstone, 'b', Items.book);
        GameRegistry.addRecipe(new ItemStack(ModItems.dimensionMonitorItem), " u ", "rCr", " r ", 'u', dimensionalShard, 'r', Items.redstone, 'C', Items.comparator);
    }

    private static void initMachineRecipes() {
        Block machineFrame = GameRegistry.findBlock("rftools", "machine_frame");
        ItemStack inkSac = new ItemStack(Items.dye, 1, 0);

        Item dimensionalShard = GameRegistry.findItem("rftools", "dimensional_shard");
        GameRegistry.addRecipe(new ItemStack(ModBlocks.activityProbeBlock), "sss", "oMo", "sss", 'o', Items.ender_pearl, 's', dimensionalShard,
                               'M', machineFrame);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionEnscriberBlock), "rpr", "bMb", "iii", 'r', Items.redstone, 'p', Items.paper, 'b', inkSac,
                'M', machineFrame, 'i', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimletWorkbenchBlock), "gug", "cMc", "grg", 'M', machineFrame, 'u', ModItems.dimletBaseItem, 'c', Blocks.crafting_table,
                               'r', Items.redstone, 'g', Items.gold_nugget);
        if (GeneralConfiguration.enableDimensionBuilderRecipe) {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionBuilderBlock), "oEo", "DMD", "ggg", 'o', Items.ender_pearl, 'E', Items.emerald, 'D', Items.diamond,
                                   'M', machineFrame, 'g', Items.gold_ingot);
        }
    }

    private static void initAbsorberRecipes() {
        Block machineFrame = GameRegistry.findBlock("rftools", "machine_frame");
        GameRegistry.addRecipe(new ItemStack(ModBlocks.featureAbsorberBlock), "dws", "wMw", "swd", 'M', machineFrame, 'd', Items.diamond, 's', Items.emerald, 'w', Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.featureAbsorberBlock), new ItemStack(ModBlocks.featureAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.terrainAbsorberBlock), "dws", "wMw", "swd", 'M', machineFrame, 'd', Blocks.stone, 's', Blocks.dirt, 'w', Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.terrainAbsorberBlock), new ItemStack(ModBlocks.terrainAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.biomeAbsorberBlock), "dws", "wMw", "swd", 'M', machineFrame, 'd', Blocks.dirt, 's', Blocks.sapling, 'w', Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.biomeAbsorberBlock), new ItemStack(ModBlocks.biomeAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.materialAbsorberBlock), "dwc", "wMw", "swg", 'M', machineFrame, 'd', Blocks.dirt, 'c', Blocks.cobblestone, 's', Blocks.sand,
                               'g', Blocks.gravel, 'w', Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.materialAbsorberBlock), new ItemStack(ModBlocks.materialAbsorberBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.liquidAbsorberBlock), "bwb", "wMw", "bwb", 'M', machineFrame, 'b', Items.bucket, 'w', Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.liquidAbsorberBlock), new ItemStack(ModBlocks.liquidAbsorberBlock));
    }

    private static void initCosmeticRecipes() {
        Item dimensionalShard = GameRegistry.findItem("rftools", "dimensional_shard");
        ItemStack inkSac = new ItemStack(Items.dye, 1, 0);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalBlankBlock, 8), "bbb", "b*b", "bbb", 'b', Blocks.stone, '*', dimensionalShard);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.dimensionalBlock), new ItemStack(ModBlocks.dimensionalBlankBlock));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalSmallBlocks, 4), "bb ", "bb ", "   ", 'b', ModBlocks.dimensionalBlankBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalCrossBlock, 5), " b ", "bbb", " b ", 'b', ModBlocks.dimensionalBlankBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalCross2Block, 5), "b b", " b ", "b b", 'b', ModBlocks.dimensionalBlankBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalPattern1Block, 7), "bxb", "bbb", "bxb", 'b', ModBlocks.dimensionalBlankBlock, 'x', inkSac);
        ItemStack bonemealStack = new ItemStack(Items.dye, 1, 15);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.dimensionalPattern2Block, 7), "bxb", "bbb", "bxb", 'b', ModBlocks.dimensionalBlankBlock, 'x', bonemealStack);
    }

    private static void initDimletRecipes() {
        Block redstoneTorch = Blocks.redstone_torch;
        addRecipe(DimletType.DIMLET_EFFECT, DimletObjectMapping.NONE_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.apple, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_FEATURE, DimletObjectMapping.NONE_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.string, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_STRUCTURE, DimletObjectMapping.NONE_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.bone, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_TERRAIN, "Void", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.brick, 'p', Items.paper);
        if (!GeneralConfiguration.voidOnly) {
            addRecipe(DimletType.DIMLET_TERRAIN, "Flat", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.brick, 'p', ModItems.dimletTemplateItem);
        }
        addRecipe(DimletType.DIMLET_CONTROLLER, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.comparator, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_CONTROLLER, "Single", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.comparator, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_MATERIAL, Blocks.stone.getRegistryName() + "@0", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Blocks.dirt, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_LIQUID, Blocks.water.getRegistryName() + "@0", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.bucket, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_SKY, "Normal", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.feather, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_SKY, "Normal Day", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.glowstone_dust, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_SKY, "Normal Night", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.coal, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_MOB, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.rotten_flesh, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_TIME, "Normal", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.clock, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_WEATHER, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.snowball, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_DIGIT, "0", " r ", "rtr", "ppp", 'r', Items.redstone, 't', redstoneTorch, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_DIGIT, "1", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "0"));
        addRecipe(DimletType.DIMLET_DIGIT, "2", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "1"));
        addRecipe(DimletType.DIMLET_DIGIT, "3", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "2"));
        addRecipe(DimletType.DIMLET_DIGIT, "4", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "3"));
        addRecipe(DimletType.DIMLET_DIGIT, "5", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "4"));
        addRecipe(DimletType.DIMLET_DIGIT, "6", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "5"));
        addRecipe(DimletType.DIMLET_DIGIT, "7", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "6"));
        addRecipe(DimletType.DIMLET_DIGIT, "8", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "7"));
        addRecipe(DimletType.DIMLET_DIGIT, "9", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "8"));
        addRecipe(DimletType.DIMLET_DIGIT, "0", "d", 'd', KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "9"));
    }

    private static void addRecipe(DimletType type, String id, Object... params) {
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(type, id), params);
    }
}
