package mcjty.rftoolsdim.dimlets;

import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.biomes.BiomeLayoutType;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class DimletDictionary {

    private Map<DimletKey, DimletSettings> dimlets = new HashMap<>();

    public DimletDictionary() {
        register(new DimletKey(DimletType.TERRAIN, TerrainType.FLAT.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.NORMAL.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.VOID.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.TERRAIN, TerrainType.WAVING.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.BIOME, BiomeLayoutType.DEFAULT.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.BIOME, BiomeLayoutType.CHECKER.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.FEATURE, FeatureType.NONE.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.FEATURE, FeatureType.SPHERES.name()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());

        register(new DimletKey(DimletType.MATERIAL, Blocks.STONE.getRegistryName().toString()), DimletSettings.create(DimletRarity.COMMON, 10, 1, 1).build());
        register(new DimletKey(DimletType.MATERIAL, Blocks.DIAMOND_BLOCK.getRegistryName().toString()), DimletSettings.create(DimletRarity.RARE, 1000, 1000, 1000).build());
    }

    private void register(DimletKey key, DimletSettings settings) {
        dimlets.put(key, settings);
    }
}
