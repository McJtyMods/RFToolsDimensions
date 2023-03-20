package mcjty.rftoolsdim.modules.dimlets.recipes;

import mcjty.lib.crafting.AbstractRecipeAdaptor;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class DimletRecipe extends AbstractRecipeAdaptor {

    private final DimletKey key;

    public DimletRecipe(ShapedRecipe recipe, DimletKey key) {
        super(recipe);
        this.key = key;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    public DimletKey getKey() {
        return key;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return DimletTools.getDimletStack(key);
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv, RegistryAccess access) {
        return DimletTools.getDimletStack(key);
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return DimletModule.DIMLET_RECIPE_SERIALIZER.get();
    }
}
