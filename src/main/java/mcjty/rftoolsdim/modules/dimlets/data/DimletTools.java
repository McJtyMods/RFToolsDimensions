package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

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

    private static DimletItem getEmptyDimletItem(DimletType type) {
        switch (type) {
            case TERRAIN:
                return DimletModule.EMPTY_TERRAIN_DIMLET.get();
            case BIOME_CONTROLLER:
                return DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get();
            case BIOME:
                return DimletModule.EMPTY_BIOME_DIMLET.get();
            case FEATURE:
                return DimletModule.EMPTY_FEATURE_DIMLET.get();
            case BLOCK:
                return DimletModule.EMPTY_BLOCK_DIMLET.get();
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

    @Nonnull
    public static ItemStack getEmptyDimletStack(DimletType type) {
        DimletItem item = getEmptyDimletItem(type);
        return new ItemStack(item);
    }

    public static ItemStack getNeededMemoryPart(DimletKey key) {
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        DimletRarity rarity = settings.getRarity();
        switch (rarity) {
            case COMMON:
                return new ItemStack(DimletModule.PART_MEMORY_0.get());
            case UNCOMMON:
                return new ItemStack(DimletModule.PART_MEMORY_1.get());
            case RARE:
                return new ItemStack(DimletModule.PART_MEMORY_2.get());
            case LEGENDARY:
                return new ItemStack(DimletModule.PART_MEMORY_3.get());
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getNeededEnergyPart(DimletKey key) {
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        DimletRarity rarity = settings.getRarity();
        switch (rarity) {
            case COMMON:
                return new ItemStack(DimletModule.PART_ENERGY_0.get());
            case UNCOMMON:
                return new ItemStack(DimletModule.PART_ENERGY_1.get());
            case RARE:
                return new ItemStack(DimletModule.PART_ENERGY_2.get());
            case LEGENDARY:
                return new ItemStack(DimletModule.PART_ENERGY_3.get());
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getNeededEssence(DimletKey key) {
        switch (key.getType()) {
            case TERRAIN:
                return ItemStack.EMPTY;
            case BIOME_CONTROLLER:
                return ItemStack.EMPTY;
            case BIOME:
                return new ItemStack(EssencesModule.BIOME_ABSORBER_ITEM.get());
            case FEATURE:
                return ItemStack.EMPTY;
            case BLOCK:
                return new ItemStack(EssencesModule.BLOCK_ABSORBER_ITEM.get());
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

    public static boolean isFullEssence(ItemStack stack, ItemStack desired, String desiredKey) {
        if (stack.isItemEqual(desired)) {
            if (stack.getItem() == EssencesModule.BIOME_ABSORBER_ITEM.get()) {
                String biome = BiomeAbsorberTileEntity.getBiome(stack);
                if (Objects.equals(desiredKey, biome)) {
                    return BiomeAbsorberTileEntity.getProgress(stack) >= 100;
                }
            } else if (stack.getItem() == EssencesModule.BLOCK_ABSORBER_ITEM.get()) {
                String block = BlockAbsorberTileEntity.getBlock(stack);
                if (Objects.equals(desiredKey, block)) {
                    return BlockAbsorberTileEntity.getProgress(stack) >= 100;
                }
            }
        }
        return false;
    }
}
