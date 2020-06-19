package mcjty.rftoolsdim.modules.dimensionbuilder.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderSetup;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DimensionBuilderTileEntity extends GenericTileEntity {

    public DimensionBuilderTileEntity() {
        super(DimensionBuilderSetup.TYPE_DIMENSION_BUILDER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(DimensionBuilderTileEntity::new)
                .infusable()
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.ROTATION;
            }
        };
    }

}
