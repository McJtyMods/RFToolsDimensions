package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
//        addStandardTable(ProcessorSetup.PROCESSOR.get());
    }

    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
