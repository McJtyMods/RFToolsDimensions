package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.dimension.biomes.BiomeDescriptor;
import mcjty.rftoolsdim.dimension.biomes.BiomeInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is derived from the DimensionDescriptor and gives a more optimal in-game representation of that
 */
public class DimensionInformation {

    private final TerrainType terrainType;
    private final List<BlockState> baseBlocks = new ArrayList<>();
    private final List<ConfiguredFeature<?, ?>> features = new ArrayList<>();
    private BiomeInfo biomeInfo;

    private DimensionInformation(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public BiomeInfo getBiomeInfo() {
        return biomeInfo;
    }


    public List<BlockState> getBaseBlocks() {
        return baseBlocks;
    }

    public List<ConfiguredFeature<?, ?>> getFeatures() {
        return features;
    }

    public static DimensionInformation createFrom(DimensionDescriptor descriptor) {
        DimensionInformation info = new DimensionInformation(descriptor.getTerrainType());

        for (ResourceLocation id : descriptor.getBaseBlocks()) {
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            if (block == null) {
                // @todo proper logging
                System.out.println("Can't find base block '" + id.toString() + "'!");
            } else {
                info.baseBlocks.add(block.getDefaultState());
            }
        }

        for (ResourceLocation id : descriptor.getFeatures()) {
            Feature<NoFeatureConfig> feature = (Feature<NoFeatureConfig>) ForgeRegistries.FEATURES.getValue(id);
            ConfiguredFeature<NoFeatureConfig, ?> configuredFeature = feature.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG);
            info.features.add(configuredFeature);
        }

        BiomeDescriptor biomeDescriptor = descriptor.getBiomeDescriptor();
        info.biomeInfo = BiomeInfo.createFrom(biomeDescriptor);

        return info;
    }
}
