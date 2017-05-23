package mcjty.rftoolsdim.dimensions.dimlets.types;

import mcjty.lib.varia.BlockTools;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class FeatureDimletType implements IDimletType {
    private static final String CATEGORY_TYPE = "type_feature";

    private static class FactorCosts {
        float materialCreationCostFactor;
        float materialMaintenanceCostFactor;
        float materialTickCostFactor;
        float liquidCreationCostFactor;
        float liquidMaintenanceCostFactor;
        float liquidTickCostFactor;

        private FactorCosts(float materialCreationCostFactor, float materialMaintenanceCostFactor, float materialTickCostFactor, float liquidCreationCostFactor, float liquidMaintenanceCostFactor, float liquidTickCostFactor) {
            this.materialCreationCostFactor = materialCreationCostFactor;
            this.materialMaintenanceCostFactor = materialMaintenanceCostFactor;
            this.materialTickCostFactor = materialTickCostFactor;
            this.liquidCreationCostFactor = liquidCreationCostFactor;
            this.liquidMaintenanceCostFactor = liquidMaintenanceCostFactor;
            this.liquidTickCostFactor = liquidTickCostFactor;
        }
    }

    // Index in the following array is the cost class from FeatureType.
    private static FactorCosts[] factors = new FactorCosts[4];
    static {
        factors[0] = new FactorCosts(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        factors[1] = new FactorCosts(1.4f, 1.4f, 1.3f, 1.4f, 1.4f, 1.3f);
        factors[2] = new FactorCosts(1.8f, 1.8f, 1.5f, 1.8f, 1.8f, 1.5f);
        factors[3] = new FactorCosts(2.5f, 2.5f, 1.7f, 2.5f, 2.5f, 1.7f);
    }

    @Override
    public String getName() {
        return "Feature";
    }

    @Override
    public String getOpcode() {
        return "f";
    }

    @Override
    public String getTextureName() {
        return "featureDimlet";
    }

    @Override
    public void setupFromConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_TYPE, "Settings for the feature dimlet type");

        String[] desc = new String[] { "lowest class", "low class", "medium class", "high class" };
        for (int i = 0 ; i < 4 ; i++) {
            FactorCosts fc = factors[i];
            fc.materialCreationCostFactor = (float) cfg.get(CATEGORY_TYPE, "material.creation.factor." + i, fc.materialCreationCostFactor, "The cost factor for a material dimlet modifier when used in combination with a feature of " + desc[i]).getDouble();
            fc.liquidCreationCostFactor = (float) cfg.get(CATEGORY_TYPE, "liquid.creation.factor." + i, fc.liquidCreationCostFactor, "The cost factor for a liquid dimlet modifier when used in combination with a feature of " + desc[i]).getDouble();
            fc.materialMaintenanceCostFactor = (float) cfg.get(CATEGORY_TYPE, "material.maintenance.factor." + i, fc.materialMaintenanceCostFactor, "The cost factor for a material dimlet modifier when used in combination with a feature of " + desc[i]).getDouble();
            fc.liquidMaintenanceCostFactor = (float) cfg.get(CATEGORY_TYPE, "liquid.maintenance.factor." + i, fc.liquidMaintenanceCostFactor, "The cost factor for a liquid dimlet modifier when used in combination with a feature of " + desc[i]).getDouble();
            fc.materialTickCostFactor = (float) cfg.get(CATEGORY_TYPE, "material.tick.factor." + i, fc.materialTickCostFactor, "The cost factor for a material dimlet modifier when used in combination with a feature of " + desc[i]).getDouble();
            fc.liquidTickCostFactor = (float) cfg.get(CATEGORY_TYPE, "liquid.tick.factor." + i, fc.liquidTickCostFactor, "The cost factor for a liquid dimlet modifier when used in combination with a feature of " + desc[i]).getDouble();
        }
    }

    @Override
    public boolean isModifier() {
        return false;
    }

    @Override
    public boolean isModifiedBy(DimletType type) {
        return type == DimletType.DIMLET_MATERIAL || type == DimletType.DIMLET_LIQUID;
    }

    @Override
    public float getModifierCreateCostFactor(DimletType modifierType, DimletKey key) {
        FeatureType featureType = DimletObjectMapping.getFeature(key);
        if (modifierType == DimletType.DIMLET_MATERIAL) {
            return factors[featureType.getMaterialClass()].materialCreationCostFactor;
        } else if (modifierType == DimletType.DIMLET_LIQUID) {
            return factors[featureType.getLiquidClass()].liquidCreationCostFactor;
        } else {
            return 1.0f;
        }
    }

    @Override
    public float getModifierMaintainCostFactor(DimletType modifierType, DimletKey key) {
        FeatureType featureType = DimletObjectMapping.getFeature(key);
        if (modifierType == DimletType.DIMLET_MATERIAL) {
            return factors[featureType.getMaterialClass()].materialMaintenanceCostFactor;
        } else if (modifierType == DimletType.DIMLET_LIQUID) {
            return factors[featureType.getLiquidClass()].liquidMaintenanceCostFactor;
        } else {
            return 1.0f;
        }
    }

    @Override
    public float getModifierTickCostFactor(DimletType modifierType, DimletKey key) {
        FeatureType featureType = DimletObjectMapping.getFeature(key);
        if (modifierType == DimletType.DIMLET_MATERIAL) {
            return factors[featureType.getMaterialClass()].materialTickCostFactor;
        } else if (modifierType == DimletType.DIMLET_LIQUID) {
            return factors[featureType.getLiquidClass()].liquidTickCostFactor;
        } else {
            return 1.0f;
        }
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
        TerrainType terrainType = dimensionInformation.getTerrainType();
        Set<FeatureType> featureTypes = dimensionInformation.getFeatureTypes();
        dimlets = DimensionInformation.extractType(DimletType.DIMLET_FEATURE, dimlets);
        if (dimlets.isEmpty()) {
            while (random.nextFloat() < WorldgenConfiguration.randomFeatureChance) {
                DimletKey key = DimletRandomizer.getRandomFeature(random);
                if (key != null) {
                    FeatureType featureType = DimletObjectMapping.getFeature(key);
                    if (!featureTypes.contains(featureType) && featureType.isTerrainSupported(terrainType)) {
                        dimensionInformation.updateCostFactor(key);
                        featureTypes.add(featureType);
                        List<DimletKey> modifiers = Collections.emptyList();
                        // @todo randomize those?
                        dimlets.add(Pair.of(key, modifiers));
                    }
                }
            }
        }

        Map<FeatureType,List<DimletKey>> modifiersForFeature = new HashMap<FeatureType, List<DimletKey>>();
        for (Pair<DimletKey, List<DimletKey>> dimlet : dimlets) {
            FeatureType featureType = DimletObjectMapping.getFeature(dimlet.getLeft());
            featureTypes.add(featureType);
            modifiersForFeature.put(featureType, dimlet.getRight());
        }

        dimensionInformation.setFluidsForLakes(getRandomFluidArray(random, dimensionInformation, featureTypes, modifiersForFeature, FeatureType.FEATURE_LAKES, true));
        dimensionInformation.setExtraOregen(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_OREGEN, true));
        dimensionInformation.setTendrilBlock(dimensionInformation.getFeatureBlock(random, modifiersForFeature, FeatureType.FEATURE_TENDRILS));
        dimensionInformation.setSphereBlocks(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_ORBS, false));
        dimensionInformation.setPyramidBlocks(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_PYRAMIDS, false));
        dimensionInformation.setHugeSphereBlocks(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_HUGEORBS, false));
        dimensionInformation.setScatteredSphereBlocks(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_SCATTEREDORBS, false));
        dimensionInformation.setLiquidSphereBlocks(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_LIQUIDORBS, false));
        dimensionInformation.setLiquidSphereFluids(getRandomFluidArray(random, dimensionInformation, featureTypes, modifiersForFeature, FeatureType.FEATURE_LIQUIDORBS, false));
        dimensionInformation.setHugeLiquidSphereBlocks(getRandomBlockArray(random, featureTypes, modifiersForFeature, FeatureType.FEATURE_HUGELIQUIDORBS, false));
        dimensionInformation.setHugeLiquidSphereFluids(getRandomFluidArray(random, dimensionInformation, featureTypes, modifiersForFeature, FeatureType.FEATURE_HUGELIQUIDORBS, false));
        dimensionInformation.setCanyonBlock(dimensionInformation.getFeatureBlock(random, modifiersForFeature, FeatureType.FEATURE_CANYONS));
    }

    private Block[] getRandomFluidArray(Random random, DimensionInformation dimensionInformation, Set<FeatureType> featureTypes, Map<FeatureType, List<DimletKey>> modifiersForFeature, FeatureType t, boolean allowEmpty) {
        Block[] fluidsForLakes;
        if (featureTypes.contains(t)) {
            List<IBlockState> blocks = new ArrayList<>();
            List<Block> fluids = new ArrayList<>();
            DimensionInformation.getMaterialAndFluidModifiers(modifiersForFeature.get(t), blocks, fluids);

            // If no fluids are specified we will usually have default fluid generation (water+lava). Otherwise some random selection.
            if (fluids.isEmpty()) {
                while (random.nextFloat() < WorldgenConfiguration.randomLakeFluidChance) {
                    DimletKey key = DimletRandomizer.getRandomFluidBlock(random);
                    if (key != null) {
                        dimensionInformation.updateCostFactor(key);
                        fluids.add(DimletObjectMapping.getFluid(key));
                    }
                }
            } else if (fluids.size() == 1 && fluids.get(0) == null) {
                fluids.clear();
            }
            fluidsForLakes = fluids.toArray(new Block[fluids.size()]);
            for (int i = 0 ; i < fluidsForLakes.length ; i++) {
                if (fluidsForLakes[i] == null) {
                    fluidsForLakes[i] = Blocks.WATER;
                }
            }
        } else {
            fluidsForLakes = new Block[0];
        }
        if (allowEmpty || fluidsForLakes.length > 0) {
            return fluidsForLakes;
        }

        return new Block[] { Blocks.WATER };
    }

    private IBlockState[] getRandomBlockArray(Random random, Set<FeatureType> featureTypes, Map<FeatureType, List<DimletKey>> modifiersForFeature, FeatureType t, boolean allowEmpty) {
        IBlockState[] blockArray;
        if (featureTypes.contains(t)) {
            List<IBlockState> blocks = new ArrayList<>();
            List<Block> fluids = new ArrayList<Block>();
            DimensionInformation.getMaterialAndFluidModifiers(modifiersForFeature.get(t), blocks, fluids);

            // If no blocks are specified we will generate a few random ores.
            if (blocks.isEmpty()) {
                float chance = 1.1f;
                while (random.nextFloat() < chance) {
                    DimletKey key = DimletRandomizer.getRandomMaterialBlock(random);
                    if (key != null) {
                        IBlockState bm = DimletObjectMapping.getBlock(key);
                        if (bm != null) {
                            blocks.add(bm);
                            chance = chance * 0.80f;
                        }
                    }
                }
            }

            blockArray = blocks.toArray(new IBlockState[blocks.size()]);
            for (int i = 0 ; i < blockArray.length ; i++) {
                if (blockArray[i] == null) {
                    blockArray[i] = Blocks.STONE.getDefaultState();
                }
            }
        } else {
            blockArray = new IBlockState[0];
        }
        if (allowEmpty || blockArray.length > 0) {
            return blockArray;
        }
        return new IBlockState[] { Blocks.STONE.getDefaultState() };
    }

    @Override
    public String[] getInformation() {
        return new String[] { "This affects various features of the dimension.", "Some of these features need material or liquid modifiers", "which you have to put in front of this feature." };
    }

    @Override
    public DimletKey isValidEssence(ItemStack stackEssence) {
        Block essenceBlock = BlockTools.getBlock(stackEssence);

        if (essenceBlock != ModBlocks.featureAbsorberBlock) {
            return null;
        }
        NBTTagCompound essenceCompound = stackEssence.getTagCompound();
        if (essenceCompound == null) {
            return null;
        }
        int absorbing = essenceCompound.getInteger("absorbing");
        String feature = essenceCompound.getString("feature");
        if (absorbing > 0 || feature == null) {
            return null;
        }
        return findFeatureDimlet(essenceCompound);
    }

    @Override
    public ItemStack getDefaultEssence(DimletKey key) {
        return new ItemStack(ModBlocks.featureAbsorberBlock);
    }

    private static DimletKey findFeatureDimlet(NBTTagCompound essenceCompound) {
        String feature = essenceCompound.getString("feature");
        DimletKey key = new DimletKey(DimletType.DIMLET_FEATURE, feature);
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if (settings == null || !settings.isDimlet()) {
            return null;
        }
        return key;
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        DimletKey featureDimlet = isValidEssence(stackEssence);
        if (featureDimlet == null) {
            return null;
        }
        if (!DimletCraftingTools.matchDimletRecipe(featureDimlet, stackController, stackMemory, stackEnergy)) {
            return null;
        }
        return featureDimlet;
    }
}
