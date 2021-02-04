package mcjty.rftoolsdim.dimension.descriptor;

import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
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
    private TimeType timeType = null;

    private int createCostPerTick = 0;
    private int maintainCostPerTick = 0;
    private int randomizedCostPerTick = 0;
    private int actualTickCost = 0;

    /**
     * Compile this descriptor
     */
    @Nonnull
    public DescriptorError compile(DimensionDescriptor descriptor, DimensionDescriptor randomizedDescriptor) {
        createCostPerTick = 10; // @todo 1.16 make configurable
        maintainCostPerTick = 10;   // @todo 1.16 make configurable
        randomizedCostPerTick = 0;
        actualTickCost = 100;       // @todo make configurable
        List<BlockState> collectedBlocks = new ArrayList<>();

        for (DimletKey dimlet : descriptor.getDimlets()) {
            DescriptorError error = handleDimlet(collectedBlocks, dimlet);
            if (error != null) {
                return error;
            }
        }
        int originalMaintainCost = maintainCostPerTick;
        maintainCostPerTick = 0;
        for (DimletKey dimlet : randomizedDescriptor.getDimlets()) {
            DescriptorError error = handleDimlet(collectedBlocks, dimlet);
            if (error != null) {
                return error;
            }
        }
        randomizedCostPerTick = (int) (maintainCostPerTick * DimensionConfig.RANDOMIZED_DIMLET_COST_FACTOR.get());
        maintainCostPerTick = originalMaintainCost;

        if (!collectedBlocks.isEmpty()) {
            return ERROR(DANGLING_BLOCKS);
        }

        return DescriptorError.OK;
    }

    public void complete() {
        // In case something is still missing (shouldn't be possible)
        if (terrainType == null) {
            terrainType = TerrainType.NORMAL;
            if (baseBlocks.isEmpty()) {
                baseBlocks.add(Blocks.STONE.getDefaultState());
            }
        }
        if (timeType == null) {
            timeType = TimeType.NORMAL;
        }
        if (biomeControllerType == null) {
            biomeControllerType = BiomeControllerType.SINGLE;
        }
    }

    private DescriptorError handleDimlet(List<BlockState> collectedBlocks, DimletKey dimlet) {
        DimletSettings settings = DimletDictionary.get().getSettings(dimlet);
        if (settings != null) {
            createCostPerTick += settings.getCreateCost();
            actualTickCost += settings.getTickCost();
            maintainCostPerTick += settings.getMaintainCost();
        }
        String name = dimlet.getKey();
        switch (dimlet.getType()) {
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
            case TIME: {
                if (timeType != null) {
                    return ERROR(ONLY_ONE_TIME);
                }
                timeType = TimeType.byName(name);
                if (timeType == null) {
                    return ERROR(BAD_TIME, name);
                }
                break;
            }
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
        return null;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public int getCreateCostPerTick() {
        return createCostPerTick;
    }

    public int getActualTickCost() {
        return actualTickCost;
    }

    /**
     * Get the actual cost per tick after accounting for efficiency dimlets
     */
    public int getActualPowerCost() {
        return maintainCostPerTick + randomizedCostPerTick;
    }

    public int getMaintainCostPerTick() {
        return maintainCostPerTick;
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
