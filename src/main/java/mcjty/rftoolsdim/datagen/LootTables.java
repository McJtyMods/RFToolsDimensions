package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderSetup;
import mcjty.rftoolsdim.modules.enscriber.EnscriberSetup;
import mcjty.rftoolsdim.modules.workbench.WorkbenchSetup;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(DimensionBuilderSetup.DIMENSION_BUILDER.get());
        addStandardTable(WorkbenchSetup.WORKBENCH.get());
        addStandardTable(EnscriberSetup.ENSCRIBER.get());
    }

    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
