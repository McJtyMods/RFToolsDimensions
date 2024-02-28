package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.util.Lazy;

public class SpawnHut extends BuildingTemplate {

    public static final Lazy<SpawnHut> SPAWN_HUT = Lazy.of(SpawnHut::new);

    public SpawnHut() {
        palette('@', Blocks.COMMAND_BLOCK);
        palette('#', DecorativeModule.DIMENSIONAL_SMALL_BLOCK);
        palette('.', DecorativeModule.DIMENSIONAL_BLANK);
        palette('*', Blocks.GLOWSTONE);
        palette('+', Blocks.GLASS_PANE);
        palette(' ', Blocks.AIR);
        palette('D', Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        palette('d', Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        palette('_', Blocks.STONE_PRESSURE_PLATE);

        slice()
                .row("###########")
                .row("#.........#")
                .row("#.*.....*.#")
                .row("#.........#")
                .row("#.........#")
                .row("#....@....#")
                .row("#.........#")
                .row("#.........#")
                .row("#.*.....*.#")
                .row("#.........#")
                .row("###########")
                ;

        slice()
                .row("#.+.+.+.+.#")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".    _    .")
                .row("#.+..D..+.#")
        ;
        slice()
                .row("#.+.+.+.+.#")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("#.+..d..+.#")
        ;

        slice()
                .row("#.+.+.+.+.#")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("+         +")
                .row(".         .")
                .row("#.+.....+.#")
        ;
        slice()
                .row("###########")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("#.........#")
                .row("###########")
        ;

        slice()
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
                .row("           ")
        ;
    }
}
