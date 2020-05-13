package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousSetup.MACHINE_FRAME.get());
        add('s', VariousSetup.DIMENSIONALSHARD.get());
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ProcessorSetup.ADVANCED_NETWORK_CARD.get())
//                        .key('M', ProcessorSetup.NETWORK_CARD.get())
//                        .addCriterion("network_card", hasItem(ProcessorSetup.NETWORK_CARD.get())),
//                "ror", "eMe", "ror");
    }
}
