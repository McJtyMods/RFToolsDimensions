package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class DimletRecipeSerializer implements RecipeSerializer<DimletRecipe> {

    private static final Codec<DimletType> DIMLET_TYPE_CODEC = Codec.STRING.xmap(DimletType::byName, DimletType::name);

    public static final MapCodec<DimletRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipe.Serializer.CODEC.forGetter(DimletRecipe::getRecipe),
            DIMLET_TYPE_CODEC.fieldOf("dimlettype").forGetter(recipe -> recipe.getKey().type()),
            Codec.STRING.fieldOf("dimletkey").forGetter(recipe -> recipe.getKey().key())
    ).apply(instance, (shaped, type, key) -> new DimletRecipe(shaped, new DimletKey(type, key))));

    public static final StreamCodec<RegistryFriendlyByteBuf, DimletRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {
                ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe.getRecipe());
                buffer.writeUtf(recipe.getKey().type().name());
                buffer.writeUtf(recipe.getKey().key());
            },
            buffer -> {
                ShapedRecipe shaped = ShapedRecipe.Serializer.STREAM_CODEC.decode(buffer);
                DimletType type = DimletType.byName(buffer.readUtf(32767));
                String key = buffer.readUtf(32767);
                return new DimletRecipe(shaped, new DimletKey(type, key));
            }
    );

    @Override
    public MapCodec<DimletRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DimletRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
