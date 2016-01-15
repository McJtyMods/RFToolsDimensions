package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.rftoolsdim.dimensions.DimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class EffectDimletType implements IDimletType {
    private static final String CATEGORY_TYPE = "type_effect";

    private static int rarity = DimletRandomizer.RARITY_3;
    private static int baseCreationCost = 200;
    private static int baseMaintainCost = 0;
    private static int baseTickCost = 100;

    @Override
    public String getName() {
        return "Effect";
    }

    @Override
    public String getOpcode() {
        return "e";
    }

    @Override
    public String getTextureName() {
        return "effectDimlet";
    }

    @Override
    public void setupFromConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_TYPE, "Settings for the effect dimlet type");
        rarity = cfg.get(CATEGORY_TYPE, "rarity", rarity, "Default rarity for this dimlet type").getInt();
        baseCreationCost = cfg.get(CATEGORY_TYPE, "creation.cost", baseCreationCost, "Dimlet creation cost (how much power this dimlets adds during creation time of a dimension)").getInt();
        baseMaintainCost = cfg.get(CATEGORY_TYPE, "maintenance.cost", baseMaintainCost, "Dimlet maintenance cost (how much power this dimlet will use up to keep the dimension running)").getInt();
        baseTickCost = cfg.get(CATEGORY_TYPE, "tick.cost", baseTickCost, "Dimlet tick cost (how long it takes to make a dimension with this dimlet in it)").getInt();
    }

    @Override
    public int getRarity() {
        return rarity;
    }

    @Override
    public int getCreationCost() {
        return baseCreationCost;
    }

    @Override
    public int getMaintenanceCost() {
        return baseMaintainCost;
    }

    @Override
    public int getTickCost() {
        return baseTickCost;
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
        Set<EffectType> effectTypes = dimensionInformation.getEffectTypes();
        EffectType effectType = DimletObjectMapping.getEffect(key);
        if (EffectType.EFFECT_NONE.equals(effectType)) {
            effectTypes.clear();
        } else {
            effectTypes.add(effectType);
        }
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        Set<EffectType> effectTypes = dimensionInformation.getEffectTypes();
        dimlets = DimensionInformation.extractType(DimletType.DIMLET_EFFECT, dimlets);
        if (dimlets.isEmpty()) {
            while (random.nextFloat() < DimletConfiguration.randomEffectChance) {
                DimletKey key = DimletRandomizer.getRandomEffect(random, false);
                EffectType effectType = DimletObjectMapping.getEffect(key);
                if (!effectTypes.contains(effectType)) {
                    dimensionInformation.updateCostFactor(key);
                    effectTypes.add(effectType);
                }
            }
        } else {
            for (Pair<DimletKey, List<DimletKey>> dimletWithModifier : dimlets) {
                DimletKey key = dimletWithModifier.getLeft();
                EffectType effectType = DimletObjectMapping.getEffect(key);
                if (effectType != EffectType.EFFECT_NONE) {
                    effectTypes.add(effectType);
                }
            }
        }
    }

    @Override
    public String[] getInformation() {
        return new String[] { "Control various environmental effects", "in the dimension." };
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        return null;
    }
}
