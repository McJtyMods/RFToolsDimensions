package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.util.Lazy;

public class SpawnPlatform extends BuildingTemplate {

    public static final Lazy<SpawnPlatform> SPAWN_PLATFORM = Lazy.of(SpawnPlatform::new);

    public SpawnPlatform() {
        addPalette('_', Blocks.COMMAND_BLOCK.getDefaultState());
        addPalette('#', Blocks.BLUE_TERRACOTTA.getDefaultState());
        addPalette('.', Blocks.CYAN_TERRACOTTA.getDefaultState());
        addPalette('*', Blocks.GLOWSTONE.getDefaultState());
        addPalette(' ', Blocks.AIR.getDefaultState());

        slice()
                .row("###########")
                .row("#.........#")
                .row("#.*.....*.#")
                .row("#.........#")
                .row("#.........#")
                .row("#...._....#")
                .row("#.........#")
                .row("#.........#")
                .row("#.*.....*.#")
                .row("#.........#")
                .row("###########")
                ;

        for (int i = 0 ; i < 3 ; i++) {
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
}
