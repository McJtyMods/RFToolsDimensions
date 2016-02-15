package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.config.MobConfiguration;
import mcjty.rftoolsdim.dimensions.description.MobDescriptor;
import mcjty.rftoolsdim.dimensions.description.SkyDescriptor;
import mcjty.rftoolsdim.dimensions.description.WeatherDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.StringUtils;

public class DimletObjectMapping {

    public static final String NONE_ID = "None";
    public static String DEFAULT_ID = "Default";

    public static FeatureType getFeature(DimletKey dimlet) {
        if (DimletType.DIMLET_FEATURE.equals(dimlet.getType())) {
            FeatureType type = FeatureType.getFeatureById(dimlet.getId());
            return type == null ? FeatureType.FEATURE_NONE : type;
        }
        return FeatureType.FEATURE_NONE;
    }

    public static ControllerType getController(DimletKey dimlet) {
        if (DimletType.DIMLET_CONTROLLER.equals(dimlet.getType())) {
            ControllerType type = ControllerType.getControllerById(dimlet.getId());
            return type == null ? ControllerType.CONTROLLER_DEFAULT : type;
        }
        return ControllerType.CONTROLLER_DEFAULT;
    }

    public static EffectType getEffect(DimletKey dimlet) {
        if (DimletType.DIMLET_EFFECT.equals(dimlet.getType())) {
            EffectType type = EffectType.getEffectById(dimlet.getId());
            return type == null ? EffectType.EFFECT_NONE : type;
        }
        return EffectType.EFFECT_NONE;
    }

    public static StructureType getStructure(DimletKey dimlet) {
        if (DimletType.DIMLET_STRUCTURE.equals(dimlet.getType())) {
            StructureType type = StructureType.getStructureById(dimlet.getId());
            return type == null ? StructureType.STRUCTURE_NONE : type;
        }
        return StructureType.STRUCTURE_NONE;
    }

    public static TerrainType getTerrain(DimletKey dimlet) {
        if (DimletType.DIMLET_TERRAIN.equals(dimlet.getType())) {
            TerrainType type = TerrainType.getTerrainById(dimlet.getId());
            return type == null ? TerrainType.TERRAIN_VOID : type;
        }
        return TerrainType.TERRAIN_VOID;
    }

    public static IBlockState getBlock(DimletKey dimlet) {
        if (DimletType.DIMLET_MATERIAL.equals(dimlet.getType())) {
            String id = dimlet.getId();
            // @todo temporary for people who accidently got an old Default dimlet
            if (DimletObjectMapping.DEFAULT_ID.equals(id)) {
                return Blocks.stone.getDefaultState();
            }
            int lastIndexOf = StringUtils.lastIndexOf(id, "@");
            String blockid = id.substring(0, lastIndexOf);
            int meta = Integer.parseInt(id.substring(lastIndexOf + 1));

            Block block = Block.blockRegistry.getObject(new ResourceLocation(blockid));
            if (block == null) {
                return null;
            }
            return block.getStateFromMeta(meta);
        }
        return null;
    }

    public static Block getFluid(DimletKey dimlet) {
        if (DimletType.DIMLET_LIQUID.equals(dimlet.getType())) {
            String id = dimlet.getId();
            // @todo temporary for people who accidently got an old Default dimlet
            if (DimletObjectMapping.DEFAULT_ID.equals(id)) {
                return Blocks.water;
            }
            int lastIndexOf = StringUtils.lastIndexOf(id, "@");
            String blockid = id.substring(0, lastIndexOf);
//            int meta = Integer.parseInt(id.substring(lastIndexOf+1));

            Block block = Block.blockRegistry.getObject(new ResourceLocation(blockid));
            if (block == null) {
                return null;
            }
            return block;
        }
        return null;
    }

    public static BiomeGenBase getBiome(DimletKey dimlet) {
        if (DimletType.DIMLET_BIOME.equals(dimlet.getType())) {
            return BiomeGenBase.BIOME_ID_MAP.get(dimlet.getId());
        }
        return null;
    }

    public static String getDigit(DimletKey dimlet) {
        if (DimletType.DIMLET_DIGIT.equals(dimlet.getType())) {
            return dimlet.getId();
        }
        return "";
    }

    public static MobDescriptor getMob(DimletKey dimlet) {
        if (DimletType.DIMLET_MOB.equals(dimlet.getType())) {
            MobDescriptor descriptor = MobConfiguration.mobClasses.get(dimlet.getId());
            if (descriptor == null) {
                return MobConfiguration.defaultDescriptor;
            }
            return descriptor;
        }
        return null;
    }

    public static SkyDescriptor getSky(DimletKey dimlet) {
        if (DimletType.DIMLET_SKY.equals(dimlet.getType())) {
            // @todo
        }
        return new SkyDescriptor.Builder().build();
    }

    public static WeatherDescriptor getWeather(DimletKey dimlet) {
        if (DimletType.DIMLET_WEATHER.equals(dimlet.getType())) {
            // @todo
        }
        return new WeatherDescriptor.Builder().build();
    }

    public static SpecialType getSpecial(DimletKey dimlet) {
        if (DimletType.DIMLET_SPECIAL.equals(dimlet.getType())) {
            // @todo
        }
        return null;
    }

    public static float getCelestialAngle(DimletKey dimlet) {
        // @todo
        return 0.0f;
    }

    public static float getTimeSpeed(DimletKey dimlet) {
        // @todo
        return 0.0f;
    }
}
