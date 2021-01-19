package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.tileentity.ITickableTileEntity;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class BiomeAbsorberTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public BiomeAbsorberTileEntity() {
        super(EssencesModule.TYPE_BIOME_ABSORBER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(BiomeAbsorberTileEntity::new)
                .manualEntry(ManualHelper.create("rftoolsdim:dimensionbuilder"))
                .info(key("message.rftoolsdim.shiftmessage"))
                // @todo 1.16 TOP absorbtion info
                // @todo 1.16 tooltip absorbtion info
                .infoShift(header())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    @Override
    public void tick() {

    }
}
