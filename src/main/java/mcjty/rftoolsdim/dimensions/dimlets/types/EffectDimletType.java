package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class EffectDimletType implements IDimletType {

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
            while (random.nextFloat() < WorldgenConfiguration.randomEffectChance) {
                DimletKey key = DimletRandomizer.getRandomEffect(random);
                if (key != null) {
                    EffectType effectType = DimletObjectMapping.getEffect(key);
                    if (!effectTypes.contains(effectType)) {
                        dimensionInformation.updateCostFactor(key);
                        effectTypes.add(effectType);
                    }
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
