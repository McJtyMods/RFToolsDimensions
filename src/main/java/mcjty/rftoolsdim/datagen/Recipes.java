package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousModule.MACHINE_FRAME.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
        add('E', DimletModule.EMPTY_DIMLET.get());
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.EMPTY_DIMLET.get())
                        .addCriterion("shard", hasItem(VariousModule.DIMENSIONALSHARD.get())),
                " p ", "psp", " p ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.EMPTY_TERRAIN_DIMLET.get())
                        .key('C', Tags.Items.COBBLESTONE)
                        .addCriterion("empty_dimlet", hasItem(DimletModule.EMPTY_DIMLET.get())),
                "CDC", "DED", "CDC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.EMPTY_FEATURE_DIMLET.get())
                        .addCriterion("empty_dimlet", hasItem(DimletModule.EMPTY_DIMLET.get())),
                "rcr", "cEc", "rcr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get())
                        .addCriterion("empty_dimlet", hasItem(DimletModule.EMPTY_DIMLET.get())),
                "DDD", "DED", "DOD");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.EMPTY_BLOCK_DIMLET.get())
                        .key('C', Items.CLAY_BALL)
                        .addCriterion("empty_dimlet", hasItem(DimletModule.EMPTY_DIMLET.get())),
                "CCC", "CEC", "CCC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.EMPTY_BIOME_DIMLET.get())
                        .key('C', Items.CLAY_BALL)
                        .addCriterion("empty_dimlet", hasItem(DimletModule.EMPTY_DIMLET.get())),
                "rCr", "CEC", "rCr");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_ENERGY_0.get())
                        .key('g', Tags.Items.DUSTS_GLOWSTONE)
                        .addCriterion("shard", hasItem(VariousModule.DIMENSIONALSHARD.get())),
                "rRr", "RsR", "rgr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_MEMORY_0.get())
                        .key('g', Tags.Items.DUSTS_GLOWSTONE)
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .addCriterion("shard", hasItem(VariousModule.DIMENSIONALSHARD.get())),
                "rlr", "lsl", "rgr");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_ENERGY_1.get())
                        .key('u', DimletModule.COMMON_ESSENCE.get())
                        .key('M', DimletModule.PART_ENERGY_0.get())
                        .addCriterion("energy0", hasItem(DimletModule.PART_ENERGY_0.get())),
                "uRu", "RMR", "usu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_MEMORY_1.get())
                        .key('u', DimletModule.COMMON_ESSENCE.get())
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .key('M', DimletModule.PART_MEMORY_0.get())
                        .addCriterion("memory0", hasItem(DimletModule.PART_MEMORY_0.get())),
                "ulu", "lMl", "usu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_ENERGY_2.get())
                        .key('u', DimletModule.RARE_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_ENDERPEARL.get())
                        .key('M', DimletModule.PART_ENERGY_1.get())
                        .addCriterion("energy1", hasItem(DimletModule.PART_ENERGY_1.get())),
                "uRu", "RMR", "uUu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_MEMORY_2.get())
                        .key('u', DimletModule.RARE_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_ENDERPEARL.get())
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .key('M', DimletModule.PART_MEMORY_1.get())
                        .addCriterion("memory1", hasItem(DimletModule.PART_MEMORY_1.get())),
                "ulu", "lMl", "uUu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_ENERGY_3.get())
                        .key('u', DimletModule.LEGENDARY_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_DIAMOND.get())
                        .key('M', DimletModule.PART_ENERGY_2.get())
                        .addCriterion("energy2", hasItem(DimletModule.PART_ENERGY_2.get())),
                "uRu", "RMR", "uUu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletModule.PART_MEMORY_3.get())
                        .key('u', DimletModule.LEGENDARY_ESSENCE.get())
                        .key('U', VariousModule.INFUSED_DIAMOND.get())
                        .key('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .key('M', DimletModule.PART_MEMORY_2.get())
                        .addCriterion("memory2", hasItem(DimletModule.PART_MEMORY_2.get())),
                "ulu", "lMl", "uUu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .key('u', KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())
                        .addCriterion("knowlege", hasItem(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())),
                "uuu", "uuu", "uuu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .key('u', KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .addCriterion("knowlege", hasItem(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())),
                "uuu", "uuu", "uuu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get())
                        .key('u', KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .addCriterion("knowlege", hasItem(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())),
                "uuu", "uuu", "uuu");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimensionBuilderModule.DIMENSION_BUILDER.get())
                        .key('g', Items.GOLD_INGOT)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "oeo", "dFd", "ggg");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WorkbenchModule.WORKBENCH.get())
                        .key('C', Blocks.CRAFTING_TABLE)
                        .key('u', DimletModule.EMPTY_DIMLET.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "rur", "CFC", "rur");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WorkbenchModule.HOLDER.get())
                        .key('C', Blocks.CHEST)
                        .key('u', DimletModule.EMPTY_DIMLET.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "sus", "CFC", "sus");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(EnscriberModule.ENSCRIBER.get())
                        .key('C', Blocks.CRAFTING_TABLE)
                        .key('u', DimletModule.EMPTY_DIMLET.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "pup", "CFC", "pup");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(EssencesModule.BLOCK_ABSORBER.get())
                        .key('C', Blocks.SPONGE)
                        .key('u', Blocks.SLIME_BLOCK)
                        .addCriterion("sponge", hasItem(Blocks.SPONGE)),
                "usu", "sCs", "usu");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(EssencesModule.BIOME_ABSORBER.get())
                        .key('C', Blocks.SPONGE)
                        .key('u', ItemTags.LEAVES)
                        .addCriterion("sponge", hasItem(Blocks.SPONGE)),
                "usu", "sCs", "usu");
    }
}
