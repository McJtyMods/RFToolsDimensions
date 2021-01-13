package mcjty.rftoolsdim.dimension.descriptor;

import com.google.common.collect.Lists;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Read a DimletDescriptor and store it in a more conveniant manner
 */
public class CompiledDescriptor {

    private TerrainType terrainType = TerrainType.NORMAL;
    private Set<ResourceLocation> features = new HashSet<>();
    private List<ResourceLocation> baseBlocks = Lists.newArrayList(new ResourceLocation("minecraft:stone"));
    private BiomeDescriptor biomeDescriptor = BiomeDescriptor.DEFAULT;

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public CompiledDescriptor(DimensionDescriptor descriptor) {
        for (DimletDescriptor dimletDescriptor : descriptor.getDimletDescriptors()) {
            switch (dimletDescriptor.getType()) {
                case TERRAIN: {
                    String name = dimletDescriptor.getName();
                    terrainType = TerrainType.byName(name);
                    if (terrainType == null) {
                        throw new RuntimeException("Bad terrain type: " + name + "!");
                    }
                    break;
                }
                case BIOME:
                    break;
                case FEATURE: {
                    String name = dimletDescriptor.getName();
                    features.add(new ResourceLocation(name));
                    break;
                }
                case MATERIAL:
                    break;
            }
        }
    }

    public List<ResourceLocation> getBaseBlocks() {
        return baseBlocks;
    }

    public Set<ResourceLocation> getFeatures() {
        return features;
    }

    public BiomeDescriptor getBiomeDescriptor() {
        return biomeDescriptor;
    }
}
