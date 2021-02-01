package mcjty.rftoolsdim.modules.dimlets.recipes;

import mcjty.lib.crafting.AbstractRecipeAdaptor;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.world.World;

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
    public boolean matches(CraftingInventory inv, World worldIn) {
        boolean matches = super.matches(inv, worldIn);
        if (matches) {
            for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
                if (!inv.getStackInSlot(i).isEmpty()) {
                    DimletKey key = DimletTools.getDimletKey(inv.getStackInSlot(i));
                    if (key != null) {
                        return key.getKey().equals(input);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, output));
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return DimletTools.getDimletStack(new DimletKey(DimletType.DIGIT, output));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return DimletModule.DIMLET_CYCLE_SERIALIZER.get();
    }
}
