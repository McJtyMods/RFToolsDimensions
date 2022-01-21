package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Lazy;

public class DimletHut extends BuildingTemplate {

    public static final Lazy<DimletHut> DIMLET_HUT = Lazy.of(DimletHut::new);

    public DimletHut() {
        palette('#', DecorativeModule.DIMENSIONAL_SMALL_BLOCK);
        palette('+', DecorativeModule.DIMENSIONAL_BLOCK);
        palette('X', DecorativeModule.DIMENSIONAL_PATTERN2_BLOCK);
        palette('.', DecorativeModule.DIMENSIONAL_BLANK);
        palette('*', Blocks.GLOWSTONE);
        palette('1', Blocks.LIGHT_BLUE_STAINED_GLASS);
        palette(' ', Blocks.AIR);
        palette('D', Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        palette('d', Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        palette('_', Blocks.STONE_PRESSURE_PLATE);
        palette('C', (reader, pos) -> {
            reader.setBlock(pos, Blocks.CHEST.defaultBlockState(), 0);
            BlockEntity te = reader.getBlockEntity(pos);
            if (te instanceof ChestBlockEntity) {
                ((ChestBlockEntity) te).setLootTable(DimensionRegistry.HUT_LOOT, reader.getRandom().nextLong());
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
