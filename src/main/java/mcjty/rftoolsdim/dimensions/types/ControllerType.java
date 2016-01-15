package mcjty.rftoolsdim.dimensions.types;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashMap;
import java.util.Map;

public enum ControllerType {
    CONTROLLER_DEFAULT("Default", 0, null),
    CONTROLLER_SINGLE("Single", 1, null),
    CONTROLLER_CHECKERBOARD("Checker", 2, null),
    CONTROLLER_COLD("Cold", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return biome.getTempCategory() == BiomeGenBase.TempCategory.COLD;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, true, false);
        }
    }),
    CONTROLLER_MEDIUM("Medium", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return biome.getTempCategory() == BiomeGenBase.TempCategory.MEDIUM;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, true, false);
        }
    }),
    CONTROLLER_WARM("Warm", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return biome.getTempCategory() == BiomeGenBase.TempCategory.WARM;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, true, false);
        }
    }),
    CONTROLLER_DRY("Dry", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return biome.rainfall < 0.1;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, true, false, false);
        }
    }),
    CONTROLLER_WET("Wet", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return biome.isHighHumidity();
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, true, false, false);
        }
    }),
    CONTROLLER_FIELDS("Fields", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            float rootHeight = biome.minHeight;
            float heightVariation = biome.maxHeight;
            return heightVariation < 0.11 && rootHeight < 0.25f;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, false, true);
        }
    }),
    CONTROLLER_MOUNTAINS("Mountains", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            float heightVariation = biome.maxHeight;
            return heightVariation > 0.45f;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, false, true);
        }
    }),
    CONTROLLER_FILTERED("Filtered", -1, null),
    CONTROLLER_MAGICAL("Magical", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL) || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SPOOKY);
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, false, false);
        }
    }),
    CONTROLLER_FOREST("Forest", 0, new BiomeFilter() {
        @Override
        public boolean match(BiomeGenBase biome) {
            return biome.theBiomeDecorator.treesPerChunk >= 5;
        }

        @Override
        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b) {
            return calculateBiomeDistance(a, b, false, false, false);
        }
    });

    private static final Map<String,ControllerType> CONTROLLER_TYPE_MAP = new HashMap<>();

    static {
        for (ControllerType type : ControllerType.values()) {
            CONTROLLER_TYPE_MAP.put(type.getId(), type);
        }
    }


    private final String id;
    private final int neededBiomes;
    private final BiomeFilter filter;

    ControllerType(String id, int neededBiomes, BiomeFilter filter) {
        this.id = id;
        this.neededBiomes = neededBiomes;
        this.filter = filter;
    }

    public String getId() {
        return id;
    }

    public static ControllerType getControllerById(String id) {
        return CONTROLLER_TYPE_MAP.get(id);
    }


    /**
     * Return the amount of biomes needed for this controller. -1 means that it can use any number of biomes.
     */
    public int getNeededBiomes() {
        return neededBiomes;
    }

    public BiomeFilter getFilter() {
        return filter;
    }

    public abstract static class BiomeFilter {
        /**
         * Return true if this biome should be selected by this filter.
         */
        public abstract boolean match(BiomeGenBase biome);

        /**
         * Return the similarity distance between two biomes.
         */
        public abstract double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b);

        public double calculateBiomeDistance(BiomeGenBase a, BiomeGenBase b, boolean ignoreRain, boolean ignoreTemperature, boolean ignoreHeight) {
            float dr = a.rainfall - b.rainfall;
            if (ignoreRain) {
                dr = 0.0f;
            }
            float dt = a.temperature - b.temperature;
            if (ignoreTemperature) {
                dt = 0.0f;
            }

            float dv = a.maxHeight - b.maxHeight;
            float dh = a.minHeight - b.minHeight;
            if (ignoreHeight) {
                dv = 0.0f;
                dh = 0.0f;
            }
            return Math.sqrt(dr * dr + dt * dt + dv * dv + dh * dh);
        }
    }
}
