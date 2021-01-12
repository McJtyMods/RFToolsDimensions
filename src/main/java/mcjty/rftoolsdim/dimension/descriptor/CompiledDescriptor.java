package mcjty.rftoolsdim.dimension.descriptor;

import com.google.common.collect.Lists;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Read a DimletDescriptor and store it in a more conveniant manner
 */
public class CompiledDescriptor {

    private TerrainType terrainType = TerrainType.NORMAL;
    private List<FeatureDescriptor> features = new ArrayList<>();
    private List<ResourceLocation> baseBlocks = new ArrayList<>();
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
                case FEATURE:
                    break;
                case MATERIAL:
                    break;
            }
        }
    }

    public List<ResourceLocation> getBaseBlocks() {
        // @todo 1.16
        return Lists.newArrayList(new ResourceLocation("minecraft:stone"));
    }

    public List<FeatureDescriptor> getFeatures() {
        // @todo 1.16
        return Collections.emptyList();
    }

    public BiomeDescriptor getBiomeDescriptor() {
        // @todo 1.16
        return BiomeDescriptor.DEFAULT;
    }
}
