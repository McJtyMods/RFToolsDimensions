package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.lib.varia.WeightedRandomSelector;
import mcjty.rftoolsdim.config.DimletConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DimletRandomizer {

    public static final int RARITY_0 = 0;
    public static final int RARITY_1 = 1;
    public static final int RARITY_2 = 2;
    public static final int RARITY_3 = 3;
    public static final int RARITY_4 = 4;
    public static final int RARITY_5 = 5;
    public static final int RARITY_6 = 6;

    // All dimlet ids in a weighted random selector based on rarity.
    private static WeightedRandomSelector<Integer,DimletKey> randomDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomMaterialDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomLiquidDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomMobDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomStructureDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomEffectDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomFeatureDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomTerrainDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomSkyDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomSkyBodyDimlets;
    private static WeightedRandomSelector<Integer,DimletKey> randomWeatherDimlets;

    public static void init() {
        randomDimlets = null;
        randomMaterialDimlets = null;
        randomLiquidDimlets = null;
        randomMobDimlets = null;
        randomStructureDimlets = null;
        randomEffectDimlets = null;
        randomFeatureDimlets = null;
        randomTerrainDimlets = null;
        randomSkyDimlets = null;
        randomSkyBodyDimlets = null;
        randomWeatherDimlets = null;
    }

    private static void setupWeightedRandomList() {
        if (randomDimlets != null) {
            return;
        }

        Map<DimletKey, Settings> knownDimlets = KnownDimletConfiguration.getKnownDimlets();
        float rarity0 = DimletConfiguration.rarity0;
        float rarity1 = DimletConfiguration.rarity1;
        float rarity2 = DimletConfiguration.rarity2;
        float rarity3 = DimletConfiguration.rarity3;
        float rarity4 = DimletConfiguration.rarity4;
        float rarity5 = DimletConfiguration.rarity5;
        float rarity6 = DimletConfiguration.rarity6;

        randomDimlets = new WeightedRandomSelector<>();
        setupRarity(randomDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);

        for (Map.Entry<DimletKey, Settings> entry : knownDimlets.entrySet()) {
            DimletKey key = entry.getKey();
            Settings settings = KnownDimletConfiguration.getSettings(key);
            if (settings == null) {
                continue;
            }
            if (!settings.isWorldgen()) {
                continue;
            }

            randomDimlets.addItem(entry.getValue().getRarity(), key);
            if (key.getType() == DimletType.DIMLET_MATERIAL) {
                if (randomMaterialDimlets == null) {
                    randomMaterialDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomMaterialDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomMaterialDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_LIQUID) {
                if (randomLiquidDimlets == null) {
                    randomLiquidDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomLiquidDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomLiquidDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_MOB) {
                if (randomMobDimlets == null) {
                    randomMobDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomMobDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomMobDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_EFFECT) {
                if (randomEffectDimlets == null) {
                    randomEffectDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomEffectDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomEffectDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_FEATURE) {
                if (randomFeatureDimlets == null) {
                    randomFeatureDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomFeatureDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomFeatureDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_STRUCTURE) {
                if (randomStructureDimlets == null) {
                    randomStructureDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomStructureDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomStructureDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_TERRAIN) {
                if (randomTerrainDimlets == null) {
                    randomTerrainDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomTerrainDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomTerrainDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_WEATHER) {
                if (randomWeatherDimlets == null) {
                    randomWeatherDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomWeatherDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomWeatherDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_SKY) {
                if (randomSkyDimlets == null) {
                    randomSkyDimlets = new WeightedRandomSelector<>();
                    setupRarity(randomSkyDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomSkyDimlets.addItem(entry.getValue().getRarity(), key);
                if (SkyRegistry.isSkyBody(key)) {
                    if (randomSkyBodyDimlets == null) {
                        randomSkyBodyDimlets = new WeightedRandomSelector<>();
                        setupRarity(randomSkyBodyDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                    }
                    randomSkyBodyDimlets.addItem(entry.getValue().getRarity(), key);
                }
            }
        }
    }

    private static void setupRarity(WeightedRandomSelector<Integer,DimletKey> randomDimlets, float rarity0, float rarity1, float rarity2, float rarity3, float rarity4, float rarity5, float rarity6) {
        randomDimlets.addRarity(RARITY_0, rarity0);
        randomDimlets.addRarity(RARITY_1, rarity1);
        randomDimlets.addRarity(RARITY_2, rarity2);
        randomDimlets.addRarity(RARITY_3, rarity3);
        randomDimlets.addRarity(RARITY_4, rarity4);
        randomDimlets.addRarity(RARITY_5, rarity5);
        randomDimlets.addRarity(RARITY_6, rarity6);
    }


    public static DimletKey getRandomTerrain(Random random) {
        setupWeightedRandomList();
        return randomTerrainDimlets == null ? null : randomTerrainDimlets.select(random);
    }

    public static DimletKey getRandomFeature(Random random) {
        setupWeightedRandomList();
        return randomFeatureDimlets == null ? null : randomFeatureDimlets.select(random);
    }

    public static DimletKey getRandomEffect(Random random) {
        setupWeightedRandomList();
        return randomEffectDimlets == null ? null : randomEffectDimlets.select(random);
    }

    public static DimletKey getRandomStructure(Random random) {
        setupWeightedRandomList();
        return randomStructureDimlets == null ? null : randomStructureDimlets.select(random);
    }

    public static DimletKey getRandomFluidBlock(Random random) {
        setupWeightedRandomList();
        return randomLiquidDimlets == null ? null : randomLiquidDimlets.select(random);
    }

    public static DimletKey getRandomMaterialBlock(Random random) {
        setupWeightedRandomList();
        return randomMaterialDimlets == null ? null : randomMaterialDimlets.select(random);
    }

    public static DimletKey getRandomSky(Random random) {
        setupWeightedRandomList();
        return randomSkyDimlets == null ? null : randomSkyDimlets.select(random);
    }

    public static DimletKey getRandomWeather(Random random) {
        setupWeightedRandomList();
        return randomWeatherDimlets == null ? null : randomWeatherDimlets.select(random);
    }

    public static DimletKey getRandomSkyBody(Random random) {
        setupWeightedRandomList();
        return randomSkyBodyDimlets == null ? null : randomSkyBodyDimlets.select(random);
    }

    public static DimletKey getRandomController(Random random) {
        ControllerType type = ControllerType.values()[random.nextInt(ControllerType.values().length)];
        return new DimletKey(DimletType.DIMLET_CONTROLLER, type.getId());
    }

    public static DimletKey getRandomBiome(Random random) {
        List<ResourceLocation> keys = new ArrayList<>(Biome.REGISTRY.getKeys());
        int size = keys.size();
        while(true) {
            Biome biome = Biome.REGISTRY.getObject(keys.get(random.nextInt(size)));
            if (biome != null) {
                return new DimletKey(DimletType.DIMLET_BIOME, biome.getBiomeName());
            }
        }
    }

    public static DimletKey getRandomMob(Random random) {
        setupWeightedRandomList();
        return randomMobDimlets == null ? null : randomMobDimlets.select(random);
    }

    public static WeightedRandomSelector<Integer, DimletKey> getRandomDimlets() {
        setupWeightedRandomList();
        return randomDimlets;
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
