package mcjty.rftoolsdim.dimension.descriptor;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.AdminDimletType;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.additional.SkyDimletType;
import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static mcjty.rftoolsdim.dimension.descriptor.DescriptorError.Code.*;
import static mcjty.rftoolsdim.dimension.descriptor.DescriptorError.ERROR;

/**
 * Read a DimletDescriptor and store it in a more conveniant manner
 */
public class CompiledDescriptor {

    private TerrainType terrainType = null;
    private final Set<AttributeType> attributeTypes = EnumSet.noneOf(AttributeType.class);
    private BlockState baseBlock = null;
    private BlockState baseLiquid = null;

    private final Set<AdminDimletType> adminDimletTypes = EnumSet.noneOf(AdminDimletType.class);
    private final Set<CompiledFeature> features = new HashSet<>();
    private BiomeControllerType biomeControllerType = null;
    private final Set<Biome.BiomeCategory> biomeCategories = new HashSet<>();
    private final List<ResourceLocation> biomes = new ArrayList<>();
    private final List<ResourceLocation> structures = new ArrayList<>();
    private long skyDimletTypes = 0;
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

        List<ResourceLocation> collectedTags = new ArrayList<>();
        List<BlockState> collectedBlocks = new ArrayList<>();
        List<BlockState> collectedFluids = new ArrayList<>();
        Set<AttributeType> collectedAttributes = EnumSet.noneOf(AttributeType.class);

        for (DimletKey dimlet : descriptor.getDimlets()) {
            DescriptorError error = handleDimlet(collectedTags, collectedBlocks, collectedFluids, collectedAttributes, dimlet);
            if (error != null) {
                return error;
            }
        }
        int originalMaintainCost = maintainCostPerTick;
        maintainCostPerTick = 0;
        for (DimletKey dimlet : randomizedDescriptor.getDimlets()) {
            DescriptorError error = handleDimlet(collectedTags, collectedBlocks, collectedFluids, collectedAttributes, dimlet);
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
        if (!collectedTags.isEmpty()) {
            return ERROR(DANGLING_TAGS);
        }

        return DescriptorError.OK;
    }

    public void complete() {
        if (timeType == null) {
            timeType = TimeType.NORMAL;
        }
        if (biomeControllerType == null) {
            biomeControllerType = BiomeControllerType.SINGLE;
        }
        if (baseBlock == null) {
            baseBlock = Blocks.STONE.defaultBlockState();
        }
        if (baseLiquid == null) {
            baseLiquid = Blocks.WATER.defaultBlockState();
        }
    }

    private DescriptorError handleDimlet(List<ResourceLocation> collectedTags, List<BlockState> collectedBlocks, List<BlockState> collectedFluids, Set<AttributeType> collectedAttributes, DimletKey dimlet) {
        DimletSettings settings = DimletDictionary.get().getSettings(dimlet);
        if (settings != null) {
            createCostPerTick += settings.getCreateCost();
            actualTickCost += settings.getTickCost();
            maintainCostPerTick += settings.getMaintainCost();
        }
        String name = dimlet.key();
        switch (dimlet.type()) {
            case TERRAIN:
                if (terrainType != null) {
                    return ERROR(ONLY_ONE_TERRAIN);
                }
                terrainType = TerrainType.byName(name);
                if (terrainType == null) {
                    return ERROR(BAD_TERRAIN_TYPE, name);
                }

                if (!collectedTags.isEmpty()) {
                    return ERROR(NO_TAGS);
                }
                if (collectedBlocks.size() > 1) {
                    return ERROR(ONLY_ONE_BLOCK);
                }
                if (collectedBlocks.isEmpty()) {
                    baseBlock = Blocks.STONE.defaultBlockState();
                } else {
                    baseBlock = collectedBlocks.iterator().next();
                }
                collectedBlocks.clear();

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
                AttributeType type = AttributeType.byName(dimlet.key());
                if (type == null) {
                    return ERROR(BAD_ATTRIBUTE, name);
                }
                collectedAttributes.add(type);
                break;
            }
            case DIGIT:
                break;
            case ADMIN: {
                AdminDimletType type = AdminDimletType.byName(dimlet.key());
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
            case BIOME_CATEGORY:
                biomeCategories.add(Biome.BiomeCategory.byName(name));
                break;
            case BIOME:
                biomes.add(new ResourceLocation(name));
                break;
            case STRUCTURE:
                structures.add(new ResourceLocation(name));
                break;
            case SKY: {
                SkyDimletType skyDimletType = SkyDimletType.byName(name);
                if (skyDimletType != null) {
                    skyDimletTypes |= skyDimletType.getMask();
                }
                break;
            }
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

                for (ResourceLocation rl : collectedTags) {
                    Tag<Block> tag = BlockTags.getAllTags().getTag(rl);
                    if (tag != null) {
                        collectedBlocks.addAll(tag.getValues().stream().map(Block::defaultBlockState).collect(Collectors.toList()));
                    }
                }
                collectedTags.clear();

                compiledFeature.getBlocks().addAll(collectedBlocks);
                collectedBlocks.clear();
                if (compiledFeature.getBlocks().isEmpty()) {
                    compiledFeature.getBlocks().add(Blocks.STONE.defaultBlockState());
                }

                compiledFeature.getFluids().addAll(collectedFluids);
                collectedFluids.clear();
                if (compiledFeature.getFluids().isEmpty()) {
                    compiledFeature.getFluids().add(Blocks.WATER.defaultBlockState());
                }

                features.add(compiledFeature);
                break;
            }
            case TAG: {
                collectedTags.add(new ResourceLocation(name));
                break;
            }
            case BLOCK: {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
                if (block == null) {
                    return ERROR(BAD_BLOCK, name);
                }
                collectedBlocks.add(block.defaultBlockState());
                break;
            }

            case FLUID: {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
                if (fluid == null) {
                    return ERROR(BAD_FLUID, name);
                }
                BlockState blockState = fluid.defaultFluidState().createLegacyBlock();
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

    public void log(String header) {
        header = "--------------------------------------------------\n" + header;
        header += "\n    TERRAIN: " + terrainType.getName();
        header += "\n    TIME: " + timeType.getName();
        header += "\n    SKY: " + SkyDimletType.getDescription(skyDimletTypes);
        header += "\n    LIQUID: " + baseLiquid.getBlock().getRegistryName().toString();
        if (baseBlock != null) {
            header += "\n    BLOCK: " + baseBlock.getBlock().getRegistryName().toString();
        }
        for (AdminDimletType type : adminDimletTypes) {
            header += "\n    ADMIN: " + type.getName();
        }
        for (AttributeType type : attributeTypes) {
            header += "\n    ATTR: " + type.getName();
        }
        header += "\n    BIOME CTRL: " + biomeControllerType.getName();
        for (ResourceLocation biome : biomes) {
            header += "\n        BIOME: " + biome.toString();
        }
        for (Biome.BiomeCategory category : biomeCategories) {
            header += "\n        CATEGORY: " + category.getName();
        }
        for (ResourceLocation structure : structures) {
            header += "\n    STRUCTURE: " + structure.toString();
        }
        for (CompiledFeature feature : features) {
            header += "\n    FEATURE: " + feature.getFeatureType().getName();
            for (BlockState block : feature.getBlocks()) {
                header += "\n        BLOCK: " + block.getBlock().getRegistryName().toString();
            }
            for (BlockState fluid : feature.getFluids()) {
                header += "\n        LIQUID: " + fluid.getBlock().getRegistryName().toString();
            }
        }

        header += "\n--------------------------------------------------";
        RFToolsDim.setup.getLogger().info(header);
    }

    public long getSkyDimletTypes() {
        return skyDimletTypes;
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

    public List<ResourceLocation> getStructures() {
        return structures;
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

    public BlockState getBaseBlock() {
        return baseBlock;
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

    public Set<Biome.BiomeCategory> getBiomeCategories() {
        return biomeCategories;
    }
}
