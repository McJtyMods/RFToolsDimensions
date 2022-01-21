package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.Lazy;

public class SpawnPlatform extends BuildingTemplate {

    public static final Lazy<SpawnPlatform> SPAWN_PLATFORM = Lazy.of(SpawnPlatform::new);

    public SpawnPlatform() {
        palette('_', Blocks.COMMAND_BLOCK);
        palette('#', DecorativeModule.DIMENSIONAL_SMALL_BLOCK);
        palette('.', DecorativeModule.DIMENSIONAL_BLANK);
        palette('*', Blocks.GLOWSTONE);
        palette(' ', Blocks.AIR);

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
