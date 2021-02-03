package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Lazy;

public class DimletHut extends BuildingTemplate {

    public static final Lazy<DimletHut> DIMLET_HUT = Lazy.of(DimletHut::new);

    public DimletHut() {
        addPalette('#', DecorativeModule.DIMENSIONAL_SMALL_BLOCK.get().getDefaultState());
        addPalette('+', DecorativeModule.DIMENSIONAL_BLOCK.get().getDefaultState());
        addPalette('X', DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK.get().getDefaultState());
        addPalette('.', DecorativeModule.DIMENSIONAL_BLANK.get().getDefaultState());
        addPalette('*', Blocks.GLOWSTONE.getDefaultState());
        addPalette('1', Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState());
        addPalette(' ', Blocks.AIR.getDefaultState());
        addPalette('D', Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        addPalette('d', Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        addPalette('_', Blocks.STONE_PRESSURE_PLATE.getDefaultState());
        addPalette('C', (reader, pos) -> {
            reader.setBlockState(pos, Blocks.CHEST.getDefaultState(), 0);
            TileEntity te = reader.getTileEntity(pos);
            if (te instanceof ChestTileEntity) {
                ((ChestTileEntity) te).setLootTable(DimensionRegistry.HUT_LOOT, reader.getSeed());
            }

        });

        slice()
                .row("#########")
                .row("#.......#")
                .row("#.......#")
                .row("#.......#")
                .row("#.......#")
                .row("#.......#")
                .row("#.......#")
                .row("#.......#")
                .row("#########")
                .row("   ###   ")
        ;
        slice()
                .row("X+++++++X")
                .row("+   C   +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+   _   +")
                .row("X+1+D+1+X")
                .row("    _    ")
        ;
        slice()
                .row("X+++1+++X")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("1       1")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("X+1+d+1+X")
                .row("         ")
        ;
        slice()
                .row("X+++++++X")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("+       +")
                .row("X+1+++1+X")
                .row("         ")
        ;
        slice()
                .row("XXXXXXXXX")
                .row("X.......X")
                .row("X.......X")
                .row("X.......X")
                .row("X.......X")
                .row("X.......X")
                .row("X.......X")
                .row("X.......X")
                .row("XXXXXXXXX")
                .row("         ")
        ;
    }
}
