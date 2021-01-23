package mcjty.rftoolsdim.dimension.features;

import com.mojang.serialization.Codec;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledFeature;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;

import java.util.Random;
import java.util.Set;

public class RFTFeature extends Feature<NoFeatureConfig> {

    public static final ResourceLocation RFTFEATURE_ID = new ResourceLocation(RFToolsDim.MODID, "rftfeature");
    public static final ResourceLocation CONFIGURED_RFTFEATURE_ID = new ResourceLocation(RFToolsDim.MODID, "configured_rftfeature");

    public static ConfiguredFeature<?, ?> RFTFEATURE_CONFIGURED;

    private final static long primes[] = new long[] { 900157, 981961, 50001527, 32667413, 1111114993, 65548559, 320741, 100002509,
            35567897, 218021, 2900001163L };

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        RFTFEATURE_CONFIGURED = Registration.RFTFEATURE.get()
                .withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(1, 0, 1)));

        Registry.register(registry, CONFIGURED_RFTFEATURE_ID, RFTFEATURE_CONFIGURED);
    }

    public RFTFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (generator instanceof BaseChunkGenerator) {
            CompiledDescriptor compiledDescriptor = ((BaseChunkGenerator) generator).getSettings().getCompiledDescriptor();
            Set<CompiledFeature> features = compiledDescriptor.getFeatures();
            if (features.stream().anyMatch(f -> f.getFeatureType().equals(FeatureType.NONE))) {
                // Inhibit all other features
                return false;
            }
            boolean generatedSomething = false;
            int primeIndex = 0;
            for (CompiledFeature feature : features) {
                if (feature.getFeatureType().getFeature().generate(reader, generator, rand, pos,
                        feature.getBlocks(), primes[primeIndex % primes.length])) {
                    generatedSomething = true;
                }
                primeIndex++;
            }
            return generatedSomething;
        }
        return false;
    }

}
