package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class DimletCycleRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<DigitCycleRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    @Nonnull
    public DigitCycleRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        ShapedRecipe recipe = serializer.fromJson(recipeId, json);
        String inputString = json.getAsJsonPrimitive("input").getAsString();
        String outputString = json.getAsJsonPrimitive("output").getAsString();
        return new DigitCycleRecipe(recipe, inputString, outputString);
    }

    @Override
    public DigitCycleRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        ShapedRecipe recipe = serializer.fromNetwork(recipeId, buffer);
        String inputString = buffer.readUtf(32767);
        String outputString = buffer.readUtf(32767);
        return new DigitCycleRecipe(recipe, inputString, outputString);
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, DigitCycleRecipe recipe) {
        serializer.toNetwork(buffer, recipe.getRecipe());
        buffer.writeUtf(recipe.getInput());
        buffer.writeUtf(recipe.getOutput());
    }
}
