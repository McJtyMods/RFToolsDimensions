package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.lib.varia.WeightedRandomSelector;
import mcjty.rftoolsdim.config.DimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.List;
import java.util.Random;

public class DimletRandomizer {

    public static final int RARITY_0 = 0;
    public static final int RARITY_1 = 1;
    public static final int RARITY_2 = 2;
    public static final int RARITY_3 = 3;
    public static final int RARITY_4 = 4;
    public static final int RARITY_5 = 5;
    public static final int RARITY_6 = 6;

    public static DimletKey getRandomTerrain(Random random, boolean forWorldGen) {
        // @todo
        TerrainType terrainType = TerrainType.values()[random.nextInt(TerrainType.values().length)];
        return new DimletKey(DimletType.DIMLET_TERRAIN, terrainType.getId());
    }

    public static DimletKey getRandomFeature(Random random, boolean forWorldGen) {
        // @todo
        FeatureType featureType = FeatureType.values()[random.nextInt(FeatureType.values().length)];
        return new DimletKey(DimletType.DIMLET_FEATURE, featureType.getId());
    }

    public static DimletKey getRandomEffect(Random random, boolean forWorldGen) {
        // @todo
        EffectType effectType = EffectType.values()[random.nextInt(EffectType.values().length)];
        return new DimletKey(DimletType.DIMLET_EFFECT, effectType.getId());
    }

    public static DimletKey getRandomStructure(Random random, boolean forWorldGen) {
        // @todo
        StructureType structureType = StructureType.values()[random.nextInt(StructureType.values().length)];
        return new DimletKey(DimletType.DIMLET_STRUCTURE, structureType.getId());
    }

    public static DimletKey getRandomFluidBlock(Random random, boolean forWorldGen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_LIQUID, Blocks.water.getRegistryName()+"@0");
    }

    public static DimletKey getRandomMaterialBlock(Random random, boolean forWorldGen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_MATERIAL, Blocks.stone.getRegistryName()+"@0");
    }

    public static DimletKey getRandomController(Random random, boolean forWorldGen) {
        ControllerType type = ControllerType.values()[random.nextInt(ControllerType.values().length)];
        return new DimletKey(DimletType.DIMLET_CONTROLLER, type.getId());
    }

    public static DimletKey getRandomBiome(Random random, boolean forWorldGen) {
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        while(true) {
            BiomeGenBase biome = biomes[random.nextInt(biomes.length)];
            if (biome != null) {
                return new DimletKey(DimletType.DIMLET_BIOME, Integer.toString(biome.biomeID));
            }
        }
    }

    public static DimletKey getRandomMob(Random random, boolean forWorldGen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_MOB, "@@@");
    }

    private static WeightedRandomSelector<Integer, ItemStack> dimletPartDistribution;

    // Get a random part item
    public static ItemStack getRandomPart(Random random) {
        if (dimletPartDistribution == null) {
            dimletPartDistribution = new WeightedRandomSelector<>();
            dimletPartDistribution.addRarity(RARITY_0, DimletConfiguration.rarity0);
            dimletPartDistribution.addRarity(RARITY_1, DimletConfiguration.rarity1);
            dimletPartDistribution.addRarity(RARITY_2, DimletConfiguration.rarity2);
            dimletPartDistribution.addRarity(RARITY_3, DimletConfiguration.rarity3);
            dimletPartDistribution.addRarity(RARITY_4, DimletConfiguration.rarity4);
            dimletPartDistribution.addRarity(RARITY_5, DimletConfiguration.rarity5);
            dimletPartDistribution.addRarity(RARITY_6, DimletConfiguration.rarity6);
            List<List<ItemStack>> stacks = KnownDimletConfiguration.getRandomPartLists();
            for (int i = 0 ; i < stacks.size() ; i++) {
                final int finalI = i;
                stacks.get(i).stream().forEach(s -> dimletPartDistribution.addItem(finalI, s));
            }
        }
        return dimletPartDistribution.select(random);
    }

}
