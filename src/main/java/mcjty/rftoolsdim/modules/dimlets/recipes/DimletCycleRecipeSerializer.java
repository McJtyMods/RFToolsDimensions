package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class DimletCycleRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DigitCycleRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    public DigitCycleRecipe read(ResourceLocation recipeId, JsonObject json) {
        ShapedRecipe recipe = serializer.read(recipeId, json);
        String inputString = json.getAsJsonPrimitive("input").getAsString();
        String outputString = json.getAsJsonPrimitive("output").getAsString();
        return new DigitCycleRecipe(recipe, inputString, outputString);
    }

    @Override
    public DigitCycleRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe recipe = serializer.read(recipeId, buffer);
        String inputString = buffer.readString(32767);
        String outputString = buffer.readString(32767);
        return new DigitCycleRecipe(recipe, inputString, outputString);
    }

    @Override
    public void write(PacketBuffer buffer, DigitCycleRecipe recipe) {
        serializer.write(buffer, recipe.getRecipe());
        buffer.writeString(recipe.getInput());
        buffer.writeString(recipe.getOutput());
    }
}
