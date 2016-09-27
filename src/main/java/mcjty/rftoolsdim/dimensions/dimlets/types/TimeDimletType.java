package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.lib.varia.BlockTools;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.blocks.absorbers.TimeAbsorberTileEntity;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeDimletType implements IDimletType {

    @Override
    public String getName() {
        return "Time";
    }

    @Override
    public String getOpcode() {
        return "t";
    }

    @Override
    public String getTextureName() {
        return "timeDimlet";
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
        dimensionInformation.setCelestialAngle(DimletObjectMapping.getCelestialAngle(key));
        dimensionInformation.setTimeSpeed(DimletObjectMapping.getTimeSpeed(key));
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        Float celestialAngle = null;
        Float timeSpeed = null;
        dimlets = DimensionInformation.extractType(DimletType.DIMLET_TIME, dimlets);
        if (dimlets.isEmpty()) {
            if (random.nextFloat() < WorldgenConfiguration.randomSpecialTimeChance) {
                celestialAngle = null;      // Default
                timeSpeed = null;
            } else {
                List<DimletKey> keys = new ArrayList<>(DimletObjectMapping.getTimeDimlets());
                DimletKey key = keys.get(random.nextInt(keys.size()));
                celestialAngle = DimletObjectMapping.getCelestialAngle(key);
                timeSpeed = DimletObjectMapping.getTimeSpeed(key);
            }
        } else {
            DimletKey key = dimlets.get(random.nextInt(dimlets.size())).getKey();
            celestialAngle = DimletObjectMapping.getCelestialAngle(key);
            timeSpeed = DimletObjectMapping.getTimeSpeed(key);
        }
        dimensionInformation.setCelestialAngle(celestialAngle);
        dimensionInformation.setTimeSpeed(timeSpeed);
    }

    @Override
    public String[] getInformation() {
        return new String[] { "Control the flow of time." };
    }

    private static boolean isValidTimeEssence(ItemStack stackEssence, NBTTagCompound essenceCompound) {
        Block essenceBlock = BlockTools.getBlock(stackEssence);

        if (essenceBlock != ModBlocks.timeAbsorberBlock) {
            return false;
        }
        if (essenceCompound == null) {
            return false;
        }
        int absorbing = essenceCompound.getInteger("absorbing");
        float angle = essenceCompound.getFloat("angle");
        if (absorbing > 0 || angle < -0.01f) {
            return false;
        }
        return true;
    }

    private static DimletKey findTimeDimlet(ItemStack stackEssence) {
        float angle = stackEssence.getTagCompound().getFloat("angle");
        return TimeAbsorberTileEntity.findBestTimeDimlet(angle);
    }

    @Override
    public ItemStack getDefaultEssence(DimletKey key) {
        return new ItemStack(ModBlocks.timeAbsorberBlock);
    }


    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        if (!isValidTimeEssence(stackEssence, stackEssence.getTagCompound())) {
            return null;
        }
        DimletKey timeDimlet = findTimeDimlet(stackEssence);
        if (timeDimlet == null) {
            return null;
        }
        if (!DimletCraftingTools.matchDimletRecipe(timeDimlet, stackController, stackMemory, stackEnergy)) {
            return null;
        }
        return timeDimlet;
    }
}
