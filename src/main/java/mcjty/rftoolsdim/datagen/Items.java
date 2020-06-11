package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.DimletSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsDim.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        parentedBlock(ProcessorSetup.PROCESSOR.get(), "block/processor");

        itemGenerated(DimletSetup.EMPTY_DIMLET.get(), "item/dimlets/empty_dimlet");
        itemGenerated(DimletSetup.EMPTY_TERRAIN_DIMLET.get(), "item/dimlets/empty_terrain_dimlet");
        itemGenerated(DimletSetup.EMPTY_FEATURE_DIMLET.get(), "item/dimlets/empty_feature_dimlet");
        itemGenerated(DimletSetup.EMPTY_BIOME_MODIFIER_DIMLET.get(), "item/dimlets/empty_biome_modifier_dimlet");
        itemGenerated(DimletSetup.TERRAIN_DIMLET.get(), "item/dimlets/terrain_dimlet");
        itemGenerated(DimletSetup.FEATURE_DIMLET.get(), "item/dimlets/feature_dimlet");
        itemGenerated(DimletSetup.BIOME_MODIFIER_DIMLET.get(), "item/dimlets/biome_modifier_dimlet");

        itemGenerated(DimletSetup.PART_ENERGY_0.get(), "item/parts/part_energy_0");
        itemGenerated(DimletSetup.PART_ENERGY_1.get(), "item/parts/part_energy_1");
        itemGenerated(DimletSetup.PART_ENERGY_2.get(), "item/parts/part_energy_2");
        itemGenerated(DimletSetup.PART_MEMORY_0.get(), "item/parts/part_memory_0");
        itemGenerated(DimletSetup.PART_MEMORY_1.get(), "item/parts/part_memory_1");
        itemGenerated(DimletSetup.PART_MEMORY_2.get(), "item/parts/part_memory_2");

        itemGenerated(DimletSetup.COMMON_ESSENCE.get(), "item/parts/common_essence");
        itemGenerated(DimletSetup.RARE_ESSENCE.get(), "item/parts/rare_essence");

    }

    @Override
    public String getName() {
        return "RFTools Dimensions Item Models";
    }
}
