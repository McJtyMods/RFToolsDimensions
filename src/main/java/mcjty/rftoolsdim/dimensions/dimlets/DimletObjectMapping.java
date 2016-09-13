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
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
                return Blocks.STONE.getDefaultState();
            }
            int lastIndexOf = StringUtils.lastIndexOf(id, "@");
            String blockid;
            int meta;
            if (lastIndexOf == -1) {
                blockid = id;
                meta = 0;
            } else {
                blockid = id.substring(0, lastIndexOf);
                meta = Integer.parseInt(id.substring(lastIndexOf + 1));
            }

            Block block = Block.REGISTRY.getObject(new ResourceLocation(blockid));
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
                return Blocks.WATER;
            }
            int lastIndexOf = StringUtils.lastIndexOf(id, "@");
            String blockid;
            if (lastIndexOf == -1) {
                blockid = id;
            } else {
                blockid = id.substring(0, lastIndexOf);
            }
//            int meta = Integer.parseInt(id.substring(lastIndexOf+1));

            Block block = Block.REGISTRY.getObject(new ResourceLocation(blockid));
            if (block == null) {
                return null;
            }
            return block;
        }
        return null;
    }

    public static Biome getBiome(DimletKey dimlet) {
        if (DimletType.DIMLET_BIOME.equals(dimlet.getType())) {
            Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(dimlet.getId()));
            return biome;
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
            return SkyRegistry.getSkyDescriptor(dimlet);
        }
        return new SkyDescriptor.Builder().build();
    }

    public static WeatherDescriptor getWeather(DimletKey dimlet) {
        if (DimletType.DIMLET_WEATHER.equals(dimlet.getType())) {
            return WeatherRegistry.getWeatherDescriptor(dimlet);
        }
        return new WeatherDescriptor.Builder().build();
    }

    public static SpecialType getSpecial(DimletKey dimlet) {
        if (DimletType.DIMLET_SPECIAL.equals(dimlet.getType())) {
            SpecialType type = SpecialType.getSpecialById(dimlet.getId());
            return type;
        }
        return null;
    }

    private static Map<DimletKey, Float> dimletToCelestialAngle;
    private static Map<DimletKey, Float> dimletToSpeed;

    private static void setupTimeTables() {
        if (dimletToCelestialAngle == null) {
            dimletToCelestialAngle = new HashMap<>();
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Normal"), null);
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Noon"), 0.0f);
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Midnight"), 0.5f);
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Morning"), 0.75f);
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Evening"), 0.2f);
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Fast"), null);
            dimletToCelestialAngle.put(new DimletKey(DimletType.DIMLET_TIME, "Slow"), null);
        }
        if (dimletToSpeed == null) {
            dimletToSpeed = new HashMap<>();
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Normal"), null);
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Noon"), null);
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Midnight"), null);
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Morning"), null);
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Evening"), null);
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Fast"), 2.0f);
            dimletToSpeed.put(new DimletKey(DimletType.DIMLET_TIME, "Slow"), 0.5f);
        }
    }


    public static Collection<DimletKey> getTimeDimlets() {
        setupTimeTables();
        List<DimletKey> dimlets = new ArrayList<>();
        for (DimletKey dimletKey : dimletToCelestialAngle.keySet()) {
            dimlets.add(dimletKey);
        }
        return dimlets;
    }

    public static Collection<DimletKey> getCelestialAngles() {
        setupTimeTables();
        List<DimletKey> dimlets = new ArrayList<>();
        for (DimletKey dimletKey : dimletToCelestialAngle.keySet()) {
            if (dimletToCelestialAngle.get(dimletKey) != null) {
                dimlets.add(dimletKey);
            }
        }
        return dimlets;
    }

    public static Float getCelestialAngle(DimletKey dimlet) {
        setupTimeTables();
        return dimletToCelestialAngle.get(dimlet);
    }

    public static Float getTimeSpeed(DimletKey dimlet) {
        setupTimeTables();
        return dimletToSpeed.get(dimlet);
    }
}
