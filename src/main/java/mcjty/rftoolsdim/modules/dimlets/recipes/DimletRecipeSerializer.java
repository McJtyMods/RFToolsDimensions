package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.google.gson.JsonObject;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class DimletRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DimletRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Nonnull
    @Override
    public DimletRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        ShapedRecipe recipe = serializer.fromJson(recipeId, json);
        String typeString = json.getAsJsonPrimitive("dimlettype").getAsString();
        DimletType type = DimletType.byName(typeString);
        String key = json.getAsJsonPrimitive("dimletkey").getAsString();
        return new DimletRecipe(recipe, new DimletKey(type, key));
    }

    @Override
    public DimletRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        ShapedRecipe recipe = serializer.fromNetwork(recipeId, buffer);
        String typeString = buffer.readUtf(32767);
        DimletType type = DimletType.byName(typeString);
        String key = buffer.readUtf(32767);
        return new DimletRecipe(recipe, new DimletKey(type, key));
    }

    @Override
    public void toNetwork(@Nonnull PacketBuffer buffer, DimletRecipe recipe) {
        serializer.toNetwork(buffer, recipe.getRecipe());
        buffer.writeUtf(recipe.getKey().getType().name());
        buffer.writeUtf(recipe.getKey().getKey());
    }
}
