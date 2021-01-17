package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biomes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DimletDictionary {

    private static DimletDictionary INSTANCE = new DimletDictionary();

    private Map<DimletKey, DimletSettings> dimlets = new HashMap<>();

    public static DimletDictionary get() {
        return INSTANCE;
    }

    public DimletDictionary() {
        register(new DimletKey(DimletType.TERRAIN, TerrainType.FLAT.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.NORMAL.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.VOID.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.WAVES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.BIOME_CONTROLLER, BiomeControllerType.DEFAULT.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME_CONTROLLER, BiomeControllerType.CHECKER.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME_CONTROLLER, BiomeControllerType.SINGLE.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.BIOME, Biomes.PLAINS.getLocation().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME, Biomes.DESERT.getLocation().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME, Biomes.OCEAN.getLocation().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME, Biomes.DEEP_OCEAN.getLocation().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.FEATURE, FeatureType.NONE.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.FEATURE, FeatureType.SPHERES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.FEATURE, FeatureType.CUBES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.BLOCK, Blocks.STONE.getRegistryName().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BLOCK, Blocks.DIAMOND_BLOCK.getRegistryName().toString()), DimletSettings.create(DimletRarity.RARE, 1000, 1000, 1000).build());
        register(new DimletKey(DimletType.BLOCK, Blocks.GOLD_BLOCK.getRegistryName().toString()), DimletSettings.create(DimletRarity.RARE, 500, 500, 500).build());
        register(new DimletKey(DimletType.BLOCK, Blocks.IRON_BLOCK.getRegistryName().toString()), DimletSettings.create(DimletRarity.RARE, 500, 500, 500).build());
    }

    private void register(DimletKey key, DimletSettings settings) {
        dimlets.put(key, settings);
    }

    public Set<DimletKey> getDimlets() {
        return dimlets.keySet();
    }
}
