package mcjty.rftoolsdim.dimension.additional;

import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;

import java.util.HashMap;
import java.util.Map;

public enum SkyDimletType {
    DEFAULT(0L, KnowledgeSet.SET1),
    END(1L, KnowledgeSet.SET1),
    INFERNAL(1L<<1, KnowledgeSet.SET2),
    BLACK(1L<<2, KnowledgeSet.SET3),
    STARS(1L<<3, KnowledgeSet.SET5),
    NOCLOUDS(1L<<4, KnowledgeSet.SET4),
    ;

    private final long mask;
    private final KnowledgeSet set;

    private static final Map<String, SkyDimletType> BY_NAME = new HashMap<>();

    static {
        for (SkyDimletType type : values()) {
            BY_NAME.put(type.name().toLowerCase(), type);
        }
    }

    SkyDimletType(long mask, KnowledgeSet set) {
        this.mask = mask;
        this.set = set;
    }

    public static String getDescription(long skyDimletTypes) {
        String buf = "";
        for (SkyDimletType value : SkyDimletType.values()) {
            if (value.match(skyDimletTypes)) {
                buf += value.name() + " ";
            }
        }
        return buf;
    }

    public long getMask() {
        return mask;
    }

    public KnowledgeSet getKnowledgeSet() {
        return set;
    }

    public boolean match(long skyMask) {
        return (skyMask & this.mask) != 0L;
    }

    public static SkyDimletType byName(String name) {
        return BY_NAME.get(name.toLowerCase());
    }
}
