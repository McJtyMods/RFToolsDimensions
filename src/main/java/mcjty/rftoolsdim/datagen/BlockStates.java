package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderSetup;
import mcjty.rftoolsdim.modules.enscriber.EnscriberSetup;
import mcjty.rftoolsdim.modules.workbench.WorkbenchSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsDim.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        orientedBlock(DimensionBuilderSetup.DIMENSION_BUILDER.get(), frontBasedModel("dimension_builder", modLoc("block/dimensionbuilder")));
        orientedBlock(WorkbenchSetup.WORKBENCH.get(), frontBasedModel("dimlet_workbench", modLoc("block/dimletworkbenchtop"))); // @todo 1.15 fix
        orientedBlock(EnscriberSetup.ENSCRIBER.get(), frontBasedModel("enscriber", modLoc("block/dimensionenscriber")));
    }
}
