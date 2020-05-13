package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsDim.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        parentedBlock(ProcessorSetup.PROCESSOR.get(), "block/processor");

//        itemGenerated(VariousSetup.PROGRAM_CARD.get(), "item/programcard");
    }

    @Override
    public String getName() {
        return "RFTools Dimensions Item Models";
    }
}
