package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.world.level.levelgen.NoiseSettings;

public class NoiseSettingsBuilder {
    int minY = 0;
    int height = 256;
    private int noiseSizeHorizontal = 2;
    private int noiseSizeVertical = 1;
    private boolean islandNoiseOverride = false;
    private boolean amplified = false;
    private boolean largeBiomes = false;
    // @todo 1.19
//    private NoiseSamplingSettings samplingSettings;
//    private NoiseSlider topSlider;
//    private NoiseSlider bottomSlider;
//    private TerrainShaper terrainShaper = TerrainProvider.floatingIslands();

    public NoiseSettingsBuilder minY(int minY) {
        this.minY = minY;
        return this;
    }

    public NoiseSettingsBuilder height(int height) {
        this.height = height;
        return this;
    }

    public NoiseSettingsBuilder noiseSizeHorizontal(int noiseSizeHorizontal) {
        this.noiseSizeHorizontal = noiseSizeHorizontal;
        return this;
    }

    public NoiseSettingsBuilder noiseSizeVertical(int noiseSizeVertical) {
        this.noiseSizeVertical = noiseSizeVertical;
        return this;
    }

    public NoiseSettingsBuilder islandNoiseOverride(boolean islandNoiseOverride) {
        this.islandNoiseOverride = islandNoiseOverride;
        return this;
    }

    public NoiseSettingsBuilder amplified(boolean amplified) {
        this.amplified = amplified;
        return this;
    }

    public NoiseSettingsBuilder largeBiomes(boolean largeBiomes) {
        this.largeBiomes = largeBiomes;
        return this;
    }

    public NoiseSettingsBuilder samplingSettings(NoiseSamplingSettingsBuilder samplingSettings) {
        // @todo 1.19
//        this.samplingSettings = samplingSettings.build();
        return this;
    }

    // @todo 1.19
//    public NoiseSettingsBuilder samplingSettings(NoiseSamplingSettings samplingSettings) {
//        this.samplingSettings = samplingSettings;
//        return this;
//    }

    public NoiseSettingsBuilder topSlider(NoiseSliderBuilder topSlider) {
        // @todo 1.19
//        this.topSlider = topSlider.build();
        return this;
    }

    // @todo 1.19
//    public NoiseSettingsBuilder topSlider(NoiseSlider topSlider) {
//        this.topSlider = topSlider;
//        return this;
//    }

    public NoiseSettingsBuilder bottomSlider(NoiseSliderBuilder bottomSlider) {
        // @todo 1.19
//        this.bottomSlider = bottomSlider.build();
        return this;
    }

    // @todo 1.19
//    public NoiseSettingsBuilder bottomSlider(NoiseSlider bottomSlider) {
//        this.bottomSlider = bottomSlider;
//        return this;
//    }

    // @todo 1.19
//    public NoiseSettingsBuilder terrainShaper(TerrainShaper terrainShaper) {
//        this.terrainShaper = terrainShaper;
//        return this;
//    }

    public static NoiseSettingsBuilder create(NoiseSettings settings) {
        return new NoiseSettingsBuilder()
                .minY(settings.minY())
                .height(settings.height())
                .noiseSizeHorizontal(settings.noiseSizeHorizontal())
                .noiseSizeVertical(settings.noiseSizeVertical())
                // @todo 1.19
//                .bottomSlider(settings.bottomSlideSettings())
//                .topSlider(settings.topSlideSettings())
//                .samplingSettings(settings.noiseSamplingSettings())
//                .terrainShaper(settings.terrainShaper())
                ;
    }

    // @todo 1.19
//    public NoiseSettings build() {
//        return NoiseSettings.create(minY, height,
//                samplingSettings,
//                topSlider,
//                bottomSlider,
//                noiseSizeHorizontal, noiseSizeVertical,//@todo 1.18.2 islandNoiseOverride, amplified, largeBiomes,
//                terrainShaper);
//    }
}
