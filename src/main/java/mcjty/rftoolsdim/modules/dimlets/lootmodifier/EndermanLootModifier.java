package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.google.gson.JsonObject;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class EndermanLootModifier extends LootModifier {

    public EndermanLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        Random random = context.getRandom();
        if (random.nextFloat() < .5f) {
            generatedLoot.add(new ItemStack(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get()));
        }

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<EndermanLootModifier> {

        @Override
        public EndermanLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new EndermanLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(EndermanLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
