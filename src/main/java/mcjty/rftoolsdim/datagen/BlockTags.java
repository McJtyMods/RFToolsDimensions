package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseBlockTagsProvider;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BlockTags extends BaseBlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RFToolsDim.MODID, helper);
    }

    @Override
    protected void addTags() {
        ironPickaxe(
                DecorativeModule.DIMENSIONAL_BLANK, DecorativeModule.DIMENSIONAL_BLOCK,
                DecorativeModule.DIMENSIONAL_CROSS2_BLOCK, DecorativeModule.DIMENSIONAL_CROSS_BLOCK,
                DecorativeModule.DIMENSIONAL_PATTERN1_BLOCK, DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK,
                DecorativeModule.DIMENSIONAL_SMALL_BLOCK,
                DimensionBuilderModule.DIMENSION_BUILDER,
                DimensionEditorModule.DIMENSION_EDITOR,
                EnscriberModule.ENSCRIBER,
                EssencesModule.BIOME_ABSORBER, EssencesModule.BLOCK_ABSORBER,
                EssencesModule.FLUID_ABSORBER, EssencesModule.STRUCTURE_ABSORBER,
                WorkbenchModule.WORKBENCH, WorkbenchModule.HOLDER
        );
    }

    @Override
    @Nonnull
    public String getName() {
        return "RFToolsDim Tags";
    }
}
