package mcjty.rftoolsdim.dimensions.world;

import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.storage.WorldInfo;

public abstract class GenericBiomeProvider extends BiomeProvider {

    public abstract DimensionInformation getDimensionInformation(); // Hack to get the dimension information here before 'super'.

    public GenericBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);
    }

    @Override
    public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
        GenLayer[] layer = super.getModdedBiomeGenerators(worldType, seed, original);
        GenLayer rflayer = null;
        ControllerType type = getDimensionInformation().getControllerType();

        switch (type) {
            case CONTROLLER_DEFAULT:
            case CONTROLLER_SINGLE:
                // Cannot happen
                break;
            case CONTROLLER_CHECKERBOARD:
                rflayer = new GenLayerCheckerboard(this, seed, layer[0]);
                break;
            case CONTROLLER_COLD:
            case CONTROLLER_WARM:
            case CONTROLLER_MEDIUM:
            case CONTROLLER_DRY:
            case CONTROLLER_WET:
            case CONTROLLER_FIELDS:
            case CONTROLLER_MOUNTAINS:
            case CONTROLLER_MAGICAL:
            case CONTROLLER_FOREST:
            case CONTROLLER_FILTERED:
                rflayer = new GenLayerFiltered(this, seed, layer[0], type);
                break;
        }
        GenLayerVoronoiZoom zoomLayer = new GenLayerVoronoiZoom(10L, rflayer);
        zoomLayer.initWorldGenSeed(seed);
        return new GenLayer[] {rflayer, zoomLayer, rflayer};
    }
}
