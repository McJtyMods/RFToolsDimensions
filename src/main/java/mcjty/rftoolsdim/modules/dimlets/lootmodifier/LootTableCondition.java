package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.google.gson.*;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class LootTableCondition implements ILootCondition {

    private final Set<ResourceLocation> tables;

    public LootTableCondition(Set<ResourceLocation> tables) {
        this.tables = tables;
    }

    @Nonnull
    @Override
    public LootConditionType getType() {
        return DimletModule.LOOT_TABLE_CONDITION;
    }

    @Override
    public boolean test(LootContext lootContext) {
        ResourceLocation table = lootContext.getQueriedLootTableId();
        return tables.contains(table);
    }

    public static class Serializer implements ILootSerializer<LootTableCondition> {


        @Override
        public void serialize(@Nonnull JsonObject object, LootTableCondition condition, @Nonnull JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            for (ResourceLocation table : condition.tables) {
                array.add(table.toString());
            }
            object.add("tables", array);
        }

        @Nonnull
        @Override
        public LootTableCondition deserialize(JsonObject object, @Nonnull JsonDeserializationContext context) {
            Set<ResourceLocation> tables = new HashSet<>();
            JsonArray tablesArray = object.getAsJsonArray("tables");
            tables.clear();
            for (JsonElement element : tablesArray) {
                tables.add(new ResourceLocation(element.getAsString()));
            }

            return new LootTableCondition(tables);
        }
    }
}
