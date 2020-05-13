package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsDim.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        orientedBlock(ProcessorSetup.PROCESSOR.get(), frontBasedModel("processor", modLoc("block/machineprocessoron")));
    }
}
