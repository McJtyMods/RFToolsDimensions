package mcjty.rftoolsdim.dimension;

import mcjty.rftoolsdim.dimension.biomes.BiomeInfo;
import mcjty.rftoolsdim.dimension.descriptor.BiomeDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
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

    public static DimensionInformation createFrom(CompiledDescriptor descriptor) {
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

        for (ResourceLocation featureDescriptor : descriptor.getFeatures()) {
            Feature<IFeatureConfig> feature = (Feature<IFeatureConfig>) ForgeRegistries.FEATURES.getValue(featureDescriptor);
            // @todo error checking

//            Function<Dynamic<?>, ? extends IFeatureConfig> configFactory = ObfuscationReflectionHelper.getPrivateValue(Feature.class, feature, "field_214535_a"); // 'configFactory'
//            Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, featureDescriptor.getConfigElement());
//            IFeatureConfig cfg = configFactory.apply(dynamic);
//
//            ConfiguredFeature<IFeatureConfig, ?> configuredFeature = feature.withConfiguration(cfg);
//            info.features.add(configuredFeature);
        }

        BiomeDescriptor biomeDescriptor = descriptor.getBiomeDescriptor();
        info.biomeInfo = BiomeInfo.createFrom(biomeDescriptor);

        return info;
    }
}
