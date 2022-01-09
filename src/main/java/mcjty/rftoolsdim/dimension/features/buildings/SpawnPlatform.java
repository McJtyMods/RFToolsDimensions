package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.Lazy;

public class SpawnPlatform extends BuildingTemplate {

    public static final Lazy<SpawnPlatform> SPAWN_PLATFORM = Lazy.of(SpawnPlatform::new);

    public SpawnPlatform() {
        addPalette('_', Blocks.COMMAND_BLOCK.defaultBlockState());
        addPalette('#', DecorativeModule.DIMENSIONAL_SMALL_BLOCK.get().defaultBlockState());
        addPalette('.', DecorativeModule.DIMENSIONAL_BLANK.get().defaultBlockState());
        addPalette('*', Blocks.GLOWSTONE.defaultBlockState());
        addPalette(' ', Blocks.AIR.defaultBlockState());

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
