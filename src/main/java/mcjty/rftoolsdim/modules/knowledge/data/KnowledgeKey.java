package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;

import java.util.Objects;

public class KnowledgeKey {
    private final DimletType type;
    private final DimletRarity rarity;
    private final KnowledgeSet set;

    public KnowledgeKey(DimletType type, DimletRarity rarity, KnowledgeSet set) {
        this.type = type;
        this.rarity = rarity;
        this.set = set;
    }

    public KnowledgeKey(String data) {
        String[] split = data.split(":");
        type = DimletType.byName(split[0]);
        rarity = DimletRarity.byName(split[1]);
        set = KnowledgeSet.valueOf(split[2]);
    }

    public DimletType getType() {
        return type;
    }

    public DimletRarity getRarity() {
        return rarity;
    }

    public KnowledgeSet getSet() {
        return set;
    }

    public String serialize() {
        return type.getShortName() + ":" + rarity.getShortName() + ":" + set.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeKey that = (KnowledgeKey) o;
        return type == that.type &&
                rarity == that.rarity &&
                set == that.set;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, rarity, set);
    }
}
