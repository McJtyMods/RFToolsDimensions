package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public enum TimeType {
    NORMAL("normal", KnowledgeSet.SET1, DimensionRegistry.NORMAL_TIME_ID),
    DAY("day", KnowledgeSet.SET2, DimensionRegistry.FIXED_DAY_ID),
    NIGHT("night", KnowledgeSet.SET2, DimensionRegistry.FIXED_NIGHT_ID);

    private final String name;
    private final KnowledgeSet set;
    private final ResourceLocation dimensionType;

    private static final Map<String, TimeType> TYPE_BY_NAME = new HashMap<>();

    static {
        for (TimeType type : values()) {
            TYPE_BY_NAME.put(type.getName(), type);
        }
    }

    TimeType(String name, KnowledgeSet set, ResourceLocation dimensionType) {
        this.name = name;
        this.set = set;
        this.dimensionType = dimensionType;
    }

    public String getName() {
        return name;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public ResourceLocation getDimensionType() {
        return dimensionType;
    }

    public static TimeType byName(String name) {
        return TYPE_BY_NAME.get(name.toLowerCase());
    }
}
