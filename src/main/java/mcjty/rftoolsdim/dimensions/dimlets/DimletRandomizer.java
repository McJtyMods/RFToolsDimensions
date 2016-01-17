package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

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
        return new DimletKey(DimletType.DIMLET_TERRAIN, TerrainType.TERRAIN_VOID.getId());
    }

    public static DimletKey getRandomFeature(Random random, boolean forWorldGen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_FEATURE, FeatureType.FEATURE_NONE.getId());
    }

    public static DimletKey getRandomEffect(Random random, boolean forWorldGen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_EFFECT, EffectType.EFFECT_NONE.getId());
    }

    public static DimletKey getRandomStructure(Random random, boolean forWorldGen) {
        // @todo
        return new DimletKey(DimletType.DIMLET_STRUCTURE, StructureType.STRUCTURE_NONE.getId());
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
}
