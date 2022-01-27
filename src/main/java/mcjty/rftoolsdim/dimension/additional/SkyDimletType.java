package mcjty.rftoolsdim.dimension.additional;

import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeSet;

import java.util.HashMap;
import java.util.Map;

public enum SkyDimletType {
    DEFAULT(0L, KnowledgeSet.SET1),
    END(1L, KnowledgeSet.SET1),

    INFERNAL(1L<<10, KnowledgeSet.SET5),
    BLACK(1L<<11, KnowledgeSet.SET5),
    STARS(1L<<12, KnowledgeSet.SET5),
    NEBULA(1L<<13, KnowledgeSet.SET5),

    NOCLOUDS(1L<<30, KnowledgeSet.SET4),

    THICKBLACKFOG(1L<<40, KnowledgeSet.SET3),
    THICKREDFOG(1L<<41, KnowledgeSet.SET3),
    THICKWHITEFOG(1L<<42, KnowledgeSet.SET3),

    BLACKFOG(1L<<45, KnowledgeSet.SET3),
    REDFOG(1L<<46, KnowledgeSet.SET3),
    WHITEFOG(1L<<47, KnowledgeSet.SET3),
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
