package mcjty.rftoolsdim.modules.dimensioneditor;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;

public class DataGenHelper {

    public static void registerDimensionEditor(BaseBlockStateProvider provider) {
        ModelFile model = provider.frontBasedModel("dimensioneditor", provider.modLoc("block/dimensioneditor"));
        ModelFile modelEmpty = provider.frontBasedModel("dimensioneditor_empty", provider.modLoc("block/dimensioneditor_empty"));
        ModelFile modelBusy1 = provider.frontBasedModel("dimensioneditor_busy1", provider.modLoc("block/dimensioneditor_busy1"));
        ModelFile modelBusy2 = provider.frontBasedModel("dimensioneditor_busy2", provider.modLoc("block/dimensioneditor_busy2"));
        VariantBlockStateBuilder builder = provider.getVariantBuilder(DimensionEditorModule.DIMENSION_EDITOR.get());
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            provider.applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.CHARGING)
                    .modelForState().modelFile(model), direction);
            provider.applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.BUILDING1)
                    .modelForState().modelFile(modelBusy1), direction);
            provider.applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.BUILDING2)
                    .modelForState().modelFile(modelBusy2), direction);
            provider.applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(DimensionEditorTileEntity.OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.EMPTY)
                    .modelForState().modelFile(modelEmpty), direction);
        }
    }

}
