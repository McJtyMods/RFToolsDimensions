package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EndermanLootModifier extends LootModifier {

    // @todo better but doesn't work for some reason
//    public static final Codec<EndermanLootModifier> CODEC = RecordCodecOBuilder.create(instance -> codecStart(instance).and(
//            instance.group(
//                    Codec.FLOAT.fieldOf("commonKnowledgeChance").forGetter(l -> l.commonKnowledgeChance),
//                    Codec.FLOAT.fieldOf("uncommonKnowledgeChance").forGetter(l -> l.uncommonKnowledgeChance),
//                    Codec.FLOAT.fieldOf("rareKnowledgeChance").forGetter(l -> l.rareKnowledgeChance),
//                    Codec.FLOAT.fieldOf("legendaryKnowledgeChance").forGetter(l -> l.legendaryKnowledgeChance),
//                    Codec.FLOAT.fieldOf("commonDimletChance").forGetter(l -> l.commonDimletChance),
//                    Codec.FLOAT.fieldOf("uncommonDimletChance").forGetter(l -> l.uncommonDimletChance),
//                    Codec.FLOAT.fieldOf("rareDimletChance").forGetter(l -> l.rareDimletChance),
//                    Codec.FLOAT.fieldOf("legendaryDimletChance").forGetter(l -> l.legendaryDimletChance))
//    ).apply(instance, EndermanLootModifier::new));

    public static final Codec<EndermanLootModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(l -> l.conditions),
            Codec.FLOAT.fieldOf("commonKnowledgeChance").forGetter(l -> l.commonKnowledgeChance),
            Codec.FLOAT.fieldOf("uncommonKnowledgeChance").forGetter(l -> l.uncommonKnowledgeChance),
            Codec.FLOAT.fieldOf("rareKnowledgeChance").forGetter(l -> l.rareKnowledgeChance),
            Codec.FLOAT.fieldOf("legendaryKnowledgeChance").forGetter(l -> l.legendaryKnowledgeChance),
            Codec.FLOAT.fieldOf("commonDimletChance").forGetter(l -> l.commonDimletChance),
            Codec.FLOAT.fieldOf("uncommonDimletChance").forGetter(l -> l.uncommonDimletChance),
            Codec.FLOAT.fieldOf("rareDimletChance").forGetter(l -> l.rareDimletChance),
            Codec.FLOAT.fieldOf("legendaryDimletChance").forGetter(l -> l.legendaryDimletChance)
    ).apply(instance, EndermanLootModifier::new));


    private final float commonKnowledgeChance;
    private final float uncommonKnowledgeChance;
    private final float rareKnowledgeChance;
    private final float legendaryKnowledgeChance;
    private final float commonDimletChance;
    private final float uncommonDimletChance;
    private final float rareDimletChance;
    private final float legendaryDimletChance;

    public EndermanLootModifier(LootItemCondition[] conditionsIn,
                                float commonKnowledgeChance, float uncommonKnowledgeChance, float rareKnowledgeChance, float legendaryKnowledgeChance,
                                float commonDimletChance, float uncommonDimletChance, float rareDimletChance, float legendaryDimletChance) {
        super(conditionsIn);
        this.commonKnowledgeChance = commonKnowledgeChance;
        this.uncommonKnowledgeChance = uncommonKnowledgeChance;
        this.rareKnowledgeChance = rareKnowledgeChance;
        this.legendaryKnowledgeChance = legendaryKnowledgeChance;
        this.commonDimletChance = commonDimletChance;
        this.uncommonDimletChance = uncommonDimletChance;
        this.rareDimletChance = rareDimletChance;
        this.legendaryDimletChance = legendaryDimletChance;
    }

    public float getCommonKnowledgeChance() {
        return commonKnowledgeChance;
    }

    public float getUncommonKnowledgeChance() {
        return uncommonKnowledgeChance;
    }

    public float getRareKnowledgeChance() {
        return rareKnowledgeChance;
    }

    public float getLegendaryKnowledgeChance() {
        return legendaryKnowledgeChance;
    }

    public float getCommonDimletChance() {
        return commonDimletChance;
    }

    public float getUncommonDimletChance() {
        return uncommonDimletChance;
    }

    public float getRareDimletChance() {
        return rareDimletChance;
    }

    public float getLegendaryDimletChance() {
        return legendaryDimletChance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        RandomSource random = context.getRandom();

        spawnKnowledge(generatedLoot, context, random, commonKnowledgeChance, DimletRarity.COMMON);
        spawnKnowledge(generatedLoot, context, random, uncommonKnowledgeChance, DimletRarity.UNCOMMON);
        spawnKnowledge(generatedLoot, context, random, rareKnowledgeChance, DimletRarity.RARE);
        spawnKnowledge(generatedLoot, context, random, legendaryKnowledgeChance, DimletRarity.LEGENDARY);
        spawnDimlet(generatedLoot, context, random, commonDimletChance, DimletRarity.COMMON);
        spawnDimlet(generatedLoot, context, random, uncommonDimletChance, DimletRarity.UNCOMMON);
        spawnDimlet(generatedLoot, context, random, rareDimletChance, DimletRarity.RARE);
        spawnDimlet(generatedLoot, context, random, legendaryDimletChance, DimletRarity.LEGENDARY);

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    private void spawnKnowledge(List<ItemStack> generatedLoot, LootContext context, RandomSource random, float chance, DimletRarity rarity) {
        if (random.nextFloat() < chance) {
            ItemStack stack = LostKnowledgeItem.createUnresearchedLostKnowledge(rarity);
            if (!stack.isEmpty()) {
                generatedLoot.add(stack);
            }
        }
    }

    private void spawnDimlet(List<ItemStack> generatedLoot, LootContext context, RandomSource random, float chance, DimletRarity rarity) {
        if (random.nextFloat() < chance) {
            DimletKey key = DimletDictionary.get().getRandomDimlet(rarity, random);
            if (key != null) {
                generatedLoot.add(DimletTools.getDimletStack(key));
            }
        }
    }
}
