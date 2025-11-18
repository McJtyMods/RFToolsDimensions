package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class DimletCycleRecipeSerializer implements RecipeSerializer<DigitCycleRecipe> {

    public static final MapCodec<DigitCycleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipe.Serializer.CODEC.forGetter(DigitCycleRecipe::getRecipe),
            Codec.STRING.fieldOf("input").forGetter(DigitCycleRecipe::getInput),
            Codec.STRING.fieldOf("output").forGetter(DigitCycleRecipe::getOutput)
    ).apply(instance, DigitCycleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DigitCycleRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {
                ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe.getRecipe());
                buffer.writeUtf(recipe.getInput());
                buffer.writeUtf(recipe.getOutput());
            },
            buffer -> {
                ShapedRecipe shaped = ShapedRecipe.Serializer.STREAM_CODEC.decode(buffer);
                String input = buffer.readUtf(32767);
                String output = buffer.readUtf(32767);
                return new DigitCycleRecipe(shaped, input, output);
            }
    );

    @Override
    public MapCodec<DigitCycleRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DigitCycleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
