package mcjty.rftoolsdim.modules.dimlets.lootmodifier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootPoolEntryType;
import net.minecraft.loot.StandaloneLootEntry;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Consumer;

public class DimletLootEntry extends StandaloneLootEntry {

    private final DimletRarity rarity;

    public DimletLootEntry(int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn, DimletRarity rarity) {
        super(weightIn, qualityIn, conditionsIn, functionsIn);
        this.rarity = rarity;
    }

    private final Random random = new Random();

    @Override
    protected void createItemStack(@Nonnull Consumer<ItemStack> stackConsumer, @Nonnull LootContext context) {
        DimletKey dimlet = DimletDictionary.get().getRandomDimlet(rarity, random);
        if (dimlet != null) {
            stackConsumer.accept(DimletTools.getDimletStack(dimlet));
        }
    }

    public DimletRarity getRarity() {
        return rarity;
    }

    @Override
    @Nonnull
    public LootPoolEntryType getType() {
        return DimletModule.DIMLET_LOOT_ENTRY;
    }

    public static StandaloneLootEntry.Builder<?> builder(DimletRarity rarity) {
        return simpleBuilder((weight, quality, conditions, functions) -> new DimletLootEntry(weight, quality, conditions, functions, rarity));
    }

    public static class Serializer extends StandaloneLootEntry.Serializer<DimletLootEntry> {

        @Override
        public void serializeCustom(@Nonnull JsonObject object, @Nonnull DimletLootEntry entry, @Nonnull JsonSerializationContext conditions) {
            super.serializeCustom(object, entry, conditions);
            object.addProperty("rarity", entry.getRarity().name());
        }

        @Override
        @Nonnull
        protected DimletLootEntry deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext context, int weight, int quality, @Nonnull ILootCondition[] conditions, @Nonnull ILootFunction[] functions) {
            String rarityString = JSONUtils.getAsString(object, "rarity");
            DimletRarity rarity = DimletRarity.byName(rarityString);
            return new DimletLootEntry(weight, quality, conditions, functions, rarity);
        }
    }
}
