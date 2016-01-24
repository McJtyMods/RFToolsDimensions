package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.description.MobDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class MobDimletType implements IDimletType {

    @Override
    public String getName() {
        return "Mob";
    }

    @Override
    public String getOpcode() {
        return "M";
    }

    @Override
    public String getTextureName() {
        return "mobsDimlet";
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
        MobDescriptor mobDescriptor = DimletObjectMapping.getMob(key);
        if (mobDescriptor != null && mobDescriptor.getEntityClass() != null) {
            dimensionInformation.getExtraMobs().add(mobDescriptor);
        } else {
            dimensionInformation.getExtraMobs().clear();
        }
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
//        List<MobDescriptor> extraMobs = dimensionInformation.getExtraMobs();
        // @todo

//
//        dimlets = DimensionInformation.extractType(DimletType.DIMLET_MOB, dimlets);
//        if (dimlets.isEmpty()) {
//            while (random.nextFloat() < DimletConfiguration.randomExtraMobsChance) {
//                DimletKey key = DimletRandomizer.getRandomMob(random, false);
//                dimensionInformation.updateCostFactor(key);
//                extraMobs.add(DimletObjectMapping.getMob(key));
//            }
//        } else {
//            DimletKey key = dimlets.get(0).getLeft();
//            MobDescriptor mobDescriptor = DimletObjectMapping.getMob(key);
//            if (dimlets.size() == 1 && (mobDescriptor == null || mobDescriptor.getEntityClass() == null)) {
//                // Just default.
//            } else {
//                for (Pair<DimletKey, List<DimletKey>> dimletWithModifiers : dimlets) {
//                    DimletKey modifierKey = dimletWithModifiers.getLeft();
//                    MobDescriptor descriptor = DimletObjectMapping.getMob(modifierKey);
//                    if (descriptor != null && descriptor.getEntityClass() != null) {
//                        extraMobs.add(descriptor);
//                    }
//                }
//            }
//        }
    }

    @Override
    public String[] getInformation() {
        return new String[] { "Control what type of mobs can spawn", "in addition to normal mob spawning." };
    }

    private static boolean isValidMobEssence(ItemStack stackEssence, NBTTagCompound essenceCompound) {
//        if (stackEssence.getItem() != DimletConstructionSetup.syringeItem) {
//            return false;
//        }
//        if (essenceCompound == null) {
//            return false;
//        }
//        int level = essenceCompound.getInteger("level");
//        String mob = essenceCompound.getString("mobName");
//        if (level < DimletConstructionConfiguration.maxMobInjections || mob == null) {
//            return false;
//        }
        return true;
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        if (!isValidMobEssence(stackEssence, stackEssence.getTagCompound())) {
            return null;
        }
        String mob = stackEssence.getTagCompound().getString("mobName");
        if (!DimletCraftingTools.matchDimletRecipe(new DimletKey(DimletType.DIMLET_MOB, mob), stackController, stackMemory, stackEnergy)) {
            return null;
        }
        DimletKey mobDimlet = new DimletKey(DimletType.DIMLET_MOB, mob);
        return mobDimlet;
    }
}
