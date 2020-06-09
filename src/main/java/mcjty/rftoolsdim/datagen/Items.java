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
        itemGenerated(DimletSetup.TERRAIN_DIMLET.get(), "item/dimlets/terrain_dimlet");
        itemGenerated(DimletSetup.FEATURE_DIMLET.get(), "item/dimlets/feature_dimlet");
        itemGenerated(DimletSetup.BIOME_MODIFIER_DIMLET.get(), "item/dimlets/biome_modifier_dimlet");
    }

    @Override
    public String getName() {
        return "RFTools Dimensions Item Models";
    }
}
