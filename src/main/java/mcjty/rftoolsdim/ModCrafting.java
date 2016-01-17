package mcjty.rftoolsdim;

import mcjty.rftoolsdim.dimensions.DimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
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
        addRecipe(DimletType.DIMLET_EFFECT, "None", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.apple, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_FEATURE, "None", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.string, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_STRUCTURE, "None", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.bone, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_TERRAIN, "Void", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.brick, 'p', Items.paper);
        if (!DimletConfiguration.voidOnly) {
            addRecipe(DimletType.DIMLET_TERRAIN, "Flat", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.brick, 'p', ModItems.dimletTemplateItem);
        }
        addRecipe(DimletType.DIMLET_CONTROLLER, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.comparator, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_CONTROLLER, "Single", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.comparator, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_MATERIAL, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Blocks.dirt, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_LIQUID, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.bucket, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_SKY, "Normal", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.feather, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_SKY, "Normal Day", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.glowstone_dust, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_SKY, "Normal Night", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.coal, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_MOB, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.rotten_flesh, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_TIME, "Normal", " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.clock, 'p', ModItems.dimletTemplateItem);
        addRecipe(DimletType.DIMLET_WEATHER, DimletObjectMapping.DEFAULT_ID, " r ", "rwr", "ppp", 'r', Items.redstone, 'w', Items.snowball, 'p', Items.paper);
        addRecipe(DimletType.DIMLET_DIGIT, "0", " r ", "rtr", "ppp", 'r', Items.redstone, 't', redstoneTorch, 'p', Items.paper);
    }

    private static void addRecipe(DimletType type, String id, Object... params) {
        GameRegistry.addRecipe(KnownDimletConfiguration.getDimletStack(type, id), params);
        KnownDimletConfiguration.registerCraftableDimlet(new DimletKey(type, id));
    }
}
