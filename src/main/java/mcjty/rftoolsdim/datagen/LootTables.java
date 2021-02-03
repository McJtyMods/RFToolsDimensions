package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.DimletLootEntry;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(DimensionBuilderModule.DIMENSION_BUILDER.get());
        addStandardTable(WorkbenchModule.WORKBENCH.get());
        addStandardTable(WorkbenchModule.HOLDER.get());
        addStandardTable(EnscriberModule.ENSCRIBER.get());
        addStandardTable(EssencesModule.BLOCK_ABSORBER.get());
        addStandardTable(EssencesModule.BIOME_ABSORBER.get());

        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_COMMON.get(), DimletModule.COMMON_ESSENCE.get(), 3, 5, 0, 1);
        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_RARE.get(), DimletModule.RARE_ESSENCE.get(), 3, 5, 0, 1);
        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_LEGENDARY.get(), DimletModule.LEGENDARY_ESSENCE.get(), 4, 6, 0, 1);

        LootPool.Builder builder = LootPool.builder()
                .name(DimensionRegistry.HUT_LOOT.getPath())
                .rolls(RandomValueRange.of(1, 5))
                .addEntry(DimletLootEntry.builder(DimletRarity.COMMON)
                        .weight(14)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 2))))
                .addEntry(DimletLootEntry.builder(DimletRarity.UNCOMMON)
                        .weight(4)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 2))))
                .addEntry(DimletLootEntry.builder(DimletRarity.RARE)
                        .weight(2)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 1))))
                .addEntry(DimletLootEntry.builder(DimletRarity.LEGENDARY)
                        .weight(1)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 1))))
                .addEntry(ItemLootEntry.builder(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())
                        .weight(14)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 3))))
                .addEntry(ItemLootEntry.builder(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .weight(4)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 3))))
                .addEntry(ItemLootEntry.builder(KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .weight(2)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 2))))
                .addEntry(ItemLootEntry.builder(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get())
                        .weight(1)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(0, 1))))
                ;

        addChestLootTable(DimensionRegistry.HUT_LOOT, LootTable.builder().addLootPool(builder));
    }

    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
