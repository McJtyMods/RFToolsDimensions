package mcjty.rftoolsdim.dimension.descriptor;

import com.google.common.collect.Lists;
import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

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
    private List<ResourceLocation> baseBlocks = new ArrayList<>();
    private BiomeControllerType biomeControllerType;
    private List<ResourceLocation> biomes = new ArrayList<>();

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public CompiledDescriptor(DimensionDescriptor descriptor) {
        for (DimletDescriptor dimletDescriptor : descriptor.getDimletDescriptors()) {
            String name = dimletDescriptor.getName();
            switch (dimletDescriptor.getType()) {
                case TERRAIN:
                    terrainType = TerrainType.byName(name);
                    if (terrainType == null) {
                        throw new RuntimeException("Bad terrain type: " + name + "!");
                    }
                    break;
                case BIOME_CONTROLLER:
                    biomeControllerType = BiomeControllerType.byName(name);
                    if (biomeControllerType == null) {
                        throw new RuntimeException("Bad biome controller type: " + name + "!");
                    }
                    break;
                case BIOME:
                    biomes.add(new ResourceLocation(name));
                    break;
                case FEATURE:
                    features.add(new ResourceLocation(name));
                    break;
                case BLOCK:
                    baseBlocks.add(new ResourceLocation(name));
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

    public BiomeControllerType getBiomeControllerType() {
        return biomeControllerType;
    }

    public List<ResourceLocation> getBiomes() {
        return biomes;
    }
}
