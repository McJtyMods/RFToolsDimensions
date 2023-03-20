package mcjty.rftoolsdim.modules.dimlets.recipes;

import mcjty.lib.crafting.AbstractRecipeAdaptor;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class DigitCycleRecipe extends AbstractRecipeAdaptor {

    private final String input;
    private final String output;

    public DigitCycleRecipe(ShapedRecipe recipe, String input, String output) {
        super(recipe);
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level worldIn) {
        boolean matches = super.matches(inv, worldIn);
        if (matches) {
            for (int i = 0 ; i < inv.getContainerSize() ; i++) {
                if (!inv.getItem(i).isEmpty()) {
                    DimletKey key = DimletTools.getDimletKey(inv.getItem(i));
                    if (key != null) {
                        return key.key().equals(input);
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, output));
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv, RegistryAccess access) {
        return DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, output));
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return DimletModule.DIMLET_CYCLE_SERIALIZER.get();
    }
}
