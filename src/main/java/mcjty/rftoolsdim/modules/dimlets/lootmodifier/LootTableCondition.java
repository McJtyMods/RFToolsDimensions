package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.google.gson.*;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Set;

public class LootTableCondition implements ILootCondition {

    public LootTableCondition(Set<ResourceLocation> tables) {
    }

    @Override
    public LootConditionType func_230419_b_() {
        return DimletModule.LOOT_TABLE_CONDITION;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Vector3d pos = lootContext.get(LootParameters.field_237457_g_);
        if (pos != null) {
            BlockState state = lootContext.getWorld().getBlockState(new BlockPos(pos));
            if (state.getBlock() instanceof ChestBlock) {
                // @todo correct loottable (supported in 1.16.5?)
                return true;
            }
//            ResourceLocation lootTable = state.getBlock().getLootTable();
//            return tables.contains(lootTable);
        }
        return false;
    }

    public static class Serializer implements ILootSerializer<LootTableCondition> {

        private final Set<ResourceLocation> tables = new HashSet<>();

        @Override
        public void serialize(JsonObject object, LootTableCondition condition, JsonSerializationContext context) {

        }

        @Override
        public LootTableCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            JsonArray tablesArray = object.getAsJsonArray("tables");
            tables.clear();
            for (JsonElement element : tablesArray) {
                tables.add(new ResourceLocation(element.getAsString()));
            }

            return new LootTableCondition(tables);
        }
    }
}
