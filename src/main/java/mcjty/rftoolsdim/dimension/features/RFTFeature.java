package mcjty.rftoolsdim.dimension.features;

import com.mojang.serialization.Codec;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.DimensionCreator;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledFeature;
import mcjty.rftoolsdim.dimension.features.buildings.BuildingTemplate;
import mcjty.rftoolsdim.dimension.features.buildings.DimletHut;
import mcjty.rftoolsdim.dimension.features.buildings.SpawnHut;
import mcjty.rftoolsdim.dimension.features.buildings.SpawnPlatform;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.*;

public class RFTFeature extends Feature<NoneFeatureConfiguration> {

    public static final ResourceLocation RFTFEATURE_ID = new ResourceLocation(RFToolsDim.MODID, "rftfeature");

    public static PlacedFeature RFTFEATURE_CONFIGURED;

    private static final long[] PRIMES = new long[] { 900157, 981961, 50001527, 32667413, 1111114993, 65548559, 320741, 100002509,
            35567897, 218021, 2900001163L, 3399018867L };

    public static void registerConfiguredFeatures() {
        RFTFEATURE_CONFIGURED = registerPlacedFeature("rftfeature", Registration.RFTFEATURE.get().configured(NoneFeatureConfiguration.INSTANCE),
                CountPlacement.of(1));
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        PlacedFeature placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(registryName), feature).placed(placementModifiers);
        return PlacementUtils.register(registryName, placed);
    }

    public RFTFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        ChunkGenerator generator = context.chunkGenerator();
        BlockPos pos = context.origin();
        Random rand = context.random();
        WorldGenLevel reader = context.level();
        if (generator instanceof RFToolsChunkGenerator chunkGenerator) {
            CompiledDescriptor compiledDescriptor = chunkGenerator.getDimensionSettings().getCompiledDescriptor();
            Set<CompiledFeature> features = compiledDescriptor.getFeatures();
            if (features.stream().anyMatch(f -> f.getFeatureType().equals(FeatureType.NONE))) {
                // Inhibit all other features
                return false;
            }
            boolean generatedSomething = false;
            int primeIndex = 0;
            for (CompiledFeature feature : features) {
                if (feature.getFeatureType().getFeature().generate(reader, generator, rand, pos,
                        feature.getBlocks(), feature.getFluids(), PRIMES[primeIndex % PRIMES.length])) {
                    generatedSomething = true;
                }
                primeIndex++;
            }

            ChunkPos cp = new ChunkPos(pos);
            TerrainType terrainType = compiledDescriptor.getTerrainType();
            List<BlockState> baseBlocks = Collections.singletonList(compiledDescriptor.getBaseBlock());
            if (cp.x == 0 && cp.z == 0) {
                // Spawn platform
                int floorHeight = getFloorHeight(terrainType, reader, cp);
                DimensionCreator.get().registerPlatformHeight(reader.getLevel().dimension().location(), floorHeight);

                if (floorHeight < generator.getSeaLevel()) {
                    SpawnHut.SPAWN_HUT.get().generate(terrainType, reader, new BlockPos(3, floorHeight, 3),
                            baseBlocks, BuildingTemplate.GenerateFlag.PLAIN);
                } else {
                    SpawnPlatform.SPAWN_PLATFORM.get().generate(terrainType, reader, new BlockPos(3, floorHeight, 3),
                            baseBlocks, BuildingTemplate.GenerateFlag.PLAIN);
                }
                generatedSomething = true;
            } else if (rand.nextFloat() < DimensionConfig.DIMLET_HUT_CHANCE.get()) {
                DimletHut.DIMLET_HUT.get().generate(terrainType, reader, new BlockPos(cp.getMinBlockX() + 4, getFloorHeight(terrainType, reader, cp),cp.getMinBlockZ() + 4),
                        baseBlocks, BuildingTemplate.GenerateFlag.FILLDOWN_IFNOTVOID);
                generatedSomething = true;
            }

            return generatedSomething;
        }
        return false;
    }

    private int getFloorHeight(TerrainType type, WorldGenLevel reader, ChunkPos cp) {
        int height0 = getHeightAt(type, reader, cp, 8, 8);
        int height1 = getHeightAt(type, reader, cp, 4, 4);
        int height2 = getHeightAt(type, reader, cp, 12, 4);
        int height3 = getHeightAt(type, reader, cp, 4, 12);
        int height4 = getHeightAt(type, reader, cp, 12, 12);
        return (height0 + height1 + height2 + height3 + height4) / 5;
    }

    private int getHeightAt(TerrainType type, WorldGenLevel reader, ChunkPos cp, int dx, int dz) {
        int height;
        if (type == TerrainType.CAVERN) {
            height = (reader.getMinBuildHeight() + reader.getMaxBuildHeight())/2;
            ChunkAccess chunk = reader.getChunk(cp.x, cp.z);
            BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(dx, 0, dz);
            while (height > reader.getMinBuildHeight() && chunk.getBlockState(mpos.setY(height)).isAir()) {
                height--;
            }
        } else {
            height = reader.getHeight(Heightmap.Types.WORLD_SURFACE, cp.getMinBlockX() + dx, cp.getMinBlockZ() + dz);
        }
        if (height <= reader.getMinBuildHeight()+2 || height > reader.getMaxBuildHeight()-10) {
            height = 65;
        }
        return height;
    }

}
