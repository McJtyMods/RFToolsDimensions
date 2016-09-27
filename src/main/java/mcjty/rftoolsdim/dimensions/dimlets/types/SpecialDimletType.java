package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.types.SpecialType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class SpecialDimletType implements IDimletType {

    @Override
    public String getName() {
        return "Special";
    }

    @Override
    public String getOpcode() {
        return "X";
    }

    @Override
    public String getTextureName() {
        return "specialDimlet";
    }

    @Override
    public void setupFromConfig(Configuration cfg) {
    }

    @Override
    public boolean isModifier() {
        return false;
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
        return true;
    }

    @Override
    public void inject(DimletKey key, DimensionInformation dimensionInformation) {
        SpecialType specialType = DimletObjectMapping.getSpecial(key);
        if (specialType == SpecialType.SPECIAL_PEACEFUL) {
            dimensionInformation.setPeaceful(true);
        } else if (specialType == SpecialType.SPECIAL_NOANIMALS) {
            dimensionInformation.setNoanimals(true);
        } else if (specialType == SpecialType.SPECIAL_SPAWN) {
            dimensionInformation.setRespawnHere(true);
        } else if (specialType == SpecialType.SPECIAL_CHEATER) {
            dimensionInformation.setCheater(true);
        }
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        dimlets = DimensionInformation.extractType(DimletType.DIMLET_SPECIAL, dimlets);
        for (Pair<DimletKey, List<DimletKey>> dimlet : dimlets) {
            DimletKey key = dimlet.getLeft();
            SpecialType specialType = DimletObjectMapping.getSpecial(key);
            if (specialType == SpecialType.SPECIAL_PEACEFUL) {
                dimensionInformation.setPeaceful(true);
            } else if (specialType == SpecialType.SPECIAL_NOANIMALS) {
                dimensionInformation.setNoanimals(true);
            } else if (specialType == SpecialType.SPECIAL_SHELTER) {
                dimensionInformation.setShelter(true);
            } else if (specialType == SpecialType.SPECIAL_SPAWN) {
                dimensionInformation.setRespawnHere(true);
            } else if (specialType == SpecialType.SPECIAL_CHEATER) {
                dimensionInformation.setCheater(true);
            }
        }
    }

    @Override
    public String[] getInformation() {
        return new String[] { "Special dimlets with various features." };
    }

    private static boolean isValidSpecialEssence(ItemStack stackEssence) {
        Item peaceEssence = ForgeRegistries.ITEMS.getValue(new ResourceLocation("rftools", "peace_essence"));

        if (stackEssence.getItem() == peaceEssence) {
            return true;
        }
        if (stackEssence.getItem() == ModItems.efficiencyEssenceItem) {
            return true;
        }
        if (stackEssence.getItem() == ModItems.mediocreEfficiencyEssenceItem) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack getDefaultEssence(DimletKey key) {
        if (SpecialType.SPECIAL_PEACEFUL.getId().equals(key.getId())) {
            return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("rftools", "peace_essence")));
        } else if (SpecialType.SPECIAL_EFFICIENCY.getId().equals(key.getId())) {
            return new ItemStack(ModItems.efficiencyEssenceItem);
        } else if (SpecialType.SPECIAL_EFFICIENCY_LOW.getId().equals(key.getId())) {
            return new ItemStack(ModItems.mediocreEfficiencyEssenceItem);
        }
        return null;
    }


        @Override
    public DimletKey isValidEssence(ItemStack stackEssence) {
        Item peaceEssence = ForgeRegistries.ITEMS.getValue(new ResourceLocation("rftools", "peace_essence"));
        if (stackEssence.getItem() == peaceEssence) {
            return new DimletKey(DimletType.DIMLET_SPECIAL, SpecialType.SPECIAL_PEACEFUL.getId());
        } else if (stackEssence.getItem() == ModItems.efficiencyEssenceItem) {
            return new DimletKey(DimletType.DIMLET_SPECIAL, SpecialType.SPECIAL_EFFICIENCY.getId());
        } else if (stackEssence.getItem() == ModItems.mediocreEfficiencyEssenceItem) {
            return new DimletKey(DimletType.DIMLET_SPECIAL, SpecialType.SPECIAL_EFFICIENCY_LOW.getId());
        }
        return null;
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        if (!isValidSpecialEssence(stackEssence)) {
            return null;
        }
        DimletKey specialDimlet = isValidEssence(stackEssence);
        if (specialDimlet == null) {
            return null;
        }
        if (!DimletCraftingTools.matchDimletRecipe(specialDimlet, stackController, stackMemory, stackEnergy)) {
            return null;
        }
        return specialDimlet;
    }
}
