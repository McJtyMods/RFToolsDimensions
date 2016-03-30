package mcjty.rftoolsdim.dimensions.world;

import mcjty.rftoolsdim.dimensions.types.ControllerType;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.HashMap;
import java.util.Map;

public class BiomeControllerMapping {

    public static final Map<Integer, Integer> coldBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> warmBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> mediumBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> wetBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> dryBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> fieldsBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> mountainsBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> magicalBiomeReplacements = new HashMap<>();
    public static final Map<Integer, Integer> forestBiomeReplacements = new HashMap<>();


    public static void setupControllerBiomes() {
        BiomeGenBase[] biomeGenArray = null;//@todoBiomeGenBase.getBiomeGenArray();

        makeFilteredBiomeMap(biomeGenArray, coldBiomeReplacements, ControllerType.CONTROLLER_COLD);
        makeFilteredBiomeMap(biomeGenArray, warmBiomeReplacements, ControllerType.CONTROLLER_WARM);
        makeFilteredBiomeMap(biomeGenArray, mediumBiomeReplacements, ControllerType.CONTROLLER_MEDIUM);
        makeFilteredBiomeMap(biomeGenArray, wetBiomeReplacements, ControllerType.CONTROLLER_WET);
        makeFilteredBiomeMap(biomeGenArray, dryBiomeReplacements, ControllerType.CONTROLLER_DRY);
        makeFilteredBiomeMap(biomeGenArray, fieldsBiomeReplacements, ControllerType.CONTROLLER_FIELDS);
        makeFilteredBiomeMap(biomeGenArray, mountainsBiomeReplacements, ControllerType.CONTROLLER_MOUNTAINS);
        makeFilteredBiomeMap(biomeGenArray, magicalBiomeReplacements, ControllerType.CONTROLLER_MAGICAL);
        makeFilteredBiomeMap(biomeGenArray, forestBiomeReplacements, ControllerType.CONTROLLER_FOREST);
    }

    private static void makeFilteredBiomeMap(BiomeGenBase[] biomeGenArray, Map<Integer, Integer> map, ControllerType type) {
        makeFilteredBiomeMap(biomeGenArray, map, type.getFilter());
    }

    public static void makeFilteredBiomeMap(BiomeGenBase[] biomeGenArray, Map<Integer, Integer> map, ControllerType.BiomeFilter filter) {
        map.clear();

        // First check if there exist biomes for a certain filter.
        boolean ok = false;
        for (BiomeGenBase biome : biomeGenArray) {
            if (biome != null) {
                if (filter.match(biome)) {
                    ok = true;
                    break;
                }
            }
        }

        if (!ok) {
            // No biomes found! We just map every biome to itself as a fallback.
            for (BiomeGenBase biome : biomeGenArray) {
                if (biome != null) {
                    map.put(BiomeGenBase.getIdForBiome(biome), BiomeGenBase.getIdForBiome(biome));
                }
            }
        } else {
            for (BiomeGenBase biome : biomeGenArray) {
                if (biome != null) {
                    if (filter.match(biome)) {
                        map.put(BiomeGenBase.getIdForBiome(biome), BiomeGenBase.getIdForBiome(biome));
                    } else {
                        map.put(BiomeGenBase.getIdForBiome(biome), findSuitableBiomes(biomeGenArray, biome, filter));
                    }
                }
            }
        }
    }

    private static int findSuitableBiomes(BiomeGenBase[] biomeGenArray, BiomeGenBase biome, ControllerType.BiomeFilter filter) {
        double bestdist = 1000000000.0f;
        int bestidx = 0;        // Make sure we always have some matching biome.

        for (BiomeGenBase base : biomeGenArray) {
            if (base != null && filter.match(base)) {
                // This 'base' could be a replacement. Check if it is close enough.
                if (biome.getBiomeClass() == base.getBiomeClass()) {
                    // Same class, that's good enough for me.
                    return BiomeGenBase.getIdForBiome(base);
                }

                double dist = filter.calculateBiomeDistance(biome, base);
                if (dist < bestdist) {
                    bestdist = dist;
                    bestidx = BiomeGenBase.getIdForBiome(base);
                }
            }
        }
        return bestidx;
    }

}
