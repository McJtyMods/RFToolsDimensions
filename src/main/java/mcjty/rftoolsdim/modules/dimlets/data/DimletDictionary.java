package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class DimletDictionary {

    private Map<DimletKey, DimletSettings> dimlets = new HashMap<>();

    public DimletDictionary() {
        register(new DimletKey(DimletType.TERRAIN, TerrainType.FLAT.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.NORMAL.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.VOID.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.WAVES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.BIOME_CONTROLLER, BiomeControllerType.DEFAULT.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME_CONTROLLER, BiomeControllerType.CHECKER.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME_CONTROLLER, BiomeControllerType.SINGLE.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.FEATURE, FeatureType.NONE.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.FEATURE, FeatureType.SPHERES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.FEATURE, FeatureType.CUBES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.BLOCK, Blocks.STONE.getRegistryName().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BLOCK, Blocks.DIAMOND_BLOCK.getRegistryName().toString()), DimletSettings.create(DimletRarity.RARE, 1000, 1000, 1000).build());
    }

    private void register(DimletKey key, DimletSettings settings) {
        dimlets.put(key, settings);
    }
}
