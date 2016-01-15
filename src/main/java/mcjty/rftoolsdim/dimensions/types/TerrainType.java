package mcjty.rftoolsdim.dimensions.types;

import java.util.HashMap;
import java.util.Map;

public enum TerrainType {
    TERRAIN_VOID("Void", true, true, 0.3f, 0.3f),
    TERRAIN_FLAT("Flat", false, true, 1, 0.8f),
    TERRAIN_AMPLIFIED("Amplified", false, true, 1.5f, 1),
    TERRAIN_NORMAL("Normal", false, true, 1, 1),
    TERRAIN_ISLAND("Island", true, true, 0.5f, 0.5f),
    TERRAIN_ISLANDS("Islands", true, true, 0.5f, 0.5f),
    TERRAIN_CHAOTIC("Chaotic", true, true, 0.5f, 0.5f),
    TERRAIN_PLATEAUS("Plateaus", true, true, 0.5f, 0.5f),
    TERRAIN_GRID("Grid", true, true, 0.4f, 0.3f),
    TERRAIN_CAVERN("Cavern", true, false, 1, 1),
    TERRAIN_LOW_CAVERN("LowCavern", true, true, 1, 1),
    TERRAIN_FLOODED_CAVERN("FloodedCavern", true, true, 1, 1.5f),
    TERRAIN_NEARLANDS("NearLands", false, true, 1, 1),
    TERRAIN_LIQUID("Liquid", true, true, 0.3f, 2),
    TERRAIN_SOLID("Solid", false, true, 2, 0.6f),
    TERRAIN_WAVES("Waves", false, true, 1, 0.6f),
    TERRAIN_FILLEDWAVES("FilledWaves", false, true, 1, 1.2f),
    TERRAIN_ROUGH("Rough", false, true, 1, 0.6f);

    private static final Map<String,TerrainType> TERRAIN_TYPE_MAP = new HashMap<>();

    static {
        for (TerrainType type : TerrainType.values()) {
            TERRAIN_TYPE_MAP.put(type.getId(), type);
        }
    }

    private final String id;
    private final boolean noHorizon;
    private final boolean sky;
    private final float materialCostFactor;
    private final float liquidCostFactor;

    TerrainType(String id, boolean noHorizon, boolean sky, float materialCostFactor, float liquidCostFactor) {
        this.id = id;
        this.noHorizon = noHorizon;
        this.sky = sky;
        this.materialCostFactor = materialCostFactor;
        this.liquidCostFactor = liquidCostFactor;
    }

    public String getId() {
        return id;
    }

    public static TerrainType getTerrainById(String id) {
        return TERRAIN_TYPE_MAP.get(id);
    }

    public boolean hasNoHorizon() {
        return noHorizon;
    }

    public boolean hasSky() {
        return sky;
    }

    public float getLiquidCostFactor() {
        return liquidCostFactor;
    }

    public float getMaterialCostFactor() {
        return materialCostFactor;
    }
}
