package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.lib.varia.Tools;
import mcjty.rftoolsdim.dimension.AdminDimletType;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

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

    // Use client side!
    public static String getDimletRarity(ItemStack stack) {
        DimletKey key = getDimletKey(stack);
        if (key == null) {
            return "<Unknown>";
        } else {
            DimletSettings settings = DimletDictionary.get().getSettings(key);
            if (settings != null) {
                DimletRarity rarity = settings.getRarity();
                return rarity.name();
            } else {
                return "<Unknown>";
            }
        }
    }

    // Use client side!
    public static String getDimletCost(ItemStack stack) {
        DimletKey key = getDimletKey(stack);
        if (key == null) {
            return "<Unknown>";
        } else {
            DimletSettings settings = DimletDictionary.get().getSettings(key);
            if (settings != null) {
                int createCost = settings.getCreateCost();
                int maintainCost = settings.getMaintainCost();
                int tickCost = settings.getTickCost();
                return "C " + createCost + ", M " + maintainCost + ", T " + tickCost;
            } else {
                return "<Unknown>";
            }
        }
    }

    private static DimletItem getDimletItem(DimletType type) {
        switch (type) {
            case TERRAIN:
                return DimletModule.TERRAIN_DIMLET.get();
            case ATTRIBUTE:
                return DimletModule.ATTRIBUTE_DIMLET.get();
            case BIOME_CONTROLLER:
                return DimletModule.BIOME_CONTROLLER_DIMLET.get();
            case BIOME:
                return DimletModule.BIOME_DIMLET.get();
            case FEATURE:
                return DimletModule.FEATURE_DIMLET.get();
            case TIME:
                return DimletModule.TIME_DIMLET.get();
            case BLOCK:
                return DimletModule.BLOCK_DIMLET.get();
            case FLUID:
                return DimletModule.FLUID_DIMLET.get();
            case DIGIT:
                return DimletModule.DIGIT_DIMLET.get();
            case ADMIN:
                return DimletModule.ADMIN_DIMLET.get();
        }
        return null;
    }

    private static DimletItem getEmptyDimletItem(DimletType type) {
        switch (type) {
            case TERRAIN:
                return DimletModule.EMPTY_TERRAIN_DIMLET.get();
            case ATTRIBUTE:
                return DimletModule.EMPTY_ATTRIBUTE_DIMLET.get();
            case BIOME_CONTROLLER:
                return DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get();
            case BIOME:
                return DimletModule.EMPTY_BIOME_DIMLET.get();
            case FEATURE:
                return DimletModule.EMPTY_FEATURE_DIMLET.get();
            case TIME:
                return DimletModule.EMPTY_TIME_DIMLET.get();
            case BLOCK:
                return DimletModule.EMPTY_BLOCK_DIMLET.get();
            case FLUID:
                return DimletModule.EMPTY_FLUID_DIMLET.get();
            case DIGIT:
                return null;
            case ADMIN:
                return null;
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
        if (settings == null) {
            return ItemStack.EMPTY;
        }
        DimletRarity rarity = settings.getRarity();
        if (rarity == null) {
            return ItemStack.EMPTY;
        }
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
        if (settings == null) {
            return ItemStack.EMPTY;
        }
        DimletRarity rarity = settings.getRarity();
        if (rarity == null) {
            return ItemStack.EMPTY;
        }
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

    public static ItemStack getNeededEssence(DimletKey key, @Nonnull DimletSettings settings) {
        if (!settings.getEssence().isEmpty()) {
            return settings.getEssence();
        }
        switch (key.getType()) {
            case TERRAIN:
                return ItemStack.EMPTY;
            case ATTRIBUTE:
                return ItemStack.EMPTY;
            case BIOME_CONTROLLER:
                return ItemStack.EMPTY;
            case BIOME:
                return new ItemStack(EssencesModule.BIOME_ABSORBER_ITEM.get());
            case FEATURE:
                return ItemStack.EMPTY;
            case TIME:
                return ItemStack.EMPTY;
            case BLOCK:
                return new ItemStack(EssencesModule.BLOCK_ABSORBER_ITEM.get());
            case FLUID:
                return new ItemStack(EssencesModule.FLUID_ABSORBER_ITEM.get());
            case DIGIT:
                return ItemStack.EMPTY;
            case ADMIN:
                return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }


    @Nullable
    public static ResourceLocation getResourceLocation(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return null;
            case ATTRIBUTE:
                return null;
            case BIOME_CONTROLLER:
                return null;
            case BIOME:
                return new ResourceLocation(dimletKey.getKey());
            case FEATURE:
                return null;
            case TIME:
                return null;
            case BLOCK:
                return new ResourceLocation(dimletKey.getKey());
            case FLUID:
                return new ResourceLocation(dimletKey.getKey());
            case DIGIT:
                return null;
            case ADMIN:
                return null;
        }
        return null;
    }

    public static ITextComponent getReadable(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case ATTRIBUTE:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BIOME_CONTROLLER:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BIOME:
                ResourceLocation id = new ResourceLocation(dimletKey.getKey());
                String trans = "biome." + id.getNamespace() + "." + id.getPath();
                return new TranslationTextComponent(trans);
            case FEATURE:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case TIME:
                return new StringTextComponent(dimletKey.getKey().toLowerCase());
            case BLOCK:
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dimletKey.getKey()));
                return new TranslationTextComponent(block.getDescriptionId());
            case FLUID:
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(dimletKey.getKey()));
                return new TranslationTextComponent(fluid.defaultFluidState().createLegacyBlock().getBlock().getDescriptionId());
            case DIGIT:
                return new StringTextComponent(dimletKey.getKey());
            case ADMIN:
                return new StringTextComponent(dimletKey.getKey());
        }
        return new StringTextComponent("<unknown>");
    }

    // Use client side!
    public static String getReadableName(DimletKey dimletKey) {
        switch (dimletKey.getType()) {
            case TERRAIN:
                return dimletKey.getKey().toLowerCase();
            case ATTRIBUTE:
                return dimletKey.getKey().toLowerCase();
            case BIOME_CONTROLLER:
                return dimletKey.getKey().toLowerCase();
            case BIOME:
                ResourceLocation id = new ResourceLocation(dimletKey.getKey());
                String trans = "biome." + id.getNamespace() + "." + id.getPath();
                return I18n.get(trans);
            case FEATURE:
                return dimletKey.getKey().toLowerCase();
            case TIME:
                return dimletKey.getKey().toLowerCase();
            case BLOCK:
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dimletKey.getKey()));
                if (block != null) {
                    String modName = Tools.getModName(block);
                    if ("minecraft".equalsIgnoreCase(modName)) {
                        return I18n.get(block.getDescriptionId());
                    } else {
                        return I18n.get(block.getDescriptionId()) + " (" + modName + ")";
                    }
                }
                return "<Invalid " + dimletKey.getKey() + ">";
            case FLUID:
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(dimletKey.getKey()));
                if (fluid != null) {
                    String modName = Tools.getModName(fluid);
                    if ("minecraft".equalsIgnoreCase(modName)) {
                        return I18n.get(fluid.defaultFluidState().createLegacyBlock().getBlock().getDescriptionId());
                    } else {
                        return I18n.get(fluid.defaultFluidState().createLegacyBlock().getBlock().getDescriptionId())
                                + " (" + modName + ")";
                    }
                }
                return "<Invalid " + dimletKey.getKey() + ">";
            case DIGIT:
                return dimletKey.getKey();
            case ADMIN:
                return dimletKey.getKey();
        }
        return "<unknown>";
    }

    public static boolean isFullEssence(ItemStack stack, ItemStack desired, String desiredKey) {
        if (stack.sameItem(desired)) {
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
            } else if (stack.getItem() == EssencesModule.FLUID_ABSORBER_ITEM.get()) {
                String fluid = FluidAbsorberTileEntity.getFluid(stack);
                if (Objects.equals(desiredKey, fluid)) {
                    return FluidAbsorberTileEntity.getProgress(stack) >= 100;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isOwnerDimlet(DimletKey dimletKey) {
        return dimletKey != null && dimletKey.getType() == DimletType.ADMIN && dimletKey.getKey().equals(AdminDimletType.OWNER.name().toLowerCase());
    }

    /// Return true if this dimlet can exist (refers to an existing block/biome/...)
    public static boolean isValidDimlet(DimletKey key) {
        switch (key.getType()) {
            case BIOME:
                return ForgeRegistries.BIOMES.getValue(new ResourceLocation(key.getKey())) != null;
            case BLOCK:
                Block value = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key.getKey()));
                return value != null && value != Blocks.AIR;
            case FLUID:
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(key.getKey()));
                return fluid != null && fluid.defaultFluidState().createLegacyBlock().getBlock() != Blocks.AIR;
            default:
                return true;
        }
    }
}
