package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.world.level.levelgen.NoiseSlider;

public class NoiseSliderBuilder {
    private double top = 1.0D;
    private int size = 1;
    private int offset = 0;

    public NoiseSliderBuilder top(double top) {
        this.top = top;
        return this;
    }

    public NoiseSliderBuilder size(int size) {
        this.size = size;
        return this;
    }

    public NoiseSliderBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public static NoiseSliderBuilder create() {
        return new NoiseSliderBuilder();
    }

    public static NoiseSliderBuilder create(NoiseSlider slider) {
        return new NoiseSliderBuilder()
                .top(slider.target)
                .offset(slider.offset)
                .size(slider.size);
    }

    public NoiseSlider build() {
        return new NoiseSlider(top, size, offset);
    }
}
