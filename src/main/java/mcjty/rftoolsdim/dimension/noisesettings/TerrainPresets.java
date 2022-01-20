package mcjty.rftoolsdim.dimension.noisesettings;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

import java.util.Map;
import java.util.Optional;

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

    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_CHAOTIC = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("rftoolsdim_chaotic"));
    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("rftoolsdim_islands"));
    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_CAVERN = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("rftoolsdim_cavern"));

    public static void init() {
        NoiseGeneratorSettings.register(RFTOOLSDIM_CHAOTIC, chaotic());
        NoiseGeneratorSettings.register(RFTOOLSDIM_ISLANDS, test());
        NoiseGeneratorSettings.register(RFTOOLSDIM_CAVERN, cavern());
    }

    private static NoiseGeneratorSettings cavern() {
        Map<StructureFeature<?>, StructureFeatureConfiguration> map = Maps.newHashMap(StructureSettings.DEFAULTS);
        map.put(StructureFeature.RUINED_PORTAL, new StructureFeatureConfiguration(25, 10, 34222645));
        return new NoiseGeneratorSettings(new StructureSettings(Optional.empty(), map),
                NoiseSettings.create(-64, 256+128,
                        new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D),
                        new NoiseSlider(0.9375D, 3, 0),
                        new NoiseSlider(2.5D, 4, -1),
                        1, 2, false, false, false, TerrainProvider.nether()),
                Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(),
                SurfaceRuleData.overworld(), 32, false, false, false, false, false, true);
    }

    private static NoiseGeneratorSettings islands() {

        return NoiseGeneratorSettingsBuilder.create()
                .seaLevel(-64)
                .noiseSettings(NoiseSettingsBuilder.create()
                        .minY(0)
                        .height(256)
                        .samplingSettings(NoiseSamplingSettingsBuilder.create()
                                .xzScale(2.0d)
                                .yScale(1.0d)
                                .xzFactor(80.0d)
                                .yFactor(160.0d))
                        .topSlider(NoiseSliderBuilder.create()
                                .top(-23.4375D)
                                .size(64)
                                .offset(-46))
                        .bottomSlider(NoiseSliderBuilder.create()
                                .top(-0.234375D)
                                .size(7)
                                .offset(1))
                        .noiseSizeHorizontal(2)
                        .noiseSizeVertical(1)
                        .islandNoiseOverride(false)
                        .amplified(false)
                        .largeBiomes(false)
                        .terrainShaper(TerrainProvider.floatingIslands()))
                .baseBlock(Blocks.STONE.defaultBlockState())
                .liquidBlock(Blocks.WATER.defaultBlockState())
                .ruleSource(SurfaceRuleDataBuilder.create()
                        .what(false)
                        .bedrockRoof(false)
                        .bedrockFloor(false))
                .disableMobGeneration(false)
                .aquifersEnabled(false)
                .noiseCavesEnabled(false)
                .oreVeinsEnabled(false)
                .noodleCavesEnabled(false)
                .legacyRandomSource(true)
                .build();
    }

    // Similar to end: good
    public static NoiseGeneratorSettings likeEnd() {
        return NoiseGeneratorSettingsBuilder.create()
                .seaLevel(-64)
                .noiseSettings(NoiseSettingsBuilder.create()
                        .minY(0)
                        .height(256)
                        .samplingSettings(NoiseSamplingSettingsBuilder.create()
                                .xzScale(2.0d)
                                .yScale(1.0d)
                                .xzFactor(80.0d)
                                .yFactor(160.0d))
                        .topSlider(NoiseSliderBuilder.create()
                                .top(-23.4375D)
                                .size(64)
                                .offset(-46))
                        .bottomSlider(NoiseSliderBuilder.create()
                                .top(-0.234375D)
                                .size(7)
                                .offset(1))
                        .noiseSizeHorizontal(2)
                        .noiseSizeVertical(1)
                        .islandNoiseOverride(true)
                        .amplified(false)
                        .largeBiomes(false)
                        .terrainShaper(TerrainProvider.floatingIslands()))
                .baseBlock(Blocks.STONE.defaultBlockState())
                .liquidBlock(Blocks.WATER.defaultBlockState())
                .ruleSource(SurfaceRuleDataBuilder.create()
                        .what(false)
                        .bedrockRoof(false)
                        .bedrockFloor(false))
                .disableMobGeneration(false)
                .aquifersEnabled(false)
                .noiseCavesEnabled(false)
                .oreVeinsEnabled(false)
                .noodleCavesEnabled(false)
                .legacyRandomSource(true)
                .build();
    }

    private static NoiseGeneratorSettings chaotic() {
        return NoiseGeneratorSettingsBuilder.create()
                .seaLevel(-64)
                .noiseSettings(NoiseSettingsBuilder.create()
                        .minY(0)
                        .height(256)
                        .samplingSettings(NoiseSamplingSettingsBuilder.create()
                                .xzScale(1.0d)
                                .yScale(0.5d)
                                .xzFactor(30.0d)
                                .yFactor(40.0d))
                        .topSlider(NoiseSliderBuilder.create()
                                .top(-23.4375D)
                                .size(64)
                                .offset(-46))
                        .bottomSlider(NoiseSliderBuilder.create()
                                .top(-0.234375D)
                                .size(7)
                                .offset(1))
                        .noiseSizeHorizontal(2)
                        .noiseSizeVertical(1)
                        .islandNoiseOverride(false)
                        .amplified(false)
                        .largeBiomes(false)
                        .terrainShaper(TerrainProvider.floatingIslands()))
                .baseBlock(Blocks.STONE.defaultBlockState())
                .liquidBlock(Blocks.WATER.defaultBlockState())
                .ruleSource(SurfaceRuleDataBuilder.create()
                        .what(false)
                        .bedrockRoof(false)
                        .bedrockFloor(false))
                .disableMobGeneration(false)
                .aquifersEnabled(false)
                .noiseCavesEnabled(false)
                .oreVeinsEnabled(false)
                .noodleCavesEnabled(false)
                .legacyRandomSource(true)
                .build();
    }

    private static NoiseGeneratorSettings test() {
        return NoiseGeneratorSettingsBuilder.create()
                .seaLevel(-64)
                .noiseSettings(NoiseSettingsBuilder.create()
                        .minY(0)
                        .height(256)
                        .samplingSettings(NoiseSamplingSettingsBuilder.create()
                                .xzScale(2.0d)
                                .yScale(1.0d)
                                .xzFactor(80.0d)
                                .yFactor(160.0d))
                        .topSlider(NoiseSliderBuilder.create()
                                .top(-23.4375D)
                                .size(64)
                                .offset(-46))
                        .bottomSlider(NoiseSliderBuilder.create()
                                .top(-0.234375D)
                                .size(7)
                                .offset(1))
                        .noiseSizeHorizontal(2)
                        .noiseSizeVertical(1)
                        .islandNoiseOverride(true)
                        .amplified(false)
                        .largeBiomes(false)
                        .terrainShaper(TerrainProvider.floatingIslands()))
                .baseBlock(Blocks.STONE.defaultBlockState())
                .liquidBlock(Blocks.WATER.defaultBlockState())
                .ruleSource(SurfaceRuleDataBuilder.create()
                        .what(false)
                        .bedrockRoof(false)
                        .bedrockFloor(false))
                .disableMobGeneration(false)
                .aquifersEnabled(false)
                .noiseCavesEnabled(false)
                .oreVeinsEnabled(false)
                .noodleCavesEnabled(false)
                .legacyRandomSource(true)
                .build();
    }

}
