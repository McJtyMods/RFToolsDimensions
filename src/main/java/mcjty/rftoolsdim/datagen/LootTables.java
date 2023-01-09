package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.lootmodifier.DimletLootEntry;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nonnull;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        LootPool.Builder builder = LootPool.lootPool()
                .name(DimensionRegistry.HUT_LOOT.getPath())
                .setRolls(UniformGenerator.between(1, 5))
                .add(DimletLootEntry.builder(DimletRarity.COMMON)
                        .setWeight(14)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 2))))
                .add(DimletLootEntry.builder(DimletRarity.UNCOMMON)
                        .setWeight(4)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 2))))
                .add(DimletLootEntry.builder(DimletRarity.RARE)
                        .setWeight(2)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 1))))
                .add(DimletLootEntry.builder(DimletRarity.LEGENDARY)
                        .setWeight(1)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 1))))
                .add(LootItem.lootTableItem(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get())
                        .setWeight(14)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 3))))
                .add(LootItem.lootTableItem(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get())
                        .setWeight(4)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 3))))
                .add(LootItem.lootTableItem(KnowledgeModule.RARE_LOST_KNOWLEDGE.get())
                        .setWeight(2)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 2))))
                .add(LootItem.lootTableItem(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get())
                        .setWeight(1)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(0, 1))))
                ;

        addChestLootTable(DimensionRegistry.HUT_LOOT, LootTable.lootTable().withPool(builder));
    }

    @Nonnull
    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
