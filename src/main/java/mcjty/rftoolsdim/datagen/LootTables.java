package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(DimensionBuilderModule.DIMENSION_BUILDER.get());
        addStandardTable(WorkbenchModule.WORKBENCH.get());
        addStandardTable(WorkbenchModule.HOLDER.get());
        addStandardTable(EnscriberModule.ENSCRIBER.get());
    }

    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
