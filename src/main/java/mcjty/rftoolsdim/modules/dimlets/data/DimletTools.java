package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DimletTools {

    // Use client side!
    public static String getDimletDescription(ItemStack stack) {
        DimletKey key = getDimletKey(stack);
        if (key == null) {
            return "<Unknown>";
        } else {
            return getReadableName(key);
        }
    }

    private static DimletItem getDimletItem(DimletType type) {
        switch (type) {
            case TERRAIN:
                return DimletModule.TERRAIN_DIMLET.get();
            case BIOME_CONTROLLER:
                return DimletModule.BIOME_CONTROLLER_DIMLET.get();
            case BIOME:
                return DimletModule.BIOME_DIMLET.get();
            case FEATURE:
                return DimletModule.FEATURE_DIMLET.get();
            case BLOCK:
                return DimletModule.BLOCK_DIMLET.get();
        }
        return null;
    }

    @Nullable
    public static DimletKey getDimletKey(ItemStack stack) {
        if (stack.getItem() instanceof DimletItem) {
            DimletType type = ((DimletItem) stack.getItem()).getType();
            if (type != null) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    String name = tag.getString("name");
                    return new DimletKey(type, name);
                }
            }
        }
        return null;
    }

    @Nonnull
    public static ItemStack getDimletStack(DimletKey key) {
        DimletItem item = getDimletItem(key.getType());
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putString("name", key.getKey());
        return stack;
    }

    public static ItemStack getNeededEssence(DimletKey key) {
        switch (key.getType()) {
            case TERRAIN:
                return ItemStack.EMPTY;
            case BIOME_CONTROLLER:
                return ItemStack.EMPTY;
            case BIOME:
                // @todo
                break;
            case FEATURE:
                return ItemStack.EMPTY;
            case BLOCK:
                // @todo
                break;
        }
        return ItemStack.EMPTY;
    }


    public static ITextComponent getReadable(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BIOME_CONTROLLER:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BIOME:
                ResourceLocation id = new ResourceLocation(dimletKey.getKey());
                String trans = "biome." + id.getNamespace() + "." + id.getPath();
                return new TranslationTextComponent(trans);
            case FEATURE:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BLOCK:
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dimletKey.getKey()));
                return new TranslationTextComponent(block.getTranslationKey());
        }
        return new StringTextComponent("<unknown>");
    }

    // Use client side!
    public static String getReadableName(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return dimletKey.getKey().toLowerCase();
            case BIOME_CONTROLLER:
                return dimletKey.getKey().toLowerCase();
            case BIOME:
                ResourceLocation id = new ResourceLocation(dimletKey.getKey());
                String trans = "biome." + id.getNamespace() + "." + id.getPath();
                return I18n.format(trans);
            case FEATURE:
                return dimletKey.getKey().toLowerCase();
            case BLOCK:
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dimletKey.getKey()));
                return I18n.format(block.getTranslationKey());
        }
        return "<unknown>";
    }
}
