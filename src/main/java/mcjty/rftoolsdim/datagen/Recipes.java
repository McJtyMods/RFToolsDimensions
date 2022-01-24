package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletCycleRecipeBuilder;
import mcjty.rftoolsdim.modules.dimlets.recipes.DimletRecipeBuilder;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousModule.MACHINE_FRAME.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
        add('E', DimletModule.EMPTY_DIMLET.get());
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_DIMLET.get())
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                " p ", "psp", " p ");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_TERRAIN_DIMLET.get())
                        .define('C', Tags.Items.COBBLESTONE)
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "CDC", "DED", "CDC");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_ATTRIBUTE_DIMLET.get())
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "ppp", "pEp", "ppp");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_FEATURE_DIMLET.get())
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "rcr", "cEc", "rcr");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get())
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "DDD", "DED", "DOD");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_BLOCK_DIMLET.get())
                        .define('C', Items.CLAY_BALL)
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "CCC", "CEC", "CCC");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_FLUID_DIMLET.get())
                        .define('C', Items.CLAY_BALL)
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "CWC", "CEC", "CCC");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_BIOME_DIMLET.get())
                        .define('C', Items.CLAY_BALL)
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "rCr", "CEC", "rCr");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.EMPTY_TIME_DIMLET.get())
                        .define('C', Items.CLOCK)
                        .unlockedBy("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "rCr", "CEC", "rCr");
        build(consumer, DimletRecipeBuilder.shapedRecipe(DimletModule.DIGIT_DIMLET.get())
                        .define('C', Items.REDSTONE_TORCH)
                        .dimletKey(new DimletKey(DimletType.DIGIT, "0"))
                        .addCriterion("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                " C ", "CEC", " C ");
        for (int i = 1 ; i <= 9 ; i++) {
            build(consumer, new ResourceLocation(RFToolsDim.MODID, "digit" + i), DimletCycleRecipeBuilder.shapedRecipe(DimletModule.DIGIT_DIMLET.get())
                            .define('C', Ingredient.of(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "0"))))
                            .input(String.valueOf(i - 1))
                            .output(String.valueOf(i))
                            .addCriterion("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                    "C");
        }
        build(consumer, new ResourceLocation(RFToolsDim.MODID, "digit0"), DimletCycleRecipeBuilder.shapedRecipe(DimletModule.DIGIT_DIMLET.get())
                        .define('C', Ingredient.of(DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, "1"))))
                        .input("9")
                        .output("0")
                        .addCriterion("empty_dimlet", has(DimletModule.EMPTY_DIMLET.get())),
                "C");

        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_ENERGY_0.get())
                        .define('g', Tags.Items.DUSTS_GLOWSTONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "rRr", "RsR", "rgr");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_MEMORY_0.get())
                        .define('g', Tags.Items.DUSTS_GLOWSTONE)
                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "rlr", "lsl", "rgr");

        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_ENERGY_1.get())
                        .define('u', DimletModule.COMMON_ESSENCE.get())
                        .define('M', DimletModule.PART_ENERGY_0.get())
                        .unlockedBy("energy0", has(DimletModule.PART_ENERGY_0.get())),
                "uRu", "RMR", "usu");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_MEMORY_1.get())
                        .define('u', DimletModule.COMMON_ESSENCE.get())
                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .define('M', DimletModule.PART_MEMORY_0.get())
                        .unlockedBy("memory0", has(DimletModule.PART_MEMORY_0.get())),
                "ulu", "lMl", "usu");

        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_ENERGY_2.get())
                        .define('u', DimletModule.RARE_ESSENCE.get())
                        .define('U', VariousModule.INFUSED_ENDERPEARL.get())
                        .define('M', DimletModule.PART_ENERGY_1.get())
                        .unlockedBy("energy1", has(DimletModule.PART_ENERGY_1.get())),
                "uRu", "RMR", "uUu");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_MEMORY_2.get())
                        .define('u', DimletModule.RARE_ESSENCE.get())
                        .define('U', VariousModule.INFUSED_ENDERPEARL.get())
                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .define('M', DimletModule.PART_MEMORY_1.get())
                        .unlockedBy("memory1", has(DimletModule.PART_MEMORY_1.get())),
                "ulu", "lMl", "uUu");

        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_ENERGY_3.get())
                        .define('u', DimletModule.LEGENDARY_ESSENCE.get())
                        .define('U', VariousModule.INFUSED_DIAMOND.get())
                        .define('M', DimletModule.PART_ENERGY_2.get())
                        .unlockedBy("energy2", has(DimletModule.PART_ENERGY_2.get())),
                "uRu", "RMR", "uUu");
        build(consumer, ShapedRecipeBuilder.shaped(DimletModule.PART_MEMORY_3.get())
                        .define('u', DimletModule.LEGENDARY_ESSENCE.get())
                        .define('U', VariousModule.INFUSED_DIAMOND.get())
                        .define('l', Tags.Items.STORAGE_BLOCKS_LAPIS)
                        .define('M', DimletModule.PART_MEMORY_2.get())
                        .unlockedBy("memory2", has(DimletModule.PART_MEMORY_2.get())),
                "ulu", "lMl", "uUu");

        build(consumer, ShapedRecipeBuilder.shaped(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .define('u', KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())
                        .unlockedBy("knowlege", has(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())),
                "uuu", "uuu", "uuu");
        build(consumer, ShapedRecipeBuilder.shaped(KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .define('u', KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .unlockedBy("knowlege", has(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())),
                "uuu", "uuu", "uuu");
        build(consumer, ShapedRecipeBuilder.shaped(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get())
                        .define('u', KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .unlockedBy("knowlege", has(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())),
                "uuu", "uuu", "uuu");

        build(consumer, ShapedRecipeBuilder.shaped(DimensionBuilderModule.DIMENSION_BUILDER.get())
                        .define('g', Items.GOLD_INGOT)
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "oeo", "dFd", "ggg");
        build(consumer, ShapedRecipeBuilder.shaped(DimensionEditorModule.DIMENSION_EDITOR.get())
                        .define('g', Items.GOLD_INGOT)
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "oio", "iFi", "ggg");
        build(consumer, ShapedRecipeBuilder.shaped(WorkbenchModule.WORKBENCH.get())
                        .define('C', Blocks.CRAFTING_TABLE)
                        .define('u', DimletModule.EMPTY_DIMLET.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "rur", "CFC", "rur");
        build(consumer, ShapedRecipeBuilder.shaped(WorkbenchModule.HOLDER.get())
                        .define('C', Blocks.CHEST)
                        .define('u', DimletModule.EMPTY_DIMLET.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "sus", "CFC", "sus");
        build(consumer, ShapedRecipeBuilder.shaped(EnscriberModule.ENSCRIBER.get())
                        .define('C', Blocks.CRAFTING_TABLE)
                        .define('u', DimletModule.EMPTY_DIMLET.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "pup", "CFC", "pup");
        build(consumer, ShapedRecipeBuilder.shaped(WorkbenchModule.RESEARCHER.get())
                        .define('C', Blocks.ENCHANTING_TABLE)
                        .define('X', Blocks.COMPARATOR)
                        .define('u', DimletModule.EMPTY_DIMLET.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "rur", "XFC", "rur");

        build(consumer, ShapedRecipeBuilder.shaped(EssencesModule.BLOCK_ABSORBER.get())
                        .define('C', Blocks.SPONGE)
                        .define('u', Blocks.SLIME_BLOCK)
                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                "usu", "sCs", "usu");
        build(consumer, ShapedRecipeBuilder.shaped(EssencesModule.FLUID_ABSORBER.get())
                        .define('C', Blocks.SPONGE)
                        .define('u', Blocks.SLIME_BLOCK)
                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                "uWu", "sCs", "usu");
        build(consumer, ShapedRecipeBuilder.shaped(EssencesModule.BIOME_ABSORBER.get())
                        .define('C', Blocks.SPONGE)
                        .define('u', ItemTags.LEAVES)
                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                "usu", "sCs", "usu");
        build(consumer, ShapedRecipeBuilder.shaped(EssencesModule.STRUCTURE_ABSORBER.get())
                        .define('C', Blocks.SPONGE)
                        .define('u', ItemTags.STONE_BRICKS)
                        .unlockedBy("sponge", has(Blocks.SPONGE)),
                "usu", "sCs", "usu");

        build(consumer, ShapedRecipeBuilder.shaped(DimensionBuilderModule.EMPTY_DIMENSION_TAB.get())
                        .unlockedBy("redstone", has(Items.REDSTONE)),
                "prp", "rpr", "prp");
        build(consumer, ShapedRecipeBuilder.shaped(DimensionBuilderModule.DIMENSION_MONITOR.get())
                        .define('C', Blocks.COMPARATOR)
                        .unlockedBy("redstone", has(Items.REDSTONE)),
                " s ", "rCr", " s ");
        build(consumer, ShapedRecipeBuilder.shaped(DimensionBuilderModule.PHASED_FIELD_GENERATOR.get())
                        .define('C', Items.ENDER_EYE)
                        .unlockedBy("redstone", has(Items.REDSTONE)),
                "rsr", "sCs", "rsr");

        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_BLANK.get())
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "ss", "ss");
        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_BLOCK.get())
                        .define('S', Tags.Items.STONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "Ss", "ss");
        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_SMALL_BLOCK.get())
                        .define('S', Tags.Items.STONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "ss", "sS");
        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_CROSS_BLOCK.get())
                        .define('S', Tags.Items.STONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "Ss", "sS");
        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_CROSS2_BLOCK.get())
                        .define('S', Tags.Items.STONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "sS", "Ss");
        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_PATTERN1_BLOCK.get())
                        .define('S', Tags.Items.STONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "sS", "sS");
        build(consumer, ShapedRecipeBuilder.shaped(DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK.get())
                        .define('S', Tags.Items.STONE)
                        .unlockedBy("shard", has(VariousModule.DIMENSIONALSHARD.get())),
                "Ss", "Ss");
    }
}
