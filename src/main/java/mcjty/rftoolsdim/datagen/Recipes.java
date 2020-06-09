package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousSetup;
import mcjty.rftoolsdim.modules.dimlets.DimletSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousSetup.MACHINE_FRAME.get());
        add('s', VariousSetup.DIMENSIONALSHARD.get());
        add('E', DimletSetup.EMPTY_DIMLET.get());
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.EMPTY_DIMLET.get())
                        .addCriterion("shard", hasItem(VariousSetup.DIMENSIONALSHARD.get())),
                " p ", "psp", " p ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.TERRAIN_DIMLET.get())
                        .key('C', Tags.Items.COBBLESTONE)
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "CDC", "DED", "CDC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.FEATURE_DIMLET.get())
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "rcr", "cEc", "rcr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimletSetup.BIOME_MODIFIER_DIMLET.get())
                        .addCriterion("empty_dimlet", hasItem(DimletSetup.EMPTY_DIMLET.get())),
                "DDD", "DED", "DOD");
    }
}
