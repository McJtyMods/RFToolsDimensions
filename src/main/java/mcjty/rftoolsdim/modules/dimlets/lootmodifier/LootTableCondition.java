package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.List;
import java.util.Set;

public record LootTableCondition(Set<ResourceLocation> tables) implements LootItemCondition {

    public static final MapCodec<LootTableCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ResourceLocation.CODEC.listOf().fieldOf("tables").forGetter(condition -> List.copyOf(condition.tables)))
                    .apply(instance, list -> new LootTableCondition(ImmutableSet.copyOf(list)))
    );

    @Override
    public LootItemConditionType getType() {
        return DimletModule.LOOT_TABLE_CONDITION.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        ResourceLocation table = lootContext.getQueriedLootTableId();
        return tables.contains(table);
    }
}
