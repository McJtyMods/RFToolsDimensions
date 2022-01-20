package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.noisesettings.TerrainPresets;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.HashMap;
import java.util.Map;

public enum TerrainType {
    FLAT("flat", KnowledgeSet.SET1, NoiseGeneratorSettings.OVERWORLD),
    WAVES("waves", KnowledgeSet.SET1, NoiseGeneratorSettings.OVERWORLD),
    VOID("void", KnowledgeSet.SET2, NoiseGeneratorSettings.OVERWORLD),
    NORMAL("normal", KnowledgeSet.SET3, NoiseGeneratorSettings.OVERWORLD),
    ISLANDS("islands", KnowledgeSet.SET4, TerrainPresets.RFTOOLSDIM_ISLANDS),
    CAVERN("cavern", KnowledgeSet.SET5, TerrainPresets.RFTOOLSDIM_CAVERN),
    CHAOTIC("chaotic", KnowledgeSet.SET4, TerrainPresets.RFTOOLSDIM_CHAOTIC)
    ;

    private final String name;
    private final KnowledgeSet set;
    private final ResourceKey<NoiseGeneratorSettings> noiseSettings;

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, KnowledgeSet set, ResourceKey<NoiseGeneratorSettings> noiseSettings) {
        this.name = name;
        this.set = set;
        this.noiseSettings = noiseSettings;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettings() {
        return noiseSettings;
    }

    public static TerrainType byName(String name) {
        return TERRAIN_BY_NAME.get(name.toLowerCase());
    }
}
