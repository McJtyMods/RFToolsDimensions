package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.lib.varia.BlockTools;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class MaterialDimletType implements IDimletType {

    @Override
    public String getName() {
        return "Material";
    }

    @Override
    public String getOpcode() {
        return "m";
    }

    @Override
    public String getTextureName() {
        return "materialDimlet";
    }

    @Override
    public void setupFromConfig(Configuration cfg) {
    }

    @Override
    public boolean isModifier() {
        return true;
    }

    @Override
    public boolean isModifiedBy(DimletType type) {
        return false;
    }

    @Override
    public float getModifierCreateCostFactor(DimletType modifierType, DimletKey key) {
        return 1.0f;
    }

    @Override
    public float getModifierMaintainCostFactor(DimletType modifierType, DimletKey key) {
        return 1.0f;
    }

    @Override
    public float getModifierTickCostFactor(DimletType modifierType, DimletKey key) {
        return 1.0f;
    }

    @Override
    public boolean isInjectable() {
        return false;
    }

    @Override
    public void inject(DimletKey key, DimensionInformation dimensionInformation) {

    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        // As a modifier this is handled in the dimlet that is being modified.
    }

    @Override
    public String[] getInformation() {
        return new String[] { "This is a modifier for terrain, tendrils, canyons, orbs,", "liquid orbs, or oregen.", "Put these dimlets BEFORE the thing you want", "to change." };
    }

    @Override
    public DimletKey isValidEssence(ItemStack stackEssence) {
        Block essenceBlock = BlockTools.getBlock(stackEssence);

        if (essenceBlock != ModBlocks.materialAbsorberBlock) {
            return null;
        }
        NBTTagCompound essenceCompound = stackEssence.getTagCompound();
        if (essenceCompound == null) {
            return null;
        }
        int absorbing = essenceCompound.getInteger("absorbing");
        if (absorbing > 0 || !essenceCompound.hasKey("block")) {
            return null;
        }
        return findMaterialDimlet(essenceCompound);
    }

    @Override
    public ItemStack getDefaultEssence(DimletKey key) {
        return new ItemStack(ModBlocks.materialAbsorberBlock);
    }

    private static DimletKey findMaterialDimlet(NBTTagCompound essenceCompound) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(essenceCompound.getString("block")));
        int meta = essenceCompound.getInteger("meta");
        DimletKey key = new DimletKey(DimletType.DIMLET_MATERIAL, block.getRegistryName() + "@" + meta);
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if (settings == null || !settings.isDimlet()) {
            return null;
        }
        return key;
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        DimletKey materialDimlet = isValidEssence(stackEssence);
        if (materialDimlet == null) {
            return null;
        }
        if (!DimletCraftingTools.matchDimletRecipe(materialDimlet, stackController, stackMemory, stackEnergy)) {
            return null;
        }
        return materialDimlet;
    }
}
