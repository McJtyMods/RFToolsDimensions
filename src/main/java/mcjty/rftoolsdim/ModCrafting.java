package mcjty.rftoolsdim;

import mcjty.rftoolsdim.dimensions.DimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {

    public static void init() {
        Block redstoneTorch = Blocks.redstone_torch;
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_EFFECT, "None"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.apple, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_FEATURE, "None"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.string, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_STRUCTURE, "None"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.bone, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_TERRAIN, "Void"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.brick, 'p', Items.paper);
        if (!DimletConfiguration.voidOnly) {
            GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_TERRAIN, "Flat"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.brick, 'p', ModItems.dimletTemplateItem);
        }
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_CONTROLLER, "Default"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.comparator, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_CONTROLLER, "Single"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.comparator, 'p', ModItems.dimletTemplateItem);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_MATERIAL, "None"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Blocks.dirt, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_LIQUID, "None"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.bucket, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_SKY, "Normal"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.feather, 'p', ModItems.dimletTemplateItem);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_SKY, "Normal Day"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.glowstone_dust, 'p', ModItems.dimletTemplateItem);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_SKY, "Normal Night"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.coal, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_MOB, "Default"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.rotten_flesh, 'p', ModItems.dimletTemplateItem);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_TIME, "Normal"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.clock, 'p', ModItems.dimletTemplateItem);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_WEATHER, "Default"), " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.snowball, 'p', Items.paper);
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(DimletType.DIMLET_DIGIT, "0"), " r ", "rtr", "ppp", 'r', Items.redstone, 't', redstoneTorch, 'p', Items.paper);
    }
}
