package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.noisesettings.TerrainPresets;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.HashMap;
import java.util.Map;

public enum TerrainType {
    FLAT("flat", KnowledgeSet.SET1, NoiseGeneratorSettings.OVERWORLD, false),
    WAVES("waves", KnowledgeSet.SET1, NoiseGeneratorSettings.OVERWORLD, false),
    VOID("void", KnowledgeSet.SET2, NoiseGeneratorSettings.OVERWORLD, true),
    NORMAL("normal", KnowledgeSet.SET3, NoiseGeneratorSettings.OVERWORLD, false),
    ISLANDS("islands", KnowledgeSet.SET4, TerrainPresets.RFTOOLSDIM_ISLANDS, true),
    CAVERN("cavern", KnowledgeSet.SET5, TerrainPresets.RFTOOLSDIM_CAVERN, false),
    CHAOTIC("chaotic", KnowledgeSet.SET4, TerrainPresets.RFTOOLSDIM_CHAOTIC, false),
    GRID("grid", KnowledgeSet.SET2, NoiseGeneratorSettings.OVERWORLD, true),
    PLATFORMS("platforms", KnowledgeSet.SET2, NoiseGeneratorSettings.OVERWORLD, false),
    SPIKES("spikes", KnowledgeSet.SET4, NoiseGeneratorSettings.OVERWORLD, false)
    ;

    private final String name;
    private final KnowledgeSet set;
    private final ResourceKey<NoiseGeneratorSettings> noiseSettings;
    private final boolean voidLike;         // World without bedrock layer

    private static final Map<String, TerrainType> TERRAIN_BY_NAME = new HashMap<>();

    static {
        for (TerrainType type : values()) {
            TERRAIN_BY_NAME.put(type.getName(), type);
        }
    }

    TerrainType(String name, KnowledgeSet set, ResourceKey<NoiseGeneratorSettings> noiseSettings, boolean voidLike) {
        this.name = name;
        this.set = set;
        this.noiseSettings = noiseSettings;
        this.voidLike = voidLike;
    }

    public String getName() {
        return name;
    }

    public boolean isVoidLike() {
        return voidLike;
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
