package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsDim.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(DimensionBuilderModule.DIMENSION_BUILDER.get(), "block/dimension_builder");
        parentedBlock(WorkbenchModule.WORKBENCH.get(), "block/dimlet_workbench");
        parentedBlock(EnscriberModule.ENSCRIBER.get(), "block/enscriber");

        itemGenerated(DimletModule.EMPTY_DIMLET.get(), "item/dimlets/empty_dimlet");
        itemGenerated(DimletModule.EMPTY_TERRAIN_DIMLET.get(), "item/dimlets/empty_terrain_dimlet");
        itemGenerated(DimletModule.EMPTY_FEATURE_DIMLET.get(), "item/dimlets/empty_feature_dimlet");
        itemGenerated(DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get(), "item/dimlets/empty_biome_controller_dimlet");
        itemGenerated(DimletModule.EMPTY_BLOCK_DIMLET.get(), "item/dimlets/empty_block_dimlet");
        itemGenerated(DimletModule.TERRAIN_DIMLET.get(), "item/dimlets/terrain_dimlet");
        itemGenerated(DimletModule.FEATURE_DIMLET.get(), "item/dimlets/feature_dimlet");
        itemGenerated(DimletModule.BIOME_CONTROLLER_DIMLET.get(), "item/dimlets/biome_controller_dimlet");
        itemGenerated(DimletModule.BLOCK_DIMLET.get(), "item/dimlets/block_dimlet");

        itemGenerated(DimletModule.PART_ENERGY_0.get(), "item/parts/part_energy_0");
        itemGenerated(DimletModule.PART_ENERGY_1.get(), "item/parts/part_energy_1");
        itemGenerated(DimletModule.PART_ENERGY_2.get(), "item/parts/part_energy_2");
        itemGenerated(DimletModule.PART_ENERGY_3.get(), "item/parts/part_energy_3");
        itemGenerated(DimletModule.PART_MEMORY_0.get(), "item/parts/part_memory_0");
        itemGenerated(DimletModule.PART_MEMORY_1.get(), "item/parts/part_memory_1");
        itemGenerated(DimletModule.PART_MEMORY_2.get(), "item/parts/part_memory_2");
        itemGenerated(DimletModule.PART_MEMORY_3.get(), "item/parts/part_memory_3");

        itemGenerated(DimletModule.COMMON_ESSENCE.get(), "item/parts/common_essence");
        itemGenerated(DimletModule.RARE_ESSENCE.get(), "item/parts/rare_essence");
        itemGenerated(DimletModule.LEGENDARY_ESSENCE.get(), "item/parts/legendary_essence");

        itemGenerated(KnowledgeModule.COMMON_LOST_KNOWLEDGE.get(), "item/common_lost_knowledge");
        itemGenerated(KnowledgeModule.UNCOMMON_LOST_KNOWLEDGE.get(), "item/uncommon_lost_knowledge");
        itemGenerated(KnowledgeModule.RARE_LOST_KNOWLEDGE.get(), "item/rare_lost_knowledge");
        itemGenerated(KnowledgeModule.LEGENDARY_LOST_KNOWLEDGE.get(), "item/legendary_lost_knowledge");
        itemGenerated(KnowledgeModule.PATTERN_RECIPE_TABLET.get(), "item/pattern_recipe_tablet");

    }

    @Override
    public String getName() {
        return "RFTools Dimensions Item Models";
    }
}
