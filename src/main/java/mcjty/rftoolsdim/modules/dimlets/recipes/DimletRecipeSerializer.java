package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.google.gson.JsonObject;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class DimletRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DimletRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    public DimletRecipe read(ResourceLocation recipeId, JsonObject json) {
        ShapedRecipe recipe = serializer.read(recipeId, json);
        String typeString = json.getAsJsonPrimitive("dimlettype").getAsString();
        DimletType type = DimletType.byName(typeString);
        String key = json.getAsJsonPrimitive("dimletkey").getAsString();
        return new DimletRecipe(recipe, new DimletKey(type, key));
    }

    @Override
    public DimletRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe recipe = serializer.read(recipeId, buffer);
        String typeString = buffer.readString(32767);
        DimletType type = DimletType.byName(typeString);
        String key = buffer.readString(32767);
        return new DimletRecipe(recipe, new DimletKey(type, key));
    }

    @Override
    public void write(PacketBuffer buffer, DimletRecipe recipe) {
        serializer.write(buffer, recipe.getRecipe());
        buffer.writeString(recipe.getKey().getType().name());
        buffer.writeString(recipe.getKey().getKey());
    }
}
