package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.DimletLootEntry;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.RandomValueRange;
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
        addStandardTable(WorkbenchModule.RESEARCHER.get());
        addStandardTable(EnscriberModule.ENSCRIBER.get());
        addStandardTable(EssencesModule.BLOCK_ABSORBER.get());
        addStandardTable(EssencesModule.FLUID_ABSORBER.get());
        addStandardTable(EssencesModule.BIOME_ABSORBER.get());

        addSimpleTable(DecorativeModule.DIMENSIONAL_SMALL_BLOCK.get());
        addSimpleTable(DecorativeModule.DIMENSIONAL_BLANK.get());
        addSimpleTable(DecorativeModule.DIMENSIONAL_BLOCK.get());
        addSimpleTable(DecorativeModule.DIMENSIONAL_PATTERN1_BLOCK.get());
        addSimpleTable(DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK.get());
        addSimpleTable(DecorativeModule.DIMENSIONAL_CROSS_BLOCK.get());
        addSimpleTable(DecorativeModule.DIMENSIONAL_CROSS2_BLOCK.get());

        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_COMMON.get(), DimletModule.COMMON_ESSENCE.get(), 3, 5, 0, 1);
        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_RARE.get(), DimletModule.RARE_ESSENCE.get(), 3, 5, 0, 1);
        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_LEGENDARY.get(), DimletModule.LEGENDARY_ESSENCE.get(), 4, 6, 0, 1);

        LootPool.Builder builder = LootPool.lootPool()
                .name(DimensionRegistry.HUT_LOOT.getPath())
                .setRolls(RandomValueRange.between(1, 5))
                .add(DimletLootEntry.builder(DimletRarity.COMMON)
                        .setWeight(14)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 2))))
                .add(DimletLootEntry.builder(DimletRarity.UNCOMMON)
                        .setWeight(4)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 2))))
                .add(DimletLootEntry.builder(DimletRarity.RARE)
                        .setWeight(2)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 1))))
                .add(DimletLootEntry.builder(DimletRarity.LEGENDARY)
                        .setWeight(1)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 1))))
                .add(ItemLootEntry.lootTableItem(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())
                        .setWeight(14)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 3))))
                .add(ItemLootEntry.lootTableItem(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .setWeight(4)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 3))))
                .add(ItemLootEntry.lootTableItem(KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .setWeight(2)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 2))))
                .add(ItemLootEntry.lootTableItem(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get())
                        .setWeight(1)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(0, 1))))
                ;

        addChestLootTable(DimensionRegistry.HUT_LOOT, LootTable.lootTable().withPool(builder));
    }

    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
