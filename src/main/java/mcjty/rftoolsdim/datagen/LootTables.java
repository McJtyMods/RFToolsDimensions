package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
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
        addStandardTable(EssencesModule.BLOCK_ABSORBER.get());
        addStandardTable(EssencesModule.BIOME_ABSORBER.get());

        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_COMMON.get(), DimletModule.COMMON_ESSENCE.get(), 3, 5, 0, 1);
        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_RARE.get(), DimletModule.RARE_ESSENCE.get(), 3, 5, 0, 1);
        addItemDropTable(BlobModule.DIMENSIONAL_BLOB_LEGENDARY.get(), DimletModule.LEGENDARY_ESSENCE.get(), 4, 6, 0, 1);
    }

    @Override
    public String getName() {
        return "RFTools Dimensions LootTables";
    }
}
