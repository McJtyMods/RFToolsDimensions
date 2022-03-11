package mcjty.rftoolsdim.dimension.noisesettings;

import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

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
    private NoiseRouterWithOnlyNoises router;
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

    public NoiseGeneratorSettingsBuilder router(NoiseRouterWithOnlyNoises router) {
        this.router = router;
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
                .liquidBlock(settings.defaultFluid())
                .baseBlock(settings.defaultBlock())
                .router(settings.noiseRouter())
                //@todo 1.18.2
//                .noiseCavesEnabled(settings.isNoiseCavesEnabled())
//                .noodleCavesEnabled(settings.isNoodleCavesEnabled())
//                .oreVeinsEnabled(settings.isOreVeinsEnabled())
                .seaLevel(settings.seaLevel())
                ;
    }

    public NoiseGeneratorSettings build(DimensionSettings settings) {
        List<ResourceLocation> structures = settings.getCompiledDescriptor().getStructures();
//        Map<StructureFeature<?>, StructureFeatureConfiguration> structMap = new HashMap<>();
//        for (ResourceLocation structure : structures) {
//            if (structure.getPath().equals("none")) {
//                structMap.clear();  // No structures
//                break;
//            } else if (structure.getPath().equals("default"))  {
//                structMap.clear();
//                addDefaults(structMap);
//            } else {
//                StructureFeature<?> feature = ForgeRegistries.STRUCTURE_FEATURES.getValue(structure);
//                if (feature != null) {
//                    structMap.put(feature, new StructureFeatureConfiguration(4, 2, 654564546));
//                }
//            }
//        }
//        var structs = ImmutableMap.copyOf(structMap);
//        StructureSettings structureSettings = new StructureSettings(Optional.empty(), structs);
        return new NoiseGeneratorSettings(
                noiseSettings,
                baseBlock, liquidBlock,
                router,
                ruleSource,
                seaLevel, disableMobGeneration, aquifersEnabled, /*noiseCavesEnabled, */oreVeinsEnabled, /*noodleCavesEnabled, */legacyRandomSource);
    }

//    private void addDefaults(Map<StructureFeature<?>, StructureFeatureConfiguration> structMap) {
//        structMap.putAll(StructureSettings.DEFAULTS);
//    }

    public NoiseGeneratorSettings build() {

//        var structs = ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
//                .put(StructureFeature.VILLAGE, new StructureFeatureConfiguration(34, 8, 10387312))
//                .put(StructureFeature.DESERT_PYRAMID, new StructureFeatureConfiguration(32, 8, 14357617))
//                .put(StructureFeature.IGLOO, new StructureFeatureConfiguration(32, 8, 14357618))
//                .put(StructureFeature.JUNGLE_TEMPLE, new StructureFeatureConfiguration(32, 8, 14357619))
//                .put(StructureFeature.SWAMP_HUT, new StructureFeatureConfiguration(32, 8, 14357620))
//                .put(StructureFeature.PILLAGER_OUTPOST, new StructureFeatureConfiguration(32, 8, 165745296))
//                .put(StructureFeature.STRONGHOLD, new StructureFeatureConfiguration(1, 0, 0))
//                .put(StructureFeature.OCEAN_MONUMENT, new StructureFeatureConfiguration(32, 5, 10387313))
//                .put(StructureFeature.END_CITY, new StructureFeatureConfiguration(20, 11, 10387313))
//                .put(StructureFeature.WOODLAND_MANSION, new StructureFeatureConfiguration(80, 20, 10387319))
//                .put(StructureFeature.BURIED_TREASURE, new StructureFeatureConfiguration(1, 0, 0))
//                .put(StructureFeature.MINESHAFT, new StructureFeatureConfiguration(1, 0, 0))
//                .put(StructureFeature.RUINED_PORTAL, new StructureFeatureConfiguration(40, 15, 34222645))
//                .put(StructureFeature.SHIPWRECK, new StructureFeatureConfiguration(24, 4, 165745295))
//                .put(StructureFeature.OCEAN_RUIN, new StructureFeatureConfiguration(20, 8, 14357621))
//                .put(StructureFeature.BASTION_REMNANT, new StructureFeatureConfiguration(27, 4, 30084232))
//                .put(StructureFeature.NETHER_BRIDGE, new StructureFeatureConfiguration(27, 4, 30084232))
//                .put(StructureFeature.NETHER_FOSSIL, new StructureFeatureConfiguration(2, 1, 14357921))
//                .build();
//        StructureSettings structureSettings = new StructureSettings(Optional.empty(), structs);
        return new NoiseGeneratorSettings(
                noiseSettings,
                baseBlock, liquidBlock,
                router,
                ruleSource,
                seaLevel, disableMobGeneration, aquifersEnabled, /*noiseCavesEnabled, */oreVeinsEnabled, /*noodleCavesEnabled, */legacyRandomSource);
    }
}
