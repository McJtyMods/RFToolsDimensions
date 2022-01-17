package mcjty.rftoolsdim.dimension.noisesettings;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class NoiseGeneratorSettingsBuilder {
    private BlockState baseBlock = Blocks.STONE.defaultBlockState();
    private BlockState liquidBlock = Blocks.WATER.defaultBlockState();
    private int seaLevel = -64;
    private boolean disableMobGeneration = false;
    private boolean aquifersEnabled = false;
    private boolean noiseCavesEnabled = false;
    private boolean oreVeinsEnabled = false;
    private boolean noodleCavesEnabled = false;
    private boolean legacyRandomSource = true;
    private NoiseSettings noiseSettings;
    private SurfaceRules.RuleSource ruleSource;

    public NoiseGeneratorSettingsBuilder baseBlock(BlockState baseBlock) {
        this.baseBlock = baseBlock;
        return this;
    }

    public NoiseGeneratorSettingsBuilder liquidBlock(BlockState liquidBlock) {
        this.liquidBlock = liquidBlock;
        return this;
    }

    public NoiseGeneratorSettingsBuilder seaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
        return this;
    }

    public NoiseGeneratorSettingsBuilder disableMobGeneration(boolean disableMobGeneration) {
        this.disableMobGeneration = disableMobGeneration;
        return this;
    }

    public NoiseGeneratorSettingsBuilder aquifersEnabled(boolean aquifersEnabled) {
        this.aquifersEnabled = aquifersEnabled;
        return this;
    }

    public NoiseGeneratorSettingsBuilder noiseCavesEnabled(boolean noiseCavesEnabled) {
        this.noiseCavesEnabled = noiseCavesEnabled;
        return this;
    }

    public NoiseGeneratorSettingsBuilder oreVeinsEnabled(boolean oreVeinsEnabled) {
        this.oreVeinsEnabled = oreVeinsEnabled;
        return this;
    }

    public NoiseGeneratorSettingsBuilder noodleCavesEnabled(boolean noodleCavesEnabled) {
        this.noodleCavesEnabled = noodleCavesEnabled;
        return this;
    }

    public NoiseGeneratorSettingsBuilder legacyRandomSource(boolean legacyRandomSource) {
        this.legacyRandomSource = legacyRandomSource;
        return this;
    }

    public NoiseGeneratorSettingsBuilder noiseSettings(NoiseSettingsBuilder noiseSettings) {
        this.noiseSettings = noiseSettings.build();
        return this;
    }

    public NoiseGeneratorSettingsBuilder ruleSource(SurfaceRuleDataBuilder ruleSource) {
        this.ruleSource = ruleSource.build();
        return this;
    }

    public NoiseGeneratorSettingsBuilder ruleSource(SurfaceRules.RuleSource ruleSource) {
        this.ruleSource = ruleSource;
        return this;
    }

    public static NoiseGeneratorSettingsBuilder create() {
        return new NoiseGeneratorSettingsBuilder();
    }

    public static NoiseGeneratorSettingsBuilder create(NoiseGeneratorSettings settings) {
        return new NoiseGeneratorSettingsBuilder()
                .noiseSettings(NoiseSettingsBuilder.create(settings.noiseSettings()))
                .ruleSource(settings.surfaceRule())
                .aquifersEnabled(settings.isAquifersEnabled())
                .disableMobGeneration(settings.disableMobGeneration())
                .legacyRandomSource(settings.useLegacyRandomSource())
                .liquidBlock(settings.getDefaultFluid())
                .baseBlock(settings.getDefaultBlock())
                .noiseCavesEnabled(settings.isNoiseCavesEnabled())
                .noodleCavesEnabled(settings.isNoodleCavesEnabled())
                .oreVeinsEnabled(settings.isOreVeinsEnabled())
                .seaLevel(settings.seaLevel())
                ;
    }

    public NoiseGeneratorSettings build() {
        return new NoiseGeneratorSettings(new StructureSettings(false),
                noiseSettings,
                baseBlock, liquidBlock,
                ruleSource,
                seaLevel, disableMobGeneration, aquifersEnabled, noiseCavesEnabled, oreVeinsEnabled, noodleCavesEnabled, legacyRandomSource);
    }
}
