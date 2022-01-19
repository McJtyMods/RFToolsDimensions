package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.NoiseSlider;

public class NoiseSettingsBuilder {
    int minY = 0;
    int height = 256;
    private int noiseSizeHorizontal = 2;
    private int noiseSizeVertical = 1;
    private boolean islandNoiseOverride = false;
    private boolean amplified = false;
    private boolean largeBiomes = false;
    private NoiseSamplingSettings samplingSettings;
    private NoiseSlider topSlider;
    private NoiseSlider bottomSlider;
    private TerrainShaper terrainShaper = TerrainProvider.floatingIslands();

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
        this.samplingSettings = samplingSettings.build();
        return this;
    }

    public NoiseSettingsBuilder topSlider(NoiseSliderBuilder topSlider) {
        this.topSlider = topSlider.build();
        return this;
    }

    public NoiseSettingsBuilder bottomSlider(NoiseSliderBuilder bottomSlider) {
        this.bottomSlider = bottomSlider.build();
        return this;
    }

    public NoiseSettingsBuilder terrainShaper(TerrainShaper terrainShaper) {
        this.terrainShaper = terrainShaper;
        return this;
    }

    public static NoiseSettingsBuilder create() {
        return new NoiseSettingsBuilder();
    }

    public static NoiseSettingsBuilder create(NoiseSettings settings) {
        return new NoiseSettingsBuilder()
                .minY(settings.minY())
                .height(settings.height())
                .noiseSizeHorizontal(settings.noiseSizeHorizontal())
                .noiseSizeVertical(settings.noiseSizeVertical())
                .islandNoiseOverride(settings.islandNoiseOverride())
                .amplified(settings.isAmplified())
                .largeBiomes(settings.largeBiomes())
                .bottomSlider(NoiseSliderBuilder.create(settings.bottomSlideSettings()))
                .topSlider(NoiseSliderBuilder.create(settings.topSlideSettings()))
                .samplingSettings(NoiseSamplingSettingsBuilder.create(settings.noiseSamplingSettings()))
                .terrainShaper(settings.terrainShaper())
                ;
    }

    public NoiseSettings build() {
        return NoiseSettings.create(minY, height,
                samplingSettings,
                topSlider,
                bottomSlider,
                noiseSizeHorizontal, noiseSizeVertical, islandNoiseOverride, amplified, largeBiomes,
                terrainShaper);
    }
}