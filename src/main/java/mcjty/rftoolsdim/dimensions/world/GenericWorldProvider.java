package mcjty.rftoolsdim.dimensions.world;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.api.dimension.IRFToolsWorldProvider;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.WeatherDescriptor;
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import mcjty.rftoolsdim.dimensions.types.PatreonType;
import mcjty.rftoolsdim.dimensions.types.SkyType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import mcjty.rftoolsdim.network.PacketGetDimensionEnergy;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

//@Optional.InterfaceList(@Optional.Interface(iface = "ivorius.reccomplex.dimensions.DimensionDictionary$Handler", modid = "reccomplex"))
public class GenericWorldProvider extends WorldProvider implements  /*@todo implements DimensionDictionary.Handler,*/ IRFToolsWorldProvider {

    public static final String RFTOOLS_DIMENSION = "rftools_dimension";

    private DimensionInformation dimensionInformation;
    private DimensionStorage storage;
    private long seed;
    private Set<String> dimensionTypes = null;  // Used for Recurrent Complex support

    private long calculateSeed(long seed, int dim) {
        if (dimensionInformation == null || dimensionInformation.getWorldVersion() < DimensionInformation.VERSION_DIMLETSSEED) {
            return dim * 13L + seed;
        } else {
            return dimensionInformation.getDescriptor().calculateSeed(seed);
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.rftoolsType;
    }

    @Override
    public long getSeed() {
        if (dimensionInformation == null || dimensionInformation.getWorldVersion() < DimensionInformation.VERSION_CORRECTSEED) {
            return super.getSeed();
        } else {
            return seed;
        }
    }

    public void setDimensionInformation(DimensionInformation info) {
        dimensionInformation = info;
    }

    public DimensionInformation getDimensionInformation() {
        if (dimensionInformation == null) {
            int dim = getDimension();
            if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                dimensionInformation = RfToolsDimensionManager.getDimensionManagerClient().getDimensionInformation(dim);
            } else {
                // Note: we cannot use world here since we are possibly still busy setting up our world so the 'mapStorage'
                // is always correct here. So we have to use the overworld.
                dimensionInformation = RfToolsDimensionManager.getDimensionManager(DimensionManager.getWorld(0)).getDimensionInformation(dim);
            }
            if (dimensionInformation == null) {
                Logging.getLogger().catching(new RuntimeException("Dimension information for dimension " + dim + " is missing!"));
            } else {
                setSeed(dim);
//                setupProviderInfo();
            }
        }
        return dimensionInformation;
    }

    @Override
    public String getSaveFolder() {
        return "RFTOOLS" + getDimension();
    }

    //    @Override
//    @Optional.Method(modid = "reccomplex")
//    public Set<String> getDimensionTypes() {
//        getDimensionInformation();
//        if (dimensionInformation == null) {
//            return Collections.EMPTY_SET;
//        }
//        if (dimensionTypes == null) {
//            dimensionTypes = new HashSet<String>();
//            dimensionTypes.add(DimensionDictionary.INFINITE);
//            dimensionTypes.add("RFTOOLS_DIMENSION");
//            // @todo temporary. This should probably be in the TerrainType enum.
//            switch (dimensionInformation.getTerrainType()) {
//                case TERRAIN_VOID:
//                case TERRAIN_ISLAND:
//                case TERRAIN_ISLANDS:
//                case TERRAIN_CHAOTIC:
//                case TERRAIN_PLATEAUS:
//                case TERRAIN_GRID:
//                    dimensionTypes.add(DimensionDictionary.NO_TOP_LIMIT);
//                    dimensionTypes.add(DimensionDictionary.NO_BOTTOM_LIMIT);
//                    break;
//                case TERRAIN_FLAT:
//                case TERRAIN_AMPLIFIED:
//                case TERRAIN_NORMAL:
//                case TERRAIN_NEARLANDS:
//                    dimensionTypes.add(DimensionDictionary.NO_TOP_LIMIT);
//                    dimensionTypes.add(DimensionDictionary.BOTTOM_LIMIT);
//                    break;
//                case TERRAIN_CAVERN_OLD:
//                    dimensionTypes.add(DimensionDictionary.BOTTOM_LIMIT);
//                    dimensionTypes.add(DimensionDictionary.TOP_LIMIT);
//                    break;
//                case TERRAIN_CAVERN:
//                case TERRAIN_LOW_CAVERN:
//                case TERRAIN_FLOODED_CAVERN:
//                    dimensionTypes.add(DimensionDictionary.BOTTOM_LIMIT);
//                    dimensionTypes.add(DimensionDictionary.NO_TOP_LIMIT);
//                    break;
//            }
//            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_RECURRENTCOMPLEX)) {
//                Collections.addAll(dimensionTypes, dimensionInformation.getDimensionTypes());
//            }
//        }
//        return dimensionTypes;
//    }
//
    private void setSeed(int dim) {
        if (dimensionInformation == null) {
            if (world == null) {
                return;
            }
            dimensionInformation = RfToolsDimensionManager.getDimensionManager(world).getDimensionInformation(dim);
            if (dimensionInformation == null) {
                Logging.log("Error: setSeed() called with null diminfo. Error ignored!");
                return;
            }
        }
        long forcedSeed = dimensionInformation.getForcedDimensionSeed();
        if (forcedSeed != 0) {
            Logging.log("Forced seed for dimension " + dim + ": " + forcedSeed);
            seed = forcedSeed;
        } else {
            long baseSeed = dimensionInformation.getBaseSeed();
            if (baseSeed != 0) {
                seed = calculateSeed(baseSeed, dim) ;
            } else {
                seed = calculateSeed(world.getSeed(), dim) ;
            }
        }
//        seed = dimensionInformation.getBaseSeed();
//        System.out.println("seed = " + seed);
    }

    private DimensionStorage getStorage() {
        if (storage == null) {
            storage = DimensionStorage.getDimensionStorage(world);
        }
        return storage;
    }


//    @Override
//    public void registerWorldChunkManager() {
//        getDimensionInformation();
//        setupProviderInfo();
//    }


    @Override
    protected void generateLightBrightnessTable() {
        getDimensionInformation();
        if (dimensionInformation != null && dimensionInformation.getTerrainType() == TerrainType.TERRAIN_INVERTIGO) {
            for (int i = 0; i <= 15; ++i)
            {
                float f1 = 1.0F - i / 15.0F;
                this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 1.0F;
            }
            return;
        }
        super.generateLightBrightnessTable();
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        if (biomeProvider == null) {
            createBiomeProviderInternal();
        }
        return biomeProvider;
    }

    @Override
    protected void init() {
        super.init();
        if (world instanceof WorldServer) {
            createBiomeProviderInternal();
            return;
        }

        // We are on a client here and we don't have sufficient information right here (dimension information has not synced yet)
        biomeProvider = null;
    }

    private void createBiomeProviderInternal() {
        getDimensionInformation();
        if (dimensionInformation != null) {
            ControllerType type = dimensionInformation.getControllerType();
            if (type == ControllerType.CONTROLLER_SINGLE) {
                this.biomeProvider = new BiomeProviderSingle(dimensionInformation.getBiomes().get(0));
            } else if (type == ControllerType.CONTROLLER_DEFAULT) {
                WorldInfo worldInfo = world.getWorldInfo();
                worldInfo = new WorldInfo(worldInfo) {
                    @Override
                    public long getSeed() {
                        return seed;
                    }
                };
                this.biomeProvider = new BiomeProvider(worldInfo);
            } else {
                GenericBiomeProvider.hackyDimensionInformation = dimensionInformation;      // Hack to get the dimension information in the superclass.
                this.biomeProvider = new GenericBiomeProvider(seed, world.getWorldInfo(), dimensionInformation);
            }
        } else {
            this.biomeProvider = new BiomeProvider(world.getWorldInfo());
        }

        if (dimensionInformation != null) {
            this.hasSkyLight = dimensionInformation.getTerrainType().hasSky();

            if (world.isRemote) {
                // Only on client!
                SkyType skyType = dimensionInformation.getSkyDescriptor().getSkyType();
                if (!hasSkyLight) {
                    SkyRenderer.registerNoSky(this);
                } else if (skyType.skyboxType != null) {
                    SkyRenderer.registerSkybox(this, skyType);
                } else if (skyType == SkyType.SKY_ENDER) {
                    SkyRenderer.registerEnderSky(this);
                } else {
                    SkyRenderer.registerSky(this, dimensionInformation);
                }

                if (dimensionInformation.isPatreonBitSet(PatreonType.PATREON_KENNEY)) {
                    SkyRenderer.registerKenneyCloudRenderer(this);
                }
            }
        }
    }

    @Override
    public double getHorizon() {
        getDimensionInformation();
        if (dimensionInformation != null && dimensionInformation.getTerrainType().hasNoHorizon()) {
            return 0;
        } else {
            return super.getHorizon();
        }
    }

    @Override
    public boolean isSurfaceWorld() {
        getDimensionInformation();
        if (dimensionInformation == null) {
            return super.isSurfaceWorld();
        }
        return dimensionInformation.getTerrainType().hasSky();
    }

//    @Override
//    public String getDimensionName() {
//        return RFTOOLS_DIMENSION;
//    }


//
//    @Override
//    public String getWelcomeMessage() {
//        return "Entering the rftools dimension!";
//    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        getDimensionInformation();
        if (GeneralConfiguration.respawnSameDim || (dimensionInformation != null && dimensionInformation.isRespawnHere())) {
            DimensionStorage dimensionStorage = getStorage();
            int power = dimensionStorage.getEnergyLevel(getDimension());
            if (power < 1000) {
                return GeneralConfiguration.spawnDimension;
            } else {
                return getDimension();
            }
        }
        return GeneralConfiguration.spawnDimension;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        int dim = getDimension();
        setSeed(dim);
        return new GenericChunkGenerator(world, seed);
    }

    @Override
    public Biome getBiomeForCoords(BlockPos pos) {
        return super.getBiomeForCoords(pos);
    }

    @Override
    public int getActualHeight() {
        return 256;
    }

    private static long lastFogTime = 0;

    @SideOnly(Side.CLIENT)
    @Override
    public float getCloudHeight() {
        getDimensionInformation();
        if (dimensionInformation != null && dimensionInformation.getTerrainType() == TerrainType.TERRAIN_INVERTIGO) {
            return 5;
        }
        return super.getCloudHeight();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float angle, float dt) {
        int dim = getDimension();
        if (System.currentTimeMillis() - lastFogTime > 1000) {
            lastFogTime = System.currentTimeMillis();
            RFToolsDimMessages.INSTANCE.sendToServer(new PacketGetDimensionEnergy(dim));
        }

        float factor = calculatePowerBlackout(dim);
        getDimensionInformation();

        Vec3d color = super.getFogColor(angle, dt);
        if (dimensionInformation == null) {
            return color;
        } else {
            float r = dimensionInformation.getSkyDescriptor().getFogColorFactorR() * factor;
            float g = dimensionInformation.getSkyDescriptor().getFogColorFactorG() * factor;
            float b = dimensionInformation.getSkyDescriptor().getFogColorFactorB() * factor;
            return new Vec3d(color.x * r, color.y * g, color.z * b);
        }
    }

    private static long lastTime = 0;

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        int dim = getDimension();
        if (System.currentTimeMillis() - lastTime > 1000) {
            lastTime = System.currentTimeMillis();
            RFToolsDimMessages.INSTANCE.sendToServer(new PacketGetDimensionEnergy(dim));
        }

        float factor = calculatePowerBlackout(dim);
        getDimensionInformation();

        Vec3d skyColor = super.getSkyColor(cameraEntity, partialTicks);
        if (dimensionInformation == null) {
            return skyColor;
        } else {
            float r = dimensionInformation.getSkyDescriptor().getSkyColorFactorR() * factor;
            float g = dimensionInformation.getSkyDescriptor().getSkyColorFactorG() * factor;
            float b = dimensionInformation.getSkyDescriptor().getSkyColorFactorB() * factor;
            return new Vec3d(skyColor.x * r, skyColor.y * g, skyColor.z * b);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getCloudColor(float partialTicks)
    {
        getDimensionInformation();

        Vec3d cloudColor = super.getCloudColor(partialTicks);
        if (dimensionInformation == null || dimensionInformation.isPatreonBitSet(PatreonType.PATREON_KENNEY)) {
            return cloudColor;
        } else {
            float r = dimensionInformation.getSkyDescriptor().getCloudColorFactorR();
            float g = dimensionInformation.getSkyDescriptor().getCloudColorFactorG();
            float b = dimensionInformation.getSkyDescriptor().getCloudColorFactorB();
            return new Vec3d(cloudColor.x * r, cloudColor.y * g, cloudColor.z * b);
        }
    }

    private float calculatePowerBlackout(int dim) {
        float factor = 1.0f;
        int power = getStorage().getEnergyLevel(dim);
        if (power < PowerConfiguration.DIMPOWER_WARN3) {
            factor = ((float) power) / PowerConfiguration.DIMPOWER_WARN3 * 0.2f;
        } else  if (power < PowerConfiguration.DIMPOWER_WARN2) {
            factor = (float) (power - PowerConfiguration.DIMPOWER_WARN3) / (PowerConfiguration.DIMPOWER_WARN2 - PowerConfiguration.DIMPOWER_WARN3) * 0.3f + 0.2f;
        } else if (power < PowerConfiguration.DIMPOWER_WARN1) {
            factor = (float) (power - PowerConfiguration.DIMPOWER_WARN2) / (PowerConfiguration.DIMPOWER_WARN1 - PowerConfiguration.DIMPOWER_WARN2) * 0.3f + 0.5f;
        } else if (power < PowerConfiguration.DIMPOWER_WARN0) {
            factor = (float) (power - PowerConfiguration.DIMPOWER_WARN1) / (PowerConfiguration.DIMPOWER_WARN0 - PowerConfiguration.DIMPOWER_WARN1) * 0.2f + 0.8f;
        }
        return factor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1) {
        getDimensionInformation();
        if (dimensionInformation == null) {
            return super.getSunBrightness(par1);
        }
        int dim = getDimension();
        float factor = calculatePowerBlackout(dim);
        return super.getSunBrightness(par1) * dimensionInformation.getSkyDescriptor().getSunBrightnessFactor() * factor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1) {
        getDimensionInformation();
        if (dimensionInformation == null) {
            return super.getStarBrightness(par1);
        }
        return super.getStarBrightness(par1) * dimensionInformation.getSkyDescriptor().getStarBrightnessFactor();
    }

    @Override
    public void updateWeather() {
        super.updateWeather();
        if (!world.isRemote) {
            getDimensionInformation();
            if (dimensionInformation != null) {
                WeatherDescriptor descriptor = dimensionInformation.getWeatherDescriptor();
                float rs = descriptor.getRainStrength();
                if (rs > -0.5f) {
                    world.rainingStrength = rs;
                    if (Math.abs(world.rainingStrength) < 0.001) {
                        world.prevRainingStrength = 0;
                        world.rainingStrength = 0;
                        world.getWorldInfo().setRaining(false);
                    }
                }

                float ts = descriptor.getThunderStrength();
                if (ts > -0.5f) {
                    world.thunderingStrength = ts;
                    if (Math.abs(world.thunderingStrength) < 0.001) {
                        world.prevThunderingStrength = 0;
                        world.thunderingStrength = 0;
                        world.getWorldInfo().setThundering(false);
                    }
                }
            }
        }
    }

    @Override
    public float calculateCelestialAngle(long time, float dt) {
        getDimensionInformation();
        if (dimensionInformation == null) {
            return super.calculateCelestialAngle(time, dt);
        }

        if (!dimensionInformation.getTerrainType().hasSky()) {
            return 0.5F;
        }

        if (dimensionInformation.getCelestialAngle() == null) {
            if (dimensionInformation.getTimeSpeed() == null) {
                return super.calculateCelestialAngle(time, dt);
            } else {
                return super.calculateCelestialAngle((long) (time * dimensionInformation.getTimeSpeed()), dt);
            }
        } else {
            return dimensionInformation.getCelestialAngle();
        }
    }

    //------------------------ RFToolsWorldProvider


    @Override
    public int getCurrentRF() {
//        DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(worldObj);
        return getStorage().getEnergyLevel(getDimension());
    }
}
