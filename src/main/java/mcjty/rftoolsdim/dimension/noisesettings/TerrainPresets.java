package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class TerrainPresets {

    public static final ResourceKey<NoiseGeneratorSettings> RFTOOLSDIM_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("rftoolsdim_islands"));

    public static void init() {
        NoiseGeneratorSettings.register(RFTOOLSDIM_ISLANDS, test());
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
                                .xzScale(1.0d)
                                .yScale(0.5d)
                                .xzFactor(30.0d)
                                .yFactor(40.0d))
                        .topSlider(NoiseSliderBuilder.create()
                                .top(-10.4375D)
                                .size(20)
                                .offset(-80))
                        .bottomSlider(NoiseSliderBuilder.create()
                                .top(-0.234375D)
                                .size(7)
                                .offset(40))
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

}
