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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * Compile this descriptor. Return null if all is ok. Otherwise return an error string
     */
    public String compile(DimensionDescriptor descriptor) {
        List<BlockState> collectedBlocks = new ArrayList<>();

        for (DimletKey dimletDescriptor : descriptor.getDimletDescriptors()) {
            String name = dimletDescriptor.getKey();
            switch (dimletDescriptor.getType()) {
                case TERRAIN:
                    if (terrainType != null) {
                        return "You can only have one terrain type!";
                    }
                    terrainType = TerrainType.byName(name);
                    if (terrainType == null) {
                        return "Bad terrain type: " + name + "!";
                    }
                    baseBlocks.addAll(collectedBlocks);
                    collectedBlocks.clear();
                    if (baseBlocks.isEmpty()) {
                        baseBlocks.add(Blocks.STONE.getDefaultState());
                    }
                    break;
                case BIOME_CONTROLLER:
                    if (biomeControllerType != null) {
                        return "You can only have one biome controller!";
                    }
                    biomeControllerType = BiomeControllerType.byName(name);
                    if (biomeControllerType == null) {
                        return "Bad biome controller type: " + name + "!";
                    }
                    break;
                case BIOME:
                    biomes.add(new ResourceLocation(name));
                    break;
                case FEATURE: {
                    FeatureType feature = FeatureType.byName(name);
                    if (feature == null) {
                        return "Bad feature: " + name + "!";
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
                        return "Bad block: " + name + "!";
                    }
                    collectedBlocks.add(block.getDefaultState());
                    break;
                }
            }
        }

        if (!collectedBlocks.isEmpty()) {
            return "Dangling blocks! Blocks should come before either a terrain or a feature";
        }

        if (terrainType == null) {
            terrainType = TerrainType.NORMAL;
        }
        if (biomeControllerType == null) {
            biomeControllerType = BiomeControllerType.SINGLE;
        }

        return null;
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
