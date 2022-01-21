package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;

public record KnowledgeKey(DimletType type,
                           DimletRarity rarity,
                           KnowledgeSet set) {

    public static KnowledgeKey create(String data) {
        String[] split = data.split(":");
        return new KnowledgeKey(DimletType.byName(split[0]), DimletRarity.byName(split[1]), KnowledgeSet.valueOf(split[2]));
    }

    public String serialize() {
        return type.getShortName() + ":" + rarity.getShortName() + ":" + set.name();
    }
}
