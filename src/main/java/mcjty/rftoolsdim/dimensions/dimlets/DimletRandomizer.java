package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.config.DimletConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import mcjty.rftoolsdim.varia.RarityRandomSelector;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

public class DimletRandomizer {

    public static final int RARITY_0 = 0;
    public static final int RARITY_1 = 1;
    public static final int RARITY_2 = 2;
    public static final int RARITY_3 = 3;
    public static final int RARITY_4 = 4;
    public static final int RARITY_5 = 5;
    public static final int RARITY_6 = 6;

    // All dimlet ids in a weighted random selector based on rarity.
    private static RarityRandomSelector<Integer,DimletKey> randomDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomUncraftableDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomMaterialDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomLiquidDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomMobDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomStructureDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomEffectDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomFeatureDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomTerrainDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomSkyDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomSkyBodyDimlets;
    private static RarityRandomSelector<Integer,DimletKey> randomWeatherDimlets;

    public static void init() {
        randomDimlets = null;
        randomUncraftableDimlets = null;
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

        SortedMap<DimletKey, Settings> knownDimlets = KnownDimletConfiguration.getKnownDimlets();
        boolean rarityScalesBySize = DimletConfiguration.rarityScalesBySize;
        float rarity0 = DimletConfiguration.rarity0;
        float rarity1 = DimletConfiguration.rarity1;
        float rarity2 = DimletConfiguration.rarity2;
        float rarity3 = DimletConfiguration.rarity3;
        float rarity4 = DimletConfiguration.rarity4;
        float rarity5 = DimletConfiguration.rarity5;
        float rarity6 = DimletConfiguration.rarity6;

        randomDimlets = new RarityRandomSelector<>(rarityScalesBySize);
        setupRarity(randomDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);

        randomUncraftableDimlets = new RarityRandomSelector<>(rarityScalesBySize);
        setupRarity(randomUncraftableDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);

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
                    randomMaterialDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomMaterialDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomMaterialDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_LIQUID) {
                if (randomLiquidDimlets == null) {
                    randomLiquidDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomLiquidDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomLiquidDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_MOB) {
                if (randomMobDimlets == null) {
                    randomMobDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomMobDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomMobDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_EFFECT) {
                if (randomEffectDimlets == null) {
                    randomEffectDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomEffectDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomEffectDimlets.addItem(entry.getValue().getRarity(), key);
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_FEATURE) {
                if (randomFeatureDimlets == null) {
                    randomFeatureDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomFeatureDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomFeatureDimlets.addItem(entry.getValue().getRarity(), key);
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_STRUCTURE) {
                if (randomStructureDimlets == null) {
                    randomStructureDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomStructureDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomStructureDimlets.addItem(entry.getValue().getRarity(), key);
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_TERRAIN) {
                if (randomTerrainDimlets == null) {
                    randomTerrainDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomTerrainDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomTerrainDimlets.addItem(entry.getValue().getRarity(), key);
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_WEATHER) {
                if (randomWeatherDimlets == null) {
                    randomWeatherDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomWeatherDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomWeatherDimlets.addItem(entry.getValue().getRarity(), key);
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_CONTROLLER) {
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            } else if (key.getType() == DimletType.DIMLET_SKY) {
                if (randomSkyDimlets == null) {
                    randomSkyDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                    setupRarity(randomSkyDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                }
                randomSkyDimlets.addItem(entry.getValue().getRarity(), key);
                if (SkyRegistry.isSkyBody(key)) {
                    if (randomSkyBodyDimlets == null) {
                        randomSkyBodyDimlets = new RarityRandomSelector<>(rarityScalesBySize);
                        setupRarity(randomSkyBodyDimlets, rarity0, rarity1, rarity2, rarity3, rarity4, rarity5, rarity6);
                    }
                    randomSkyBodyDimlets.addItem(entry.getValue().getRarity(), key);
                }
                randomUncraftableDimlets.addItem(entry.getValue().getRarity(), key);
            }
        }
    }

    private static void setupRarity(RarityRandomSelector<Integer,DimletKey> randomDimlets, float rarity0, float rarity1, float rarity2, float rarity3, float rarity4, float rarity5, float rarity6) {
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
        keys.sort(null);
        int size = keys.size();
        while(true) {
            Biome biome = Biome.REGISTRY.getObject(keys.get(random.nextInt(size)));
            if (biome != null) {
                return new DimletKey(DimletType.DIMLET_BIOME, biome.biomeName);
            }
        }
    }

    public static DimletKey getRandomMob(Random random) {
        setupWeightedRandomList();
        return randomMobDimlets == null ? null : randomMobDimlets.select(random);
    }

    public static RarityRandomSelector<Integer, DimletKey> getRandomDimlets() {
        setupWeightedRandomList();
        return randomDimlets;
    }

    public static RarityRandomSelector<Integer, DimletKey> getRandomUncraftableDimlets() {
        setupWeightedRandomList();
        return randomUncraftableDimlets;
    }

    private static RarityRandomSelector<Integer, ItemStack> dimletPartDistribution;

    // Get a random part item
    public static ItemStack getRandomPart(Random random) {
        if (dimletPartDistribution == null) {
            dimletPartDistribution = new RarityRandomSelector<>(rarityScalesBySize);
            dimletPartDistribution.addRarity(RARITY_0, DimletConfiguration.rarity0);
            dimletPartDistribution.addRarity(RARITY_1, DimletConfiguration.rarity1);
            dimletPartDistribution.addRarity(RARITY_2, DimletConfiguration.rarity2);
            dimletPartDistribution.addRarity(RARITY_3, DimletConfiguration.rarity3);
            dimletPartDistribution.addRarity(RARITY_4, DimletConfiguration.rarity4);
            dimletPartDistribution.addRarity(RARITY_5, DimletConfiguration.rarity5);
            dimletPartDistribution.addRarity(RARITY_6, DimletConfiguration.rarity6);
            List<List<ItemStack>> stacks = KnownDimletConfiguration.getRandomPartLists();
            for (int i = 0; i < stacks.size(); ++i) {
                for(ItemStack s : stacks.get(i)) {
                    dimletPartDistribution.addItem(i, s);
                }
            }
        }
        return dimletPartDistribution.select(random).copy();
    }

}
