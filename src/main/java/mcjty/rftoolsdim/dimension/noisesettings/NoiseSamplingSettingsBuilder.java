package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.world.level.levelgen.NoiseSamplingSettings;

public class NoiseSamplingSettingsBuilder {
    private double xzScale = 1.0D;
    private double yScale = 1.0D;
    private double xzFactor = 80.0D;
    private double yFactor = 80.0D;

    public NoiseSamplingSettingsBuilder xzScale(double xzScale) {
        this.xzScale = xzScale;
        return this;
    }

    public NoiseSamplingSettingsBuilder yScale(double yScale) {
        this.yScale = yScale;
        return this;
    }

    public NoiseSamplingSettingsBuilder xzFactor(double xzFactor) {
        this.xzFactor = xzFactor;
        return this;
    }

    public NoiseSamplingSettingsBuilder yFactor(double yFactor) {
        this.yFactor = yFactor;
        return this;
    }

    public static NoiseSamplingSettingsBuilder create() {
        return new NoiseSamplingSettingsBuilder();
    }

    public static NoiseSamplingSettingsBuilder create(NoiseSamplingSettings settings) {
        return new NoiseSamplingSettingsBuilder()
                .xzScale(settings.xzScale())
                .xzFactor(settings.xzFactor())
                .yScale(settings.yScale())
                .yFactor(settings.yFactor());
    }

    public NoiseSamplingSettings build() {
        return new NoiseSamplingSettings(xzScale, yScale, xzFactor, yFactor);
    }
}
