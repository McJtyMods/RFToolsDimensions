package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderSetup;
import mcjty.rftoolsdim.modules.dimlets.DimletSetup;
import mcjty.rftoolsdim.modules.enscriber.EnscriberSetup;
import mcjty.rftoolsdim.modules.workbench.WorkbenchSetup;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousModule.MACHINE_FRAME.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
        add('E', DimletSetup.EMPTY_DIMLET.get());
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.EMPTY_DIMLET.get())
                        .addCriterion("shard", hasItem(VariousModule.DIMENSIONALSHARD.get())),
                " p ", "psp", " p ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.EMPTY_TERRAIN_DIMLET.get())
                        .key('C', Tags.Items.COBBLESTONE)
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "CDC", "DED", "CDC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.EMPTY_FEATURE_DIMLET.get())
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "rcr", "cEc", "rcr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.EMPTY_BIOME_MODIFIER_DIMLET.get())
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "DDD", "DED", "DOD");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.EMPTY_MATERIAL_DIMLET.get())
                        .key('C', Items.CLAY_BALL)
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "CCC", "CEC", "CCC");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_ENERGY_0.get())
                        .key('g', Tags.Items.DUSTS_GLOWSTONE)
                        .addCriterion("shard", hasItem(VariousModule.DIMENSIONALSHARD.get())),
                "rRr", "RsR", "rgr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_MEMORY_0.get())
                        .key('g', Tags.Items.DUSTS_GLOWSTONE)
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .addCriterion("shard", hasItem(VariousModule.DIMENSIONALSHARD.get())),
                "rlr", "lsl", "rgr");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_ENERGY_1.get())
                        .key('u', DimletSetup.COMMON_ESSENCE.get())
                        .key('M', DimletSetup.PART_ENERGY_0.get())
                        .addCriterion("energy0", hasItem(DimletSetup.PART_ENERGY_0.get())),
                "uRu", "RMR", "usu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_MEMORY_1.get())
                        .key('u', DimletSetup.COMMON_ESSENCE.get())
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .key('M', DimletSetup.PART_MEMORY_0.get())
                        .addCriterion("memory0", hasItem(DimletSetup.PART_MEMORY_0.get())),
                "ulu", "lMl", "usu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_ENERGY_2.get())
                        .key('u', DimletSetup.RARE_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_ENDERPEARL.get())
                        .key('M', DimletSetup.PART_ENERGY_1.get())
                        .addCriterion("energy1", hasItem(DimletSetup.PART_ENERGY_1.get())),
                "uRu", "RMR", "uUu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_MEMORY_2.get())
                        .key('u', DimletSetup.RARE_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_ENDERPEARL.get())
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .key('M', DimletSetup.PART_MEMORY_1.get())
                        .addCriterion("memory1", hasItem(DimletSetup.PART_MEMORY_1.get())),
                "ulu", "lMl", "uUu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_ENERGY_3.get())
                        .key('u', DimletSetup.LEGENDARY_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_DIAMOND.get())
                        .key('M', DimletSetup.PART_ENERGY_2.get())
                        .addCriterion("energy2", hasItem(DimletSetup.PART_ENERGY_2.get())),
                "uRu", "RMR", "uUu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.PART_MEMORY_3.get())
                        .key('u', DimletSetup.LEGENDARY_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_DIAMOND.get())
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .key('M', DimletSetup.PART_MEMORY_2.get())
                        .addCriterion("memory2", hasItem(DimletSetup.PART_MEMORY_2.get())),
                "ulu", "lMl", "uUu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimensionBuilderSetup.DIMENSION_BUILDER.get())
                        .key('g', Items.GOLD_INGOT)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "oeo", "dFd", "ggg");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WorkbenchSetup.WORKBENCH.get())
                        .key('C', Blocks.CRAFTING_TABLE)
                        .key('u', DimletSetup.EMPTY_DIMLET.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "rur", "CFC", "rur");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(EnscriberSetup.ENSCRIBER.get())
                        .key('C', Blocks.CRAFTING_TABLE)
                        .key('u', DimletSetup.EMPTY_DIMLET.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "pup", "CFC", "pup");
    }
}
