package mcjty.rftoolsdim.dimension.noisesettings;

import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/*
Sampling:
     xz_scale (double): Scales the X and Z axis of the noise. Higher values results in more intricate
                     horizontal shapes. Works similarly to coordinate scale from old customized worlds.
     xz_factor (double): Smoothes the noise on the horizontal axis. Works similarly to main noise scale
                     X/Z from old customized worlds.
     y_scale (double): Scales the Y axis of the noise. Higher values result in more intricate vertical
                     shapes. Works similarly to height scale from old customized worlds.
     y_factor (double): Smoothes the noise on the vertical axis. Works similarly to main noise scale Y
                     from old customized worlds.
Bottom Slide:
     target (integer): The value of the curve. Negative values remove the floor and round off the bottom
                     of the islands, positive values make a floor. Higher values produce larger effects.
     size (integer): Defines the size of the affected area from the bottom of the world. Uses the same
                     formula as in top_slide.
     offset (integer): Moves the affected area from the bottom of the world. Uses the same formula as in top_slide.
                     For bottom_slide, positive values move the area up and negative values bring it down.
Top Slide:
     target (integer): The value of the curve. Negative values round off the top of the hills in the
                     affected area, positive values create a roof. Higher values produce larger effects.
     size (integer): Defines the size of the affected area from the top of the world. size is calculated
                     using the formula size = <height in blocks> * 0.25 / size_vertical.
     offset (integer): Moves the affected area from the top of the world. offset uses the same formula as
                     size so offset = <height in blocks> * 0.25 / size_vertical. For top_slide, positive
                     values move the area down and negative values bring it up.

height (integer): Changes the max height of generated terrain by squashing the world. For example, with
                 height=128, the ground is set to Y=32. this does not affect sea level.[needs testing]
size_horizontal (integer): Changes the X/Z scale of the landmass, but not the biomes.[needs testing]
size_vertical (integer): Changes the Y scale of the landmass. Values between 1 and 15 gradually increase
                 the hill height, above 20 are all above the normal sea level of 63, and higher than 32
                 give normal land levels of 100+.[needs testing]
density_factor (double): Changes the gradient of terrain density from the bottom to the top of the world.
                 Positive values result in terrain that is solid underneath with shapes that shrink at
                 higher altitudes, negative values result in terrain that is solid on top with empty space
                 underneath. Greater positive or negative values result in a sharper transition.
density_offset (double; values between -1 and 1): Moves the center height for terrain density relative to
                 Y=128, by an amount inversely proportional to density_factor.[needs testing]
random_density_offset (boolean; optional):[needs testing]
simplex_surface_noise (boolean):[needs testing]
island_noise_override (boolean; optional): Causes the world to generate like The End with a big island in the
                 center and smaller ones around.
amplified (boolean; optional): Toggles between amplified and normal terrain generation. Can be used alongside
                 large biomes in `vanilla_layered` types, and in any dimension (Nether, End, and custom).

new NoiseSettings(
    height,
    sampling = new ScalingSettings(xz_scale, y_scale, xz_factor, y_factor),
    top_slide = new SlideSettings(target, size, offet),         // Settings for the curve at the top of the world
    bottom_slide = new SlideSettings(target, size, offset),     // Settings for the curve at the bottom of the world
    size_horizontal, size_vertical, density_factor, density_offset,
    simplex_surface_noise, random_density_offset, island_noise_override, amplified),

 */
public class TerrainPresets {

    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_CHAOTIC = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(RFToolsDim.MODID, "rftoolsdim_chaotic"));
    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(RFToolsDim.MODID, "rftoolsdim_islands"));
    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_CAVERN = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(RFToolsDim.MODID, "rftoolsdim_cavern"));
    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_FLAT = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(RFToolsDim.MODID, "rftoolsdim_flat"));
    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_OVERWORLD = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(RFToolsDim.MODID, "rftoolsdim_overworld"));
}
