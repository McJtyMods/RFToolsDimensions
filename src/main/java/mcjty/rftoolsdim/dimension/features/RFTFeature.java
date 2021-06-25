package mcjty.rftoolsdim.dimension.features;

import com.mojang.serialization.Codec;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledFeature;
import mcjty.rftoolsdim.dimension.features.buildings.BuildingTemplate;
import mcjty.rftoolsdim.dimension.features.buildings.DimletHut;
import mcjty.rftoolsdim.dimension.features.buildings.SpawnPlatform;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
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

    private final static long[] primes = new long[] { 900157, 981961, 50001527, 32667413, 1111114993, 65548559, 320741, 100002509,
            35567897, 218021, 2900001163L, 3399018867L };

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        RFTFEATURE_CONFIGURED = Registration.RFTFEATURE.get()
                .configured(NoFeatureConfig.NONE)
                .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(1, 0, 1)));

        Registry.register(registry, CONFIGURED_RFTFEATURE_ID, RFTFEATURE_CONFIGURED);
    }

    public RFTFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
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
                        feature.getBlocks(), feature.getFluids(), primes[primeIndex % primes.length])) {
                    generatedSomething = true;
                }
                primeIndex++;
            }

            ChunkPos cp = new ChunkPos(pos);
            if (cp.x == 0 && cp.z == 0) {
                // Spawn platform
                int floorHeight = getFloorHeight(reader, cp);
                DimensionManager.get().registerPlatformHeight(reader.getLevel().dimension().location(), floorHeight);
                SpawnPlatform.SPAWN_PLATFORM.get().generate(reader, new BlockPos(3, floorHeight, 3),
                        compiledDescriptor.getBaseBlocks(), BuildingTemplate.GenerateFlag.PLAIN);
                generatedSomething = true;
            } else if (rand.nextFloat() < DimensionConfig.DIMLET_HUT_CHANCE.get()) {
                DimletHut.DIMLET_HUT.get().generate(reader, new BlockPos(cp.getMinBlockX() + 4, getFloorHeight(reader, cp),cp.getMinBlockZ() + 4),
                        compiledDescriptor.getBaseBlocks(), BuildingTemplate.GenerateFlag.FILLDOWN_IFNOTVOID);
                generatedSomething = true;
            }

            return generatedSomething;
        }
        return false;
    }

    private int getFloorHeight(ISeedReader reader, ChunkPos cp) {
        int height0 = getHeightAt(reader, cp, 8, 8);
        int height1 = getHeightAt(reader, cp, 4, 4);
        int height2 = getHeightAt(reader, cp, 12, 4);
        int height3 = getHeightAt(reader, cp, 4, 12);
        int height4 = getHeightAt(reader, cp, 12, 12);
        return (height0 + height1 + height2 + height3 + height4) / 5;
    }

    private int getHeightAt(ISeedReader reader, ChunkPos cp, int dx, int dz) {
        int height = reader.getHeight(Heightmap.Type.WORLD_SURFACE, cp.getMinBlockX() + dx, cp.getMinBlockZ() + dz);
        if (height <= 1 || height > 250) {
            height = 65;
        }
        return height;
    }

}
