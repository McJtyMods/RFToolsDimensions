package mcjty.rftoolsdim.dimensions.world;

import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.MobDescriptor;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.types.StructureType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import mcjty.rftoolsdim.dimensions.world.mapgen.*;
import mcjty.rftoolsdim.dimensions.world.terrain.*;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Blocks;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

public class GenericChunkProvider implements IChunkProvider {
    public Random rand;
    public long seed;

    private World worldObj;
    public DimensionInformation dimensionInformation;
    private List<BiomeGenBase.SpawnListEntry> extraSpawns;
    private List<Integer> extraSpawnsMax;

    // @todo, examine and consider customizing
    private ChunkProviderSettings settings = new ChunkProviderSettings.Factory().func_177864_b();

    private final BaseTerrainGenerator terrainGenerator;

    // Are map structures going to be generated (e.g. strongholds)
    public WorldType worldType;

    private MapGenBase caveGenerator = new MapGenCaves();

    // RFTools specific features.
    private MapGenTendrils tendrilGenerator = new MapGenTendrils(this);
    private MapGenCanyons canyonGenerator = new MapGenCanyons(this);
    private MapGenPyramids pyramidGenerator = new MapGenPyramids(this);
    private MapGenOrbs sphereGenerator = new MapGenOrbs(this, false);
    private MapGenOrbs hugeSphereGenerator = new MapGenOrbs(this, true);
    private MapGenRuinedCities ruinedCitiesGenerator = new MapGenRuinedCities(this);
    private MapGenLiquidOrbs liquidSphereGenerator = new MapGenLiquidOrbs(this, false);
    private MapGenLiquidOrbs hugeLiquidSphereGenerator = new MapGenLiquidOrbs(this, true);
    private MapGenBase denseCaveGenerator = new MapGenDenseCaves(this);

    // Holds Stronghold Generator
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();

    // Holds Village Generator
    private MapGenVillage villageGenerator = new MapGenVillage();

    // Holds Mineshaft Generator
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

    // For nether fortresses
    public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();

    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

    // Holds ravine generator
    private MapGenBase ravineGenerator = new MapGenRavine();

    // The biomes that are used to generate the chunk
    public BiomeGenBase[] biomesForGeneration;

    {
        caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
//        tendrilGenerator = TerrainGen.getModdedMapGen(tendrilGenerator, CAVE);
//        canyonGenerator = TerrainGen.getModdedMapGen(canyonGenerator, RAVINE);
//        sphereGenerator = TerrainGen.getModdedMapGen(sphereGenerator, RAVINE);
        strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, STRONGHOLD);
        villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, VILLAGE);
        mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator, SCATTERED_FEATURE);
        ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
        genNetherBridge = (MapGenNetherBridge) TerrainGen.getModdedMapGen(genNetherBridge, NETHER_BRIDGE);
    }

    public ChunkProviderSettings getSettings() {
        return settings;
    }

    public final LongHashMap<Chunk> id2ChunkMap = new LongHashMap();
    private final Set<Long> droppedChunksSet = Collections.<Long>newSetFromMap(new ConcurrentHashMap());

    @Override
    public Chunk getLoadedChunk(int x, int z) {
        long i = ChunkCoordIntPair.chunkXZ2Int(x, z);
        Chunk chunk = this.id2ChunkMap.getValueByKey(i);
        this.droppedChunksSet.remove(Long.valueOf(i));
        return chunk;
    }

    public GenericChunkProvider(World world, long seed) {
        this.worldObj = world;

        dimensionInformation = RfToolsDimensionManager.getDimensionManager(world).getDimensionInformation(world.provider.getDimension());

        this.worldType = world.getWorldInfo().getTerrainType();

        if (dimensionInformation.getTerrainType() == TerrainType.TERRAIN_AMPLIFIED) {
            worldType = WorldType.AMPLIFIED;
        } else if (dimensionInformation.getTerrainType() == TerrainType.TERRAIN_NORMAL && !WorldgenConfiguration.normalTerrainInheritsOverworld) {
            worldType = WorldType.DEFAULT;
        } else if (dimensionInformation.getTerrainType() == TerrainType.TERRAIN_FLAT) {
            worldType = WorldType.FLAT;
        }

        this.seed = seed;
        this.rand = new Random((seed + 516) * 314);

        switch (dimensionInformation.getTerrainType()) {
            case TERRAIN_VOID:
                terrainGenerator = new VoidTerrainGenerator();
                break;
            case TERRAIN_FLAT:
                terrainGenerator = new FlatTerrainGenerator((byte) 63);
                break;
            case TERRAIN_AMPLIFIED:
                terrainGenerator = new AmplifiedTerrainGenerator();
                break;
            case TERRAIN_NEARLANDS:
                terrainGenerator = new NearlandsTerrainGenerator();
                break;
            case TERRAIN_NORMAL:
                terrainGenerator = new NormalTerrainGenerator();
                break;
            case TERRAIN_ISLAND:
                terrainGenerator = new IslandTerrainGenerator(IslandTerrainGenerator.NORMAL);
                break;
            case TERRAIN_ISLANDS:
                terrainGenerator = new IslandTerrainGenerator(IslandTerrainGenerator.ISLANDS);
                break;
            case TERRAIN_CHAOTIC:
                terrainGenerator = new IslandTerrainGenerator(IslandTerrainGenerator.CHAOTIC);
                break;
            case TERRAIN_PLATEAUS:
                terrainGenerator = new IslandTerrainGenerator(IslandTerrainGenerator.PLATEAUS);
                break;
            case TERRAIN_GRID:
                terrainGenerator = new GridTerrainGenerator();
                break;
            case TERRAIN_CAVERN:
                terrainGenerator = new CavernTerrainGenerator(null);
                break;
            case TERRAIN_LOW_CAVERN:
                terrainGenerator = new CavernTerrainGenerator(CavernTerrainGenerator.CavernHeight.HEIGHT_128);
                break;
            case TERRAIN_FLOODED_CAVERN:
                terrainGenerator = new CavernTerrainGenerator(CavernTerrainGenerator.CavernHeight.HEIGHT_128);
                break;
            case TERRAIN_LIQUID:
                terrainGenerator = new LiquidTerrainGenerator();
                break;
            case TERRAIN_SOLID:
                terrainGenerator = new FlatTerrainGenerator((byte) 127);
                break;
            case TERRAIN_WAVES:
                terrainGenerator = new WavesTerrainGenerator(false);
                break;
            case TERRAIN_FILLEDWAVES:
                terrainGenerator = new WavesTerrainGenerator(true);
                break;
            case TERRAIN_ROUGH:
                terrainGenerator = new RoughTerrainGenerator(false);
                break;
            default:
                terrainGenerator = new VoidTerrainGenerator();
                break;
        }

        terrainGenerator.setup(world, this);

        extraSpawns = new ArrayList<>();
        extraSpawnsMax = new ArrayList<>();
        for (MobDescriptor mob : dimensionInformation.getExtraMobs()) {
            Class<? extends Entity> entityClass = mob.getEntityClass();
            extraSpawns.add(new BiomeGenBase.SpawnListEntry((Class<? extends EntityLiving>) entityClass, mob.getSpawnChance(), mob.getMinGroup(), mob.getMaxGroup()));
            extraSpawnsMax.add(mob.getMaxLoaded());
        }

    }

//    public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
//        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
//        this.func_147423_a(chunkX * 4, 0, chunkZ * 4);
//
//        for (int i = 0; i < 4; ++i) {
//            int j = i * 5;
//            int k = (i + 1) * 5;
//
//            for (int l = 0; l < 4; ++l) {
//                int i1 = (j + l) * 33;
//                int j1 = (j + l + 1) * 33;
//                int k1 = (k + l) * 33;
//                int l1 = (k + l + 1) * 33;
//
//                for (int i2 = 0; i2 < 32; ++i2) {
//                    double d0 = 0.125D;
//                    double d1 = this.field_147434_q[i1 + i2];
//                    double d2 = this.field_147434_q[j1 + i2];
//                    double d3 = this.field_147434_q[k1 + i2];
//                    double d4 = this.field_147434_q[l1 + i2];
//                    double d5 = (this.field_147434_q[i1 + i2 + 1] - d1) * d0;
//                    double d6 = (this.field_147434_q[j1 + i2 + 1] - d2) * d0;
//                    double d7 = (this.field_147434_q[k1 + i2 + 1] - d3) * d0;
//                    double d8 = (this.field_147434_q[l1 + i2 + 1] - d4) * d0;
//
//                    for (int j2 = 0; j2 < 8; ++j2) {
//                        double d9 = 0.25D;
//                        double d10 = d1;
//                        double d11 = d2;
//                        double d12 = (d3 - d1) * d9;
//                        double d13 = (d4 - d2) * d9;
//
//                        for (int k2 = 0; k2 < 4; ++k2) {
//                            double d14 = 0.25D;
//                            double d16 = (d11 - d10) * d14;
//                            double lvt_45_1_ = d10 - d16;
//
//                            for (int l2 = 0; l2 < 4; ++l2) {
//                                if ((lvt_45_1_ += d16) > 0.0D) {
//                                    primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, Blocks.stone.getDefaultState());
//                                } else if (i2 * 8 + j2 < this.settings.seaLevel) {
//                                    primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.field_177476_s.getDefaultState());
//                                }
//                            }
//
//                            d10 += d12;
//                            d11 += d13;
//                        }
//
//                        d1 += d5;
//                        d2 += d6;
//                        d3 += d7;
//                        d4 += d8;
//                    }
//                }
//            }
//        }
//    }
//


    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L + 123456);

        ChunkPrimer chunkprimer = new ChunkPrimer();
//        this.setBlocksInChunk(chunkX, chunkZ, chunkprimer);
//        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);

        terrainGenerator.generate(chunkX, chunkZ, chunkprimer);
//        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
        terrainGenerator.replaceBlocksForBiome(chunkX, chunkZ, chunkprimer, this.biomesForGeneration);

        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_TENDRILS)) {
            this.tendrilGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_CANYONS)) {
            this.canyonGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_PYRAMIDS)) {
            this.pyramidGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_ORBS)) {
            this.sphereGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_HUGEORBS)) {
            this.hugeSphereGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_LIQUIDORBS)) {
            this.liquidSphereGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_HUGELIQUIDORBS)) {
            this.hugeLiquidSphereGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_CAVES)) {
            this.caveGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_DENSE_CAVES)) {
            this.denseCaveGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_RAVINES)) {
            this.ravineGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_MINESHAFT)) {
            this.mineshaftGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_VILLAGE)) {
            this.villageGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_STRONGHOLD)) {
            this.strongholdGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_FORTRESS)) {
            this.genNetherBridge.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SCATTERED)) {
            this.scatteredFeatureGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }

//        this.ruinedCitiesGenerator.generate(this.worldObj, chunkX, chunkZ, ablock, abyte);

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
        byte[] abyte1 = chunk.getBiomeArray();

        //@todo
//        for (int k = 0; k < abyte1.length; ++k) {
//            abyte1[k] = (byte) this.biomesForGeneration[k].biomeID;
//        }

        chunk.generateSkylightMap();

        return chunk;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        int x = chunkX * 16;
        int z = chunkZ * 16;
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(x + 16, 0, z + 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * i1 + chunkZ * j1 ^ this.worldObj.getSeed());
        boolean flag = false;

        //@todo
//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, rand, chunkX, chunkZ, flag));

        ChunkCoordIntPair cp = new ChunkCoordIntPair(chunkX, chunkZ);

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_MINESHAFT)) {
            this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_VILLAGE)) {
            flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_STRONGHOLD)) {
            this.strongholdGenerator.generateStructure(this.worldObj, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_FORTRESS)) {
            this.genNetherBridge.generateStructure(this.worldObj, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SCATTERED)) {
            this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, cp);
        }

        int k1;
        int l1;
        int i2;


        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_LAKES)) {
            if (dimensionInformation.getFluidsForLakes().length == 0) {
                // No specific liquid dimlets specified: we generate default lakes (water and lava were appropriate).
//                if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && !flag && this.rand.nextInt(4) == 0
//                        && TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, flag, LAKE)) {
//                    k1 = x + this.rand.nextInt(16) + 8;
//                    l1 = this.rand.nextInt(256);
//                    i2 = z + this.rand.nextInt(16) + 8;
//                    (new WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand, new BlockPos(k1, l1, i2));
//                }

//                if (TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, flag, LAVA) && !flag && this.rand.nextInt(8) == 0) {
//                    k1 = x + this.rand.nextInt(16) + 8;
//                    l1 = this.rand.nextInt(this.rand.nextInt(248) + 8);
//                    i2 = z + this.rand.nextInt(16) + 8;
//
//                    if (l1 < 63 || this.rand.nextInt(10) == 0) {
//                        (new WorldGenLakes(Blocks.lava)).generate(this.worldObj, this.rand, new BlockPos(k1, l1, i2));
//                    }
//                }
            } else {
                // Generate lakes for the specified biomes.
// @todo
//                for (Block liquid : dimensionInformation.getFluidsForLakes()) {
//                    if (!flag && this.rand.nextInt(4) == 0
//                            && TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, flag, LAKE)) {
//                        k1 = x + this.rand.nextInt(16) + 8;
//                        l1 = this.rand.nextInt(256);
//                        i2 = z + this.rand.nextInt(16) + 8;
//                        (new WorldGenLakes(liquid)).generate(this.worldObj, this.rand, new BlockPos(k1, l1, i2));
//                    }
//                }
            }
        }

        boolean doGen = false;
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_DUNGEON)) {
//@todo
//            doGen = TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, flag, DUNGEON);
//            for (k1 = 0; doGen && k1 < 8; ++k1) {
//                l1 = x + this.rand.nextInt(16) + 8;
//                i2 = this.rand.nextInt(256);
//                int j2 = z + this.rand.nextInt(16) + 8;
//                (new WorldGenDungeons()).generate(this.worldObj, this.rand, new BlockPos(l1, i2, j2));
//            }
        }

        biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(x, 0, z));
//        if (TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, flag, ANIMALS)) {
//            SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, x + 8, z + 8, 16, 16, this.rand);
//        }
        x += 8;
        z += 8;

//        doGen = TerrainGen.populate(chunkProvider, worldObj, rand, chunkX, chunkZ, flag, ICE);
        for (k1 = 0; doGen && k1 < 16; ++k1) {
            for (l1 = 0; l1 < 16; ++l1) {
                i2 = this.worldObj.getPrecipitationHeight(new BlockPos(x + k1, 0, z + l1)).getY();

                if (this.worldObj.canBlockFreeze(new BlockPos(k1 + x, i2 - 1, l1 + z), false)) {
                    this.worldObj.setBlockState(new BlockPos(k1 + x, i2 - 1, l1 + z), Blocks.ice.getDefaultState(), 2);
                }

                if (this.worldObj.canSnowAt(new BlockPos(k1 + x, i2, l1 + z), true)) {
                    this.worldObj.setBlockState(new BlockPos(k1 + x, i2, l1 + z), Blocks.snow_layer.getDefaultState(), 2);
                }
            }
        }

//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, rand, chunkX, chunkZ, flag));

        BlockFalling.fallInstantly = false;
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Converts the instance data to a readable string.
     */
    @Override
    public String makeString() {
        return "RandomLevelSource";
    }



    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
//    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        List creatures = getDefaultCreatures(creatureType, pos);
        if (extraSpawns.isEmpty()) {
            return creatures;
        }

        if (creatureType == EnumCreatureType.AMBIENT) {
            creatures = new ArrayList(creatures);
            for (int i = 0 ; i < extraSpawns.size() ; i++) {
                Class entityClass = extraSpawns.get(i).entityClass;
                if (IAnimals.class.isAssignableFrom(entityClass)) {
                    int count = worldObj.countEntities(entityClass);
                    if (count < extraSpawnsMax.get(i)) {
                        creatures.add(extraSpawns.get(i));
                    }
                }
            }
        } else if (creatureType == EnumCreatureType.MONSTER) {
            creatures = new ArrayList(creatures);
            for (int i = 0 ; i < extraSpawns.size() ; i++) {
                Class entityClass = extraSpawns.get(i).entityClass;
                if (IMob.class.isAssignableFrom(entityClass)) {
                    int count = worldObj.countEntities(entityClass);
                    if (count < extraSpawnsMax.get(i)) {
                        creatures.add(extraSpawns.get(i));
                    }
                }
            }
        }


        return creatures;
    }

    private List getDefaultCreatures(EnumCreatureType creatureType, BlockPos pos) {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        if (creatureType == EnumCreatureType.MONSTER) {
            if (dimensionInformation.isPeaceful()) {
                return Collections.emptyList();
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SCATTERED)) {
                if (this.scatteredFeatureGenerator.func_175798_a(pos)) {
                    return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
                }
            }

            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_FORTRESS)) {
                if (this.genNetherBridge.func_175795_b(pos)) {
                    return this.genNetherBridge.getSpawnList();
                }

                if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick) {
                    return this.genNetherBridge.getSpawnList();
                }
            }
        } else if (creatureType == EnumCreatureType.AMBIENT) {
            if (dimensionInformation.isNoanimals()) {
                return Collections.emptyList();
            }
        }

        return biomegenbase.getSpawnableList(creatureType);
    }
}