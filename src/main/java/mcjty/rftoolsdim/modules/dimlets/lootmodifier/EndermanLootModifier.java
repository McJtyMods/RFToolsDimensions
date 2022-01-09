package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.google.gson.JsonObject;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class EndermanLootModifier extends LootModifier {

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

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        Random random = context.getRandom();

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

    private void spawnKnowledge(List<ItemStack> generatedLoot, LootContext context, Random random, float chance, DimletRarity rarity) {
        if (random.nextFloat() < chance) {
            ItemStack stack = LostKnowledgeItem.createUnresearchedLostKnowledge(rarity);
            if (!stack.isEmpty()) {
                generatedLoot.add(stack);
            }
        }
    }

    private void spawnDimlet(List<ItemStack> generatedLoot, LootContext context, Random random, float chance, DimletRarity rarity) {
        if (random.nextFloat() < chance) {
            DimletKey key = DimletDictionary.get().getRandomDimlet(rarity, random);
            if (key != null) {
                generatedLoot.add(DimletTools.getDimletStack(key));
            }
        }
    }

    public static class Serializer extends GlobalLootModifierSerializer<EndermanLootModifier> {

        @Override
        public EndermanLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            float commonKnowledgeChance = GsonHelper.getAsFloat(object, "commonKnowledgeChance");
            float uncommonKnowledgeChance = GsonHelper.getAsFloat(object, "uncommonKnowledgeChance");
            float rareKnowledgeChance = GsonHelper.getAsFloat(object, "rareKnowledgeChance");
            float legendaryKnowledgeChance = GsonHelper.getAsFloat(object, "legendaryKnowledgeChance");
            float commonDimletChance = GsonHelper.getAsFloat(object, "commonDimletChance");
            float uncommonDimletChance = GsonHelper.getAsFloat(object, "uncommonDimletChance");
            float rareDimletChance = GsonHelper.getAsFloat(object, "rareDimletChance");
            float legendaryDimletChance = GsonHelper.getAsFloat(object, "legendaryDimletChance");
            return new EndermanLootModifier(conditionsIn,
                    commonKnowledgeChance, uncommonKnowledgeChance, rareKnowledgeChance, legendaryKnowledgeChance,
                    commonDimletChance, uncommonDimletChance, rareDimletChance, legendaryDimletChance);
        }

        @Override
        public JsonObject write(EndermanLootModifier instance) {
            JsonObject object = makeConditions(instance.conditions);
            object.addProperty("commonKnowledgeChance", instance.getCommonKnowledgeChance());
            object.addProperty("uncommonKnowledgeChance", instance.getUncommonKnowledgeChance());
            object.addProperty("rareKnowledgeChance", instance.getRareKnowledgeChance());
            object.addProperty("legendaryKnowledgeChance", instance.getLegendaryKnowledgeChance());
            object.addProperty("commonDimletChance", instance.getCommonDimletChance());
            object.addProperty("uncommonDimletChance", instance.getUncommonDimletChance());
            object.addProperty("rareDimletChance", instance.getRareDimletChance());
            object.addProperty("legendaryDimletChance", instance.getLegendaryDimletChance());
            return object;
        }
    }
}
