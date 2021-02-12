package mcjty.rftoolsdim.dimension.descriptor;

import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.AdminDimletType;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidUtil;
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
    private final Set<AttributeType> attributeTypes = new HashSet<>();
    private final List<BlockState> baseBlocks = new ArrayList<>();
    private BlockState baseLiquid = null;

    private final Set<AdminDimletType> adminDimletTypes = new HashSet<>();
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
        List<BlockState> collectedFluids = new ArrayList<>();
        Set<AttributeType> collectedAttributes = new HashSet<>();

        for (DimletKey dimlet : descriptor.getDimlets()) {
            DescriptorError error = handleDimlet(collectedBlocks, collectedFluids, collectedAttributes, dimlet);
            if (error != null) {
                return error;
            }
        }
        int originalMaintainCost = maintainCostPerTick;
        maintainCostPerTick = 0;
        for (DimletKey dimlet : randomizedDescriptor.getDimlets()) {
            DescriptorError error = handleDimlet(collectedBlocks, collectedFluids, collectedAttributes, dimlet);
            if (error != null) {
                return error;
            }
        }
        randomizedCostPerTick = (int) (maintainCostPerTick * DimensionConfig.RANDOMIZED_DIMLET_COST_FACTOR.get());
        maintainCostPerTick = originalMaintainCost;

        if (adminDimletTypes.contains(AdminDimletType.CHEATER)) {
            createCostPerTick = 0;
            maintainCostPerTick = 0;
            randomizedCostPerTick = 0;
            actualTickCost = 1;
        }

        if (!collectedBlocks.isEmpty()) {
            return ERROR(DANGLING_BLOCKS);
        }

        if (!collectedFluids.isEmpty()) {
            return ERROR(DANGLING_FLUIDS);
        }

        if (!collectedAttributes.isEmpty()) {
            return ERROR(DANGLING_ATTRIBUTES);
        }

        return DescriptorError.OK;
    }

    public void complete() {
        if (terrainType == null) {
            terrainType = TerrainType.NORMAL;
        }
        if (timeType == null) {
            timeType = TimeType.NORMAL;
        }
        if (biomeControllerType == null) {
            biomeControllerType = BiomeControllerType.SINGLE;
        }
        if (baseBlocks.isEmpty()) {
            baseBlocks.add(Blocks.STONE.getDefaultState());
        }
        if (baseLiquid == null) {
            baseLiquid = Blocks.WATER.getDefaultState();
        }
    }

    private DescriptorError handleDimlet(List<BlockState> collectedBlocks, List<BlockState> collectedFluids, Set<AttributeType> collectedAttributes, DimletKey dimlet) {
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
                attributeTypes.addAll(collectedAttributes);
                collectedAttributes.clear();

                if (collectedFluids.size() > 1) {
                    return ERROR(ONLY_ONE_FLUID, name);
                } else if (collectedFluids.size() == 1) {
                    baseLiquid = collectedFluids.get(0);
                    collectedFluids.clear();
                }

                break;
            case ATTRIBUTE: {
                AttributeType type = AttributeType.byName(dimlet.getKey());
                if (type == null) {
                    return ERROR(BAD_ATTRIBUTE, name);
                }
                collectedAttributes.add(type);
                break;
            }
            case ADMIN: {
                AdminDimletType type = AdminDimletType.byName(dimlet.getKey());
                if (type == null) {
                    return ERROR(BAD_ADMIN_TYPE, name);
                }
                adminDimletTypes.add(type);
                break;
            }
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

                compiledFeature.getFluids().addAll(collectedFluids);
                collectedFluids.clear();
                if (compiledFeature.getFluids().isEmpty()) {
                    compiledFeature.getFluids().add(Blocks.WATER.getDefaultState());
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

            case FLUID: {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
                if (fluid == null) {
                    return ERROR(BAD_FLUID, name);
                }
                BlockState blockState = fluid.getDefaultState().getBlockState();
                if (blockState != null && !blockState.isAir()) {
                    collectedFluids.add(blockState);
                } else {
                    return ERROR(FLUID_HAS_NO_BLOCK, name);
                }
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

    public Set<AttributeType> getAttributeTypes() {
        return attributeTypes;
    }

    public Set<AdminDimletType> getAdminDimletTypes() {
        return adminDimletTypes;
    }

    public BlockState getBaseLiquid() {
        return baseLiquid;
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
