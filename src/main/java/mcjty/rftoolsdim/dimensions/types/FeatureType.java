package mcjty.rftoolsdim.dimensions.types;

import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;

import java.util.*;

import static mcjty.rftoolsdim.dimensions.types.TerrainType.*;


public enum FeatureType {
    FEATURE_NONE("None", null, null, 0, 0),
    FEATURE_CAVES("Caves", new TSet(TERRAIN_AMPLIFIED, TERRAIN_CAVERN, TERRAIN_FLOODED_CAVERN, TERRAIN_CHAOTIC, TERRAIN_FLAT, TERRAIN_ISLAND, TERRAIN_ISLANDS, TERRAIN_LOW_CAVERN, TERRAIN_NORMAL, TERRAIN_PLATEAUS, TERRAIN_NEARLANDS, TERRAIN_SOLID, TERRAIN_WAVES, TERRAIN_FILLEDWAVES, TERRAIN_ROUGH), null, 0, 0),
    FEATURE_RAVINES("Ravines", new TSet(TERRAIN_AMPLIFIED, TERRAIN_CAVERN, TERRAIN_FLOODED_CAVERN, TERRAIN_CHAOTIC, TERRAIN_FLAT, TERRAIN_ISLAND, TERRAIN_ISLANDS, TERRAIN_LOW_CAVERN, TERRAIN_NORMAL, TERRAIN_PLATEAUS, TERRAIN_NEARLANDS, TERRAIN_SOLID, TERRAIN_WAVES, TERRAIN_FILLEDWAVES, TERRAIN_ROUGH), null, 0, 0),
    FEATURE_ORBS("Orbs", null, new MMap(-1, DimletType.DIMLET_MATERIAL), 1, 0),
    FEATURE_OREGEN("Oregen", null, new MMap(-1, DimletType.DIMLET_MATERIAL), 0, 0),
    FEATURE_LAKES("Lakes", new TSet(TERRAIN_AMPLIFIED, TERRAIN_CAVERN, TERRAIN_CHAOTIC, TERRAIN_FLAT, TERRAIN_ISLAND, TERRAIN_ISLANDS, TERRAIN_LOW_CAVERN, TERRAIN_NORMAL, TERRAIN_PLATEAUS, TERRAIN_NEARLANDS, TERRAIN_SOLID, TERRAIN_WAVES, TERRAIN_FILLEDWAVES, TERRAIN_ROUGH), new MMap(-1, DimletType.DIMLET_LIQUID), 0, 0),
    FEATURE_TENDRILS("Tendrils", null, new MMap(1, DimletType.DIMLET_MATERIAL), 2, 0),
    FEATURE_CANYONS("Canyons", null, new MMap(1, DimletType.DIMLET_MATERIAL), 2, 0),
    FEATURE_MAZE("Maze", new TSet(TERRAIN_AMPLIFIED, TERRAIN_FLAT, TERRAIN_NORMAL, TERRAIN_NEARLANDS), null, 0, 0),
    FEATURE_LIQUIDORBS("LiquidOrbs", null, new MMap(-1, DimletType.DIMLET_MATERIAL, DimletType.DIMLET_LIQUID), 1, 1),
    FEATURE_SHALLOW_OCEAN("ShallowOcean", new TSet(TERRAIN_PLATEAUS, TERRAIN_ISLANDS, TERRAIN_ISLAND, TERRAIN_CHAOTIC), new MMap(1, DimletType.DIMLET_LIQUID), 0, 3),
    FEATURE_VOLCANOES("Volcanoes", null, null, 0, 0),
    FEATURE_DENSE_CAVES("DenseCaves", new TSet(TERRAIN_AMPLIFIED, TERRAIN_CAVERN, TERRAIN_FLOODED_CAVERN, TERRAIN_CHAOTIC, TERRAIN_FLAT, TERRAIN_ISLAND, TERRAIN_ISLANDS, TERRAIN_LOW_CAVERN, TERRAIN_NORMAL, TERRAIN_PLATEAUS, TERRAIN_NEARLANDS, TERRAIN_SOLID, TERRAIN_WAVES, TERRAIN_FILLEDWAVES, TERRAIN_ROUGH), null, 0, 0),
    FEATURE_HUGEORBS("HugeOrbs", null, new MMap(-1, DimletType.DIMLET_MATERIAL), 2, 0),
    FEATURE_HUGELIQUIDORBS("HugeLiquidOrbs", null, new MMap(-1, DimletType.DIMLET_MATERIAL, DimletType.DIMLET_LIQUID), 1, 2),
    FEATURE_NODIMLETBUILDINGS("NoDimletBuildings", null, null, 0, 0),
    FEATURE_PYRAMIDS("Pyramids", new TSet(TERRAIN_AMPLIFIED, TERRAIN_CAVERN, TERRAIN_FLOODED_CAVERN, TERRAIN_FLAT, TERRAIN_CHAOTIC, TERRAIN_ISLAND, TERRAIN_ISLANDS, TERRAIN_LOW_CAVERN, TERRAIN_NEARLANDS, TERRAIN_NORMAL, TERRAIN_PLATEAUS, TERRAIN_SOLID, TERRAIN_WAVES, TERRAIN_FILLEDWAVES, TERRAIN_ROUGH),
            new MMap(-1, DimletType.DIMLET_MATERIAL), 1, 0),
    FEATURE_CLEAN("Clean", null, null, 0, 0),
    FEATURE_SCATTEREDORBS("ScatteredOrbs", null, new MMap(-1, DimletType.DIMLET_MATERIAL), 1, 0),
    FEATURE_ORESAPLENTY("OresAPlenty", null, null, 0, 0);

    private final String id;                    // Unique id to use in dimlets
    private final Set<TerrainType> supportedTerrains;
    private final Map<DimletType,Integer> supportedModifiers;
    private final int materialClass;            // A value indicating how expensive material modifiers should be for this feature (0 is cheapest, 3 is most expensive)
    private final int liquidClass;              // A value indicating how expensive liquid modifiers should be for this feature (0 is cheapest, 3 is most expensive)

    private static final Map<String,FeatureType> FEATURE_TYPE_MAP = new HashMap<>();

    static {
        for (FeatureType type : FeatureType.values()) {
            FEATURE_TYPE_MAP.put(type.getId(), type);
        }
    }

    /**
     * If no terrain types are given then they are all supported.
     */
    FeatureType(String id, Set<TerrainType> terrainTypes, Map<DimletType, Integer> modifiers, int materialClass, int liquidClass) {
        this.id = id;
        if (terrainTypes == null) {
            supportedTerrains = Collections.emptySet();
        } else {
            supportedTerrains = new HashSet<>(terrainTypes);
        }
        if (modifiers == null) {
            supportedModifiers = Collections.emptyMap();
        } else {
            supportedModifiers = new HashMap<>(modifiers);
        }
        this.materialClass = materialClass;
        this.liquidClass = liquidClass;
    }

    public boolean isTerrainSupported(TerrainType type) {
        return supportedTerrains.isEmpty() || supportedTerrains.contains(type);
    }

    public String getId() {
        return id;
    }

    public boolean supportsAllTerrains() {
        return supportedTerrains.isEmpty();
    }

    public int getMaterialClass() {
        return materialClass;
    }

    public int getLiquidClass() {
        return liquidClass;
    }

    public Integer getSupportedModifierAmount(DimletType type) {
        return supportedModifiers.get(type);
    }

    public static FeatureType getFeatureById(String id) {
        return FEATURE_TYPE_MAP.get(id);
    }

    private static class TSet extends HashSet<TerrainType> {
        private TSet(TerrainType... terrainTypes) {
            for (TerrainType type : terrainTypes) {
                add(type);
            }
        }
    }

    private static class MMap extends HashMap<DimletType,Integer> {
        private MMap(int amount, DimletType... dimletTypes) {
            for (DimletType type : dimletTypes) {
                put(type, amount);
            }
        }
    }
}
