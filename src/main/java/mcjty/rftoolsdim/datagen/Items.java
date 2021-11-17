package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsDim.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(DimensionBuilderModule.DIMENSION_BUILDER.get(), "block/dimension_builder");
        parentedBlock(WorkbenchModule.WORKBENCH.get(), "block/dimlet_workbench");
        parentedBlock(WorkbenchModule.HOLDER.get(), "block/knowledge_holder");
        parentedBlock(WorkbenchModule.RESEARCHER.get(), "block/researcher");
        parentedBlock(EnscriberModule.ENSCRIBER.get(), "block/enscriber");
        parentedBlock(EssencesModule.BLOCK_ABSORBER.get(), "block/block_absorber");
        parentedBlock(EssencesModule.FLUID_ABSORBER.get(), "block/fluid_absorber");
        parentedBlock(EssencesModule.BIOME_ABSORBER.get(), "block/biome_absorber");

        parentedBlock(DecorativeModule.DIMENSIONAL_BLOCK.get(), "block/dimensional_block");
        parentedBlock(DecorativeModule.DIMENSIONAL_BLANK.get(), "block/dimensional_blank_block");
        parentedBlock(DecorativeModule.DIMENSIONAL_CROSS_BLOCK.get(), "block/dimensional_cross_block");
        parentedBlock(DecorativeModule.DIMENSIONAL_CROSS2_BLOCK.get(), "block/dimensional_cross2_block");
        parentedBlock(DecorativeModule.DIMENSIONAL_PATTERN1_BLOCK.get(), "block/dimensional_pattern1_block");
        parentedBlock(DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK.get(), "block/dimensional_pattern2_block");
        parentedBlock(DecorativeModule.DIMENSIONAL_SMALL_BLOCK.get(), "block/dimensional_small_blocks");

        itemGenerated(DimletModule.EMPTY_DIMLET.get(), "item/dimlets/empty_dimlet");
        itemGenerated(DimletModule.EMPTY_TERRAIN_DIMLET.get(), "item/dimlets/empty_terrain_dimlet");
        itemGenerated(DimletModule.EMPTY_ATTRIBUTE_DIMLET.get(), "item/dimlets/empty_attribute_dimlet");
        itemGenerated(DimletModule.EMPTY_FEATURE_DIMLET.get(), "item/dimlets/empty_feature_dimlet");
        itemGenerated(DimletModule.EMPTY_BIOME_DIMLET.get(), "item/dimlets/empty_biome_dimlet");
        itemGenerated(DimletModule.EMPTY_BIOME_CONTROLLER_DIMLET.get(), "item/dimlets/empty_biome_controller_dimlet");
        itemGenerated(DimletModule.EMPTY_BLOCK_DIMLET.get(), "item/dimlets/empty_block_dimlet");
        itemGenerated(DimletModule.EMPTY_FLUID_DIMLET.get(), "item/dimlets/empty_fluid_dimlet");
        itemGenerated(DimletModule.EMPTY_TIME_DIMLET.get(), "item/dimlets/empty_time_dimlet");

        itemGenerated(DimletModule.TERRAIN_DIMLET.get(), "item/dimlets/terrain_dimlet");
        itemGenerated(DimletModule.ATTRIBUTE_DIMLET.get(), "item/dimlets/attribute_dimlet");
        itemGenerated(DimletModule.FEATURE_DIMLET.get(), "item/dimlets/feature_dimlet");
        itemGenerated(DimletModule.BIOME_DIMLET.get(), "item/dimlets/biome_dimlet");
        itemGenerated(DimletModule.BIOME_CONTROLLER_DIMLET.get(), "item/dimlets/biome_controller_dimlet");
        itemGenerated(DimletModule.BLOCK_DIMLET.get(), "item/dimlets/block_dimlet");
        itemGenerated(DimletModule.FLUID_DIMLET.get(), "item/dimlets/fluid_dimlet");
        itemGenerated(DimletModule.TIME_DIMLET.get(), "item/dimlets/time_dimlet");
        itemGenerated(DimletModule.DIGIT_DIMLET.get(), "item/dimlets/digit_dimlet");
        itemGenerated(DimletModule.ADMIN_DIMLET.get(), "item/dimlets/admin_dimlet");

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

        itemGenerated(DimensionBuilderModule.EMPTY_DIMENSION_TAB.get(), "item/empty_dimension_tab");
        itemGenerated(DimensionBuilderModule.REALIZED_DIMENSION_TAB.get(), "item/realized_dimension_tab");

        ResourceLocation powerId = new ResourceLocation(RFToolsDim.MODID, "power");

        getBuilder(DimensionBuilderModule.DIMENSION_MONITOR.get().getRegistryName().getPath())
                .parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/monitoritem0")
                .override().predicate(powerId, 0).model(createMonitorModel(0)).end()
                .override().predicate(powerId, 1).model(createMonitorModel(1)).end()
                .override().predicate(powerId, 2).model(createMonitorModel(2)).end()
                .override().predicate(powerId, 3).model(createMonitorModel(3)).end()
                .override().predicate(powerId, 4).model(createMonitorModel(4)).end()
                .override().predicate(powerId, 5).model(createMonitorModel(5)).end()
                .override().predicate(powerId, 6).model(createMonitorModel(6)).end()
                .override().predicate(powerId, 7).model(createMonitorModel(7)).end()
                .override().predicate(powerId, 8).model(createMonitorModel(8)).end()
        ;
        getBuilder(DimensionBuilderModule.PHASED_FIELD_GENERATOR.get().getRegistryName().getPath())
                .parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/pfg/phasedfieldgeneratoriteml0")
                .override().predicate(powerId, 0).model(createPFGModel(0)).end()
                .override().predicate(powerId, 1).model(createPFGModel(1)).end()
                .override().predicate(powerId, 2).model(createPFGModel(2)).end()
                .override().predicate(powerId, 3).model(createPFGModel(3)).end()
                .override().predicate(powerId, 4).model(createPFGModel(4)).end()
                .override().predicate(powerId, 5).model(createPFGModel(5)).end()
                .override().predicate(powerId, 6).model(createPFGModel(6)).end()
                .override().predicate(powerId, 7).model(createPFGModel(7)).end()
                .override().predicate(powerId, 8).model(createPFGModel(8)).end()
        ;
    }

    private ItemModelBuilder createMonitorModel(int suffix) {
        return getBuilder("monitoritem" + suffix).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/monitoritem" + suffix);
    }

    private ItemModelBuilder createPFGModel(int suffix) {
        return getBuilder("phasedfieldgenerator" + suffix).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/pfg/phasedfieldgeneratoriteml" + suffix);
    }

    @Nonnull
    @Override
    public String getName() {
        return "RFTools Dimensions Item Models";
    }
}
