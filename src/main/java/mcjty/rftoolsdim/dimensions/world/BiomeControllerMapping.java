package mcjty.rftoolsdim.dimensions.world;

import mcjty.rftoolsdim.dimensions.types.ControllerType;
import net.minecraft.world.biome.Biome;

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
        makeFilteredBiomeMap(coldBiomeReplacements, ControllerType.CONTROLLER_COLD);
        makeFilteredBiomeMap(warmBiomeReplacements, ControllerType.CONTROLLER_WARM);
        makeFilteredBiomeMap(mediumBiomeReplacements, ControllerType.CONTROLLER_MEDIUM);
        makeFilteredBiomeMap(wetBiomeReplacements, ControllerType.CONTROLLER_WET);
        makeFilteredBiomeMap(dryBiomeReplacements, ControllerType.CONTROLLER_DRY);
        makeFilteredBiomeMap(fieldsBiomeReplacements, ControllerType.CONTROLLER_FIELDS);
        makeFilteredBiomeMap(mountainsBiomeReplacements, ControllerType.CONTROLLER_MOUNTAINS);
        makeFilteredBiomeMap(magicalBiomeReplacements, ControllerType.CONTROLLER_MAGICAL);
        makeFilteredBiomeMap(forestBiomeReplacements, ControllerType.CONTROLLER_FOREST);
    }

    private static void makeFilteredBiomeMap(Map<Integer, Integer> map, ControllerType type) {
        makeFilteredBiomeMap(map, type.getFilter());
    }

    public static void makeFilteredBiomeMap(Map<Integer, Integer> map, ControllerType.BiomeFilter filter) {
        map.clear();

        // First check if there exist biomes for a certain filter.
        boolean ok = false;
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null) {
                try {
                    if (filter.match(biome)) {
                        ok = true;
                        break;
                    }
                } catch(RuntimeException e) {
                    throw new RuntimeException("Checking suitability of biome " + biome.biomeName + " (" + Biome.REGISTRY.getNameForObject(biome) + ")", e);
                }
            }
        }

        if (!ok) {
            // No biomes found! We just map every biome to itself as a fallback.
            for (Biome biome : Biome.REGISTRY) {
                if (biome != null) {
                    map.put(Biome.getIdForBiome(biome), Biome.getIdForBiome(biome));
                }
            }
        } else {
            for (Biome biome : Biome.REGISTRY) {
                if (biome != null) {
                    try {
                        if (filter.match(biome)) {
                            map.put(Biome.getIdForBiome(biome), Biome.getIdForBiome(biome));
                        } else {
                            map.put(Biome.getIdForBiome(biome), findSuitableBiomes(biome, filter));
                        }
                    } catch(RuntimeException e) {
                        throw new RuntimeException("Checking suitability of biome " + biome.biomeName + " (" + Biome.REGISTRY.getNameForObject(biome) + ")", e);
                    }
                }
            }
        }
    }

    private static int findSuitableBiomes(Biome biome, ControllerType.BiomeFilter filter) {
        double bestdist = 1000000000.0f;
        int bestidx = 0;        // Make sure we always have some matching biome.

        for (Biome base : Biome.REGISTRY) {
            try {
                if (base != null && filter.match(base)) {
                    // This 'base' could be a replacement. Check if it is close enough.
                    if (biome.getBiomeClass() == base.getBiomeClass()) {
                        // Same class, that's good enough for me.
                        return Biome.getIdForBiome(base);
                    }

                    double dist = filter.calculateBiomeDistance(biome, base);
                    if (dist < bestdist) {
                        bestdist = dist;
                        bestidx = Biome.getIdForBiome(base);
                    }
                }
            } catch(RuntimeException e) {
                throw new RuntimeException("Checking suitability of biome " + base.biomeName + " (" + Biome.REGISTRY.getNameForObject(base) + ")", e);
            }
        }
        return bestidx;
    }

}
