package mcjty.rftoolsdim.dimension.descriptor;

import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mcjty.rftoolsdim.dimension.descriptor.DescriptorError.Code.*;
import static mcjty.rftoolsdim.dimension.descriptor.DescriptorError.ERROR;

/**
 * Read a DimletDescriptor and store it in a more conveniant manner
 */
public class CompiledDescriptor {

    private TerrainType terrainType = null;
    private final List<BlockState> baseBlocks = new ArrayList<>();
    private final Set<CompiledFeature> features = new HashSet<>();
    private BiomeControllerType biomeControllerType = null;
    private final List<ResourceLocation> biomes = new ArrayList<>();

    public TerrainType getTerrainType() {
        return terrainType;
    }

    /**
     * Compile this descriptor
     */
    @Nonnull
    public DescriptorError compile(DimensionDescriptor descriptor) {
        List<BlockState> collectedBlocks = new ArrayList<>();

        for (DimletKey dimletDescriptor : descriptor.getDimlets()) {
            String name = dimletDescriptor.getKey();
            switch (dimletDescriptor.getType()) {
                case TERRAIN:
                    if (terrainType != null) {
                        return ERROR(ONLY_ONE_TERRAIN);
                    }
                    terrainType = TerrainType.byName(name);
                    if (terrainType == null) {
                        return ERROR(BAD_TERRAIN_TYPE, name);
                    }
                    baseBlocks.addAll(collectedBlocks);
                    collectedBlocks.clear();
                    if (baseBlocks.isEmpty()) {
                        baseBlocks.add(Blocks.STONE.getDefaultState());
                    }
                    break;
                case BIOME_CONTROLLER:
                    if (biomeControllerType != null) {
                        return ERROR(ONLY_ONE_BIOME_CONTROLLER);
                    }
                    biomeControllerType = BiomeControllerType.byName(name);
                    if (biomeControllerType == null) {
                        return ERROR(BAD_BIOME_CONTROLLER, name);
                    }
                    break;
                case BIOME:
                    biomes.add(new ResourceLocation(name));
                    break;
                case FEATURE: {
                    FeatureType feature = FeatureType.byName(name);
                    if (feature == null) {
                        return ERROR(BAD_FEATURE, name);
                    }
                    CompiledFeature compiledFeature = new CompiledFeature(feature);
                    compiledFeature.getBlocks().addAll(collectedBlocks);
                    collectedBlocks.clear();
                    if (compiledFeature.getBlocks().isEmpty()) {
                        compiledFeature.getBlocks().add(Blocks.STONE.getDefaultState());
                    }
                    features.add(compiledFeature);
                    break;
                }
                case BLOCK: {
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
                    if (block == null) {
                        return ERROR(BAD_BLOCK, name);
                    }
                    collectedBlocks.add(block.getDefaultState());
                    break;
                }
            }
        }

        if (!collectedBlocks.isEmpty()) {
            return ERROR(DANGLING_BLOCKS);
        }

        if (terrainType == null) {
            terrainType = TerrainType.NORMAL;
        }
        if (biomeControllerType == null) {
            biomeControllerType = BiomeControllerType.SINGLE;
        }

        return DescriptorError.OK;
    }

    public List<BlockState> getBaseBlocks() {
        return baseBlocks;
    }

    public Set<CompiledFeature> getFeatures() {
        return features;
    }

    public BiomeControllerType getBiomeControllerType() {
        return biomeControllerType;
    }

    public List<ResourceLocation> getBiomes() {
        return biomes;
    }
}
