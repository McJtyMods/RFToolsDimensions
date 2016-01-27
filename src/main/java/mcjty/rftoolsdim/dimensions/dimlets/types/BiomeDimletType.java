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
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeDimletType implements IDimletType {
    @Override
    public String getName() {
        return "Biome";
    }

    @Override
    public String getOpcode() {
        return "B";
    }

    @Override
    public String getTextureName() {
        return "biomeDimlet";
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
        return false;
    }

    @Override
    public void inject(DimletKey key, DimensionInformation dimensionInformation) {
    }

    @Override
    public void constructDimension(List<Pair<DimletKey, List<DimletKey>>> dimlets, Random random, DimensionInformation dimensionInformation) {
        Set<DimletKey> biomeKeys = new HashSet<DimletKey>();
        List<Pair<DimletKey, List<DimletKey>>> biomeDimlets = DimensionInformation.extractType(DimletType.DIMLET_BIOME, dimlets);
        List<Pair<DimletKey, List<DimletKey>>> controllerDimlets = DimensionInformation.extractType(DimletType.DIMLET_CONTROLLER, dimlets);

        ControllerType controllerType;

        // First determine the controller to use.
        if (controllerDimlets.isEmpty()) {
            if (random.nextFloat() < WorldgenConfiguration.randomControllerChance) {
                DimletKey key = DimletRandomizer.getRandomController(random);
                if (key != null) {
                    controllerType = DimletObjectMapping.getController(key);
                } else {
                    controllerType = ControllerType.CONTROLLER_DEFAULT;
                }
            } else {
                if (biomeDimlets.isEmpty()) {
                    controllerType = ControllerType.CONTROLLER_DEFAULT;
                } else if (biomeDimlets.size() > 1) {
                    controllerType = ControllerType.CONTROLLER_FILTERED;
                } else {
                    controllerType = ControllerType.CONTROLLER_SINGLE;
                }
            }
        } else {
            DimletKey key = controllerDimlets.get(random.nextInt(controllerDimlets.size())).getLeft();
            controllerType = DimletObjectMapping.getController(key);
        }
        dimensionInformation.setControllerType(controllerType);

        // Now see if we have to add or randomize biomes.
        for (Pair<DimletKey, List<DimletKey>> dimletWithModifiers : biomeDimlets) {
            DimletKey key = dimletWithModifiers.getKey();
            biomeKeys.add(key);
        }

        int neededBiomes = controllerType.getNeededBiomes();
        if (neededBiomes == -1) {
            // Can work with any number of biomes.
            if (biomeKeys.size() >= 2) {
                neededBiomes = biomeKeys.size();     // We already have enough biomes
            } else {
                neededBiomes = random.nextInt(10) + 3;
            }
        }

        while (biomeKeys.size() < neededBiomes) {
            DimletKey key = DimletRandomizer.getRandomBiome(random);
            while (key == null || biomeKeys.contains(key)) {
                key = DimletRandomizer.getRandomBiome(random);
            }
            biomeKeys.add(key);
        }

        List<BiomeGenBase> biomes = dimensionInformation.getBiomes();
        biomes.clear();
        for (DimletKey key : biomeKeys) {
            biomes.add(DimletObjectMapping.getBiome(key));
        }
    }

    @Override
    public String[] getInformation() {
        return new String[] { "This dimlet controls the biomes that can generate in a dimension", "The controller specifies how they can be used." };
    }

    @Override
    public DimletKey isValidEssence(ItemStack stackEssence) {
        Block essenceBlock = BlockTools.getBlock(stackEssence);

        if (essenceBlock != ModBlocks.biomeAbsorberBlock) {
            return null;
        }
        NBTTagCompound essenceCompound = stackEssence.getTagCompound();
        if (essenceCompound == null) {
            return null;
        }
        int absorbing = essenceCompound.getInteger("absorbing");
        String biome = essenceCompound.getString("biome");
        if (absorbing > 0 || biome == null) {
            return null;
        }
        return findBiomeDimlet(essenceCompound);
    }

    @Override
    public ItemStack getDefaultEssence() {
        return new ItemStack(ModBlocks.biomeAbsorberBlock);
    }

    private static DimletKey findBiomeDimlet(NBTTagCompound essenceCompound) {
        String biome = essenceCompound.getString("biome");
        DimletKey key = new DimletKey(DimletType.DIMLET_BIOME, biome);
        Settings settings = KnownDimletConfiguration.getSettings(key);
        if (settings == null || !settings.isDimlet()) {
            return null;
        }
        return key;
    }

    @Override
    public DimletKey attemptDimletCrafting(ItemStack stackController, ItemStack stackMemory, ItemStack stackEnergy, ItemStack stackEssence) {
        DimletKey biomeDimlet = isValidEssence(stackEssence);
        if (biomeDimlet == null) {
            return null;
        }
        if (!DimletCraftingTools.matchDimletRecipe(biomeDimlet, stackController, stackMemory, stackEnergy)) {
            return null;
        }
        return biomeDimlet;
    }
}
