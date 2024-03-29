package mcjty.rftoolsdim.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsDim.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        orientedBlock(DimensionBuilderModule.DIMENSION_BUILDER.get(), frontBasedModel("dimension_builder", modLoc("block/dimensionbuilder")));
        orientedBlock(WorkbenchModule.WORKBENCH.get(), topBasedModel("dimlet_workbench", modLoc("block/dimletworkbenchtop")));
        orientedBlock(WorkbenchModule.HOLDER.get(), frontBasedModel("knowledge_holder", modLoc("block/knowledge_holder")));
        orientedBlock(EnscriberModule.ENSCRIBER.get(), frontBasedModel("enscriber", modLoc("block/dimensionenscriber")));
        singleTextureBlock(EssencesModule.BLOCK_ABSORBER.get(), BLOCK_FOLDER + "/block_absorber", "block/blockabsorber");
        singleTextureBlock(EssencesModule.FLUID_ABSORBER.get(), BLOCK_FOLDER + "/fluid_absorber", "block/fluidabsorber");
        singleTextureBlock(EssencesModule.BIOME_ABSORBER.get(), BLOCK_FOLDER + "/biome_absorber", "block/biomeabsorber");

        registerDimensionEditor();

        singleTextureBlock(DecorativeModule.DIMENSIONAL_BLOCK.get(), BLOCK_FOLDER + "/dimensional_block", "block/decorative/dimblock_block");
        singleTextureBlock(DecorativeModule.DIMENSIONAL_BLANK.get(), BLOCK_FOLDER + "/dimensional_blank_block", "block/decorative/dimblock_blank_stone");
        singleTextureBlock(DecorativeModule.DIMENSIONAL_CROSS_BLOCK.get(), BLOCK_FOLDER + "/dimensional_cross_block", "block/decorative/dimblock_pattern3");
        singleTextureBlock(DecorativeModule.DIMENSIONAL_CROSS2_BLOCK.get(), BLOCK_FOLDER + "/dimensional_cross2_block", "block/decorative/dimblock_pattern4");
        singleTextureBlock(DecorativeModule.DIMENSIONAL_PATTERN1_BLOCK.get(), BLOCK_FOLDER + "/dimensional_pattern1_block", "block/decorative/dimblock_pattern7");
        singleTextureBlock(DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK.get(), BLOCK_FOLDER + "/dimensional_pattern2_block", "block/decorative/dimblock_pattern8");
        singleTextureBlock(DecorativeModule.DIMENSIONAL_SMALL_BLOCK.get(), BLOCK_FOLDER + "/dimensional_small_blocks", "block/decorative/dimblock_small_blocks");

        simpleBlock(WorkbenchModule.RESEARCHER.get(), models().slab("researcher",
                modLoc("block/researcher_side"),
                new ResourceLocation("rftoolsbase", "block/base/machinebottom"),
                new ResourceLocation("rftoolsbase", "block/base/machinetop")));
    }

    private void registerDimensionEditor() {
        ModelFile model = frontBasedModel("dimensioneditor", modLoc("block/dimensioneditor"));
        ModelFile modelEmpty = frontBasedModel("dimensioneditor_empty", modLoc("block/dimensioneditor_empty"));
        ModelFile modelBusy1 = frontBasedModel("dimensioneditor_busy1", modLoc("block/dimensioneditor_busy1"));
        ModelFile modelBusy2 = frontBasedModel("dimensioneditor_busy2", modLoc("block/dimensioneditor_busy2"));
        VariantBlockStateBuilder builder = getVariantBuilder(DimensionEditorModule.DIMENSION_EDITOR.get());
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.CHARGING)
                    .modelForState().modelFile(model), direction);
            applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.BUILDING1)
                    .modelForState().modelFile(modelBusy1), direction);
            applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.BUILDING2)
                    .modelForState().modelFile(modelBusy2), direction);
            applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.EMPTY)
                    .modelForState().modelFile(modelEmpty), direction);
        }
    }

}
