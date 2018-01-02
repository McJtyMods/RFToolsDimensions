package mcjty.rftoolsdim.dimensions.world;

import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.config.OresAPlentyConfiguration;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.MobDescriptor;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.types.StructureType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import mcjty.rftoolsdim.dimensions.world.mapgen.*;
import mcjty.rftoolsdim.dimensions.world.terrain.*;
import mcjty.rftoolsdim.dimensions.world.terrain.lost.LostCitiesTerrainGenerator;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

public class GenericChunkGenerator implements IChunkGenerator {

    public Random rand;
    public long seed;

    public World worldObj;
    public DimensionInformation dimensionInformation;
    public WorldType worldType;
    private final BaseTerrainGenerator terrainGenerator;

    private ChunkGeneratorSettings settings = null;

    public Biome[] biomesForGeneration;

    private MapGenBase caveGenerator = new MapGenCaves();

    // RFTools specific features.
    private MapGenTendrils tendrilGenerator = new MapGenTendrils(this);
    private MapGenCanyons canyonGenerator = new MapGenCanyons(this);
    private MapGenPyramids pyramidGenerator = new MapGenPyramids(this);
    private MapGenOrbs sphereGenerator = new MapGenOrbs(this, false);
    private MapGenOrbs hugeSphereGenerator = new MapGenOrbs(this, true);
    private MapGenScatteredOrbs scatteredSphereGenerator = new MapGenScatteredOrbs(this);
    private MapGenRuinedCities ruinedCitiesGenerator = new MapGenRuinedCities(this);
    private MapGenLiquidOrbs liquidSphereGenerator = new MapGenLiquidOrbs(this, false);
    private MapGenLiquidOrbs hugeLiquidSphereGenerator = new MapGenLiquidOrbs(this, true);
    private MapGenBase denseCaveGenerator = new MapGenDenseCaves(this);

    private WorldGenerator coalGen = new WorldGenMinable(Blocks.COAL_ORE.getDefaultState(), OresAPlentyConfiguration.coal.getSize());
    private WorldGenerator ironGen = new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(), OresAPlentyConfiguration.iron.getSize());
    private WorldGenerator goldGen = new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(), OresAPlentyConfiguration.gold.getSize());
    private WorldGenerator redstoneGen = new WorldGenMinable(Blocks.REDSTONE_ORE.getDefaultState(), OresAPlentyConfiguration.redstone.getSize());
    private WorldGenerator diamondGen = new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), OresAPlentyConfiguration.diamond.getSize());
    private WorldGenerator lapisGen = new WorldGenMinable(Blocks.LAPIS_ORE.getDefaultState(), OresAPlentyConfiguration.lapis.getSize());
    private WorldGenerator emeraldGen = new WorldGenMinable(Blocks.EMERALD_ORE.getDefaultState(), OresAPlentyConfiguration.emerald.getSize());

    private MapGenStronghold strongholdGenerator = new MapGenStronghold() {
        @Override
        protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
            return canSpawnStructureIgnoringBiomes(chunkX, chunkZ, 10387313);
        }
    };
    private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument() {
        @Override
        protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
            return canSpawnStructureIgnoringBiomes(chunkX, chunkZ, 10387313);
        }
    };
    private MapGenVillage villageGenerator = new MapGenVillage() {
        @Override
        protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
            return canSpawnStructureIgnoringBiomes(chunkX, chunkZ, 10387312);
        }
    };
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
    private MapGenSwampHut genSwampHut = new MapGenSwampHut();
    private MapGenDesertTemple genDesertTemple = new MapGenDesertTemple();
    private MapGenJungleTemple genJungleTemple = new MapGenJungleTemple();
    private MapGenIgloo genIgloo = new MapGenIgloo();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature() {
        @Override
        protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
            return canSpawnStructureIgnoringBiomes(chunkX, chunkZ, 14357617);
        }

        @Override
        protected StructureStart getStructureStart(int chunkX, int chunkZ) {
            StructureStart start = super.getStructureStart(chunkX, chunkZ);
            if (start.getComponents().isEmpty()) {
                switch (super.rand.nextInt(4)) {
                    case 0:
                        start.getComponents().add(new ComponentScatteredFeaturePieces.SwampHut(super.rand, chunkX * 16, chunkZ * 16));
                        break;
                    case 1:
                        start.getComponents().add(new ComponentScatteredFeaturePieces.Igloo(super.rand, chunkX * 16, chunkZ * 16));
                        break;
                    case 2:
                        start.getComponents().add(new ComponentScatteredFeaturePieces.DesertPyramid(super.rand, chunkX * 16, chunkZ * 16));
                        break;
                    case 3:
                        start.getComponents().add(new ComponentScatteredFeaturePieces.JunglePyramid(super.rand, chunkX * 16, chunkZ * 16));
                        break;
                }
                start.updateBoundingBox();
            }
            return start;
        }
    };

    private boolean canSpawnStructureIgnoringBiomes(int chunkX, int chunkZ, int randseed) {
        int i = chunkX;
        int j = chunkZ;

        int distance = 16;
        int seperation = 8;

        if (chunkX < 0) {
            chunkX -= distance - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= distance - 1;
        }

        int k = chunkX / distance;
        int l = chunkZ / distance;
        Random random = this.worldObj.setRandomSeed(k, l, randseed);
        k = k * distance;
        l = l * distance;
        k = k + random.nextInt(distance - seperation);
        l = l + random.nextInt(distance - seperation);

        if (i == k && j == l) {
            return true;
        }

        return false;
    }


    // Holds ravine generator
    private MapGenBase ravineGenerator = new MapGenRavine();

    public ChunkGeneratorSettings getSettings() {
        if (settings == null) {
            System.out.println("dimensionInformation = " + dimensionInformation);
            ChunkGeneratorSettings.Factory factory = new ChunkGeneratorSettings.Factory();
            factory.lapisCount *= 5;
            factory.lapisSize *= 5;
            factory.diamondCount *= 5;
            factory.diamondSize *= 5;
            settings = factory.build();
        }
        return settings;
    }


    public GenericChunkGenerator(World world, long seed) {

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
            oceanMonumentGenerator = (StructureOceanMonument) net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(oceanMonumentGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.OCEAN_MONUMENT);
        }


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
//        System.out.println("GenericChunkGenerator: seed = " + seed);
        this.rand = new Random((seed + 516) * 314);
//        this.rand = new Random(seed);

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
            case TERRAIN_INVERTIGO:
                terrainGenerator = new UpsideDownTerrainGenerator();
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
            case TERRAIN_LOSTCITIES:
                terrainGenerator = new LostCitiesTerrainGenerator();
                break;
            default:
                terrainGenerator = new VoidTerrainGenerator();
                break;
        }

        terrainGenerator.setup(world, this);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();

        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
        terrainGenerator.generate(chunkX, chunkZ, chunkprimer);
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
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
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_SCATTEREDORBS)) {
            this.scatteredSphereGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
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
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SWAMPHUT)) {
            this.genSwampHut.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_DESERTTEMPLE)) {
            this.genDesertTemple.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_JUNGLETEMPLE)) {
            this.genJungleTemple.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_IGLOO)) {
            this.genIgloo.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_OCEAN_MONUMENT)) {
            this.oceanMonumentGenerator.generate(this.worldObj, chunkX, chunkZ, chunkprimer);
        }

//        this.ruinedCitiesGenerator.generate(this.worldObj, chunkX, chunkZ, ablock, abyte);

        if (dimensionInformation.getTerrainType() == TerrainType.TERRAIN_INVERTIGO) {
            reverse(chunkprimer);
        }

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i) {
            abyte[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
        }

        chunk.generateSkylightMap();
//        if (dimensionInformation.getTerrainType() == TerrainType.TERRAIN_INVERTIGO) {
//            ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
//            for (int x = 0 ; x < 16 ; x++) {
//                for (int z = 0 ; z < 16 ; z++) {
//                    if (worldObj.provider.hasSkyLight()) {
//                    }
//                }
//            }
//        }

        return chunk;
    }

    private static void reverse(ChunkPrimer primer) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int idx = (x << 12 | z << 8);
                for (int y = 0; y < 64; y++) {
                    char c = primer.data[idx + y];
                    c = findSuitableReplacement(c);
                    char c2 = primer.data[idx + 127 - y];
                    c2 = findSuitableReplacement(c2);
                    primer.data[idx + y] = c2;
                    primer.data[idx + 127 - y] = c;
                }
                for (int y = 128; y < 255; y++) {
                    primer.data[idx + y] = 0;
                }
            }
        }
    }

    private static char findSuitableReplacement(char c) {
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(c);
        Block block = iblockstate.getBlock();
        if (block instanceof BlockLiquid) {
            c = (char) Block.BLOCK_STATE_IDS.get(ModBlocks.fakeWaterBlock.getDefaultState());
        } else if (block instanceof BlockGravel) {
            c = (char) Block.BLOCK_STATE_IDS.get(ModBlocks.fakeGravelBlock.getDefaultState());
        } else if (block instanceof BlockSand) {
            c = (char) Block.BLOCK_STATE_IDS.get(ModBlocks.fakeSandBlock.getDefaultState());
        } else if (block == Blocks.BEDROCK) {
            c = (char) Block.BLOCK_STATE_IDS.get(Blocks.STONE.getDefaultState());
        } else if (block instanceof BlockFalling) {
            c = 0;
        }
        return c;
    }

    private static UpsidedownWorld upsidedownWorld = null;

    @Override
    public void populate(int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        int x = chunkX * 16;
        int z = chunkZ * 16;
        World w = this.worldObj;
        Biome Biome = w.getBiomeForCoordsBody(new BlockPos(x + 16, 0, z + 16));
        this.rand.setSeed(w.getSeed());
        long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * i1 + chunkZ * j1 ^ w.getSeed());
        boolean flag = false;

        if (dimensionInformation.getTerrainType() == TerrainType.TERRAIN_INVERTIGO) {
            if (upsidedownWorld == null) {
                WorldServer ww = (WorldServer) worldObj;
                upsidedownWorld = new UpsidedownWorld(ww);
                net.minecraftforge.common.DimensionManager.setWorld(ww.provider.getDimension(), ww, ww.getMinecraftServer());
            }
            upsidedownWorld.worldObj = (WorldServer) worldObj;
            w = upsidedownWorld;
        }

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(this, w, rand, chunkX, chunkZ, flag));

        ChunkPos cp = new ChunkPos(chunkX, chunkZ);

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_MINESHAFT)) {
            this.mineshaftGenerator.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_VILLAGE)) {
            flag = this.villageGenerator.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_STRONGHOLD)) {
            this.strongholdGenerator.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_FORTRESS)) {
            this.genNetherBridge.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SCATTERED)) {
            this.scatteredFeatureGenerator.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SWAMPHUT)) {
            this.genSwampHut.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_DESERTTEMPLE)) {
            this.genDesertTemple.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_JUNGLETEMPLE)) {
            this.genJungleTemple.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_IGLOO)) {
            this.genIgloo.generateStructure(w, this.rand, cp);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_OCEAN_MONUMENT)) {
            this.oceanMonumentGenerator.generateStructure(w, this.rand, cp);
        }

        int k1;
        int l1;
        int i2;

        if (dimensionInformation.getTerrainType() != TerrainType.TERRAIN_INVERTIGO) {
            if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_LAKES)) {
                if (dimensionInformation.getFluidsForLakes().length == 0) {
                    // No specific liquid dimlets specified: we generate default lakes (water and lava were appropriate).
                    if (Biome != Biomes.DESERT && Biome != Biomes.DESERT_HILLS && !flag && this.rand.nextInt(4) == 0
                            && TerrainGen.populate(this, w, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.LAKE)) {
                        k1 = x + this.rand.nextInt(16) + 8;
                        l1 = this.rand.nextInt(256);
                        i2 = z + this.rand.nextInt(16) + 8;
                        (new WorldGenLakes(Blocks.WATER)).generate(w, this.rand, new BlockPos(k1, l1, i2));
                    }

                    if (TerrainGen.populate(this, w, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.LAVA) && !flag && this.rand.nextInt(8) == 0) {
                        k1 = x + this.rand.nextInt(16) + 8;
                        l1 = this.rand.nextInt(this.rand.nextInt(248) + 8);
                        i2 = z + this.rand.nextInt(16) + 8;

                        if (l1 < 63 || this.rand.nextInt(10) == 0) {
                            (new WorldGenLakes(Blocks.LAVA)).generate(w, this.rand, new BlockPos(k1, l1, i2));
                        }
                    }
                } else {
                    // Generate lakes for the specified biomes.
                    for (Block liquid : dimensionInformation.getFluidsForLakes()) {
                        if (!flag && this.rand.nextInt(4) == 0
                                && TerrainGen.populate(this, w, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.LAKE)) {
                            k1 = x + this.rand.nextInt(16) + 8;
                            l1 = this.rand.nextInt(256);
                            i2 = z + this.rand.nextInt(16) + 8;
                            (new WorldGenLakes(liquid)).generate(w, this.rand, new BlockPos(k1, l1, i2));
                        }
                    }
                }
            }
        }

        boolean doGen = false;
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_DUNGEON)) {
            doGen = TerrainGen.populate(this, w, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.DUNGEON);
            for (k1 = 0; doGen && k1 < 8; ++k1) {
                l1 = x + this.rand.nextInt(16) + 8;
                i2 = this.rand.nextInt(256);
                int j2 = z + this.rand.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(w, this.rand, new BlockPos(l1, i2, j2));
            }
        }

        BlockPos pos = new BlockPos(x, 0, z);
        Biome.decorate(w, this.rand, pos);

        // OresAPlenty
        if (dimensionInformation.hasFeatureType(FeatureType.FEATURE_ORESAPLENTY)) {
            generateOre(w, this.rand, coalGen, OreGenEvent.GenerateMinable.EventType.COAL, pos, OresAPlentyConfiguration.coal);
            generateOre(w, this.rand, ironGen, OreGenEvent.GenerateMinable.EventType.IRON, pos, OresAPlentyConfiguration.iron);
            generateOre(w, this.rand, goldGen, OreGenEvent.GenerateMinable.EventType.GOLD, pos, OresAPlentyConfiguration.gold);
            generateOre(w, this.rand, lapisGen, OreGenEvent.GenerateMinable.EventType.LAPIS, pos, OresAPlentyConfiguration.lapis);
            generateOre(w, this.rand, diamondGen, OreGenEvent.GenerateMinable.EventType.DIAMOND, pos, OresAPlentyConfiguration.diamond);
            generateOre(w, this.rand, redstoneGen, OreGenEvent.GenerateMinable.EventType.REDSTONE, pos, OresAPlentyConfiguration.redstone);
            generateOre(w, this.rand, emeraldGen, OreGenEvent.GenerateMinable.EventType.EMERALD, pos, OresAPlentyConfiguration.emerald);
        }

        if (TerrainGen.populate(this, w, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            WorldEntitySpawner.performWorldGenSpawning(w, Biome, x + 8, z + 8, 16, 16, this.rand);
        }
        x += 8;
        z += 8;

        doGen = TerrainGen.populate(this, w, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.ICE);
        for (k1 = 0; doGen && k1 < 16; ++k1) {
            for (l1 = 0; l1 < 16; ++l1) {
                i2 = w.getPrecipitationHeight(new BlockPos(x + k1, 0, z + l1)).getY();

                if (w.canBlockFreeze(new BlockPos(k1 + x, i2 - 1, l1 + z), false)) {
                    w.setBlockState(new BlockPos(k1 + x, i2 - 1, l1 + z), Blocks.ICE.getDefaultState(), 2);
                }

                if (w.canSnowAt(new BlockPos(k1 + x, i2, l1 + z), true)) {
                    w.setBlockState(new BlockPos(k1 + x, i2, l1 + z), Blocks.SNOW_LAYER.getDefaultState(), 2);
                }
            }
        }

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(this, w, rand, chunkX, chunkZ, flag));

        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        boolean flag = false;

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_OCEAN_MONUMENT) && chunkIn.getInhabitedTime() < 3600L) {
            flag |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkPos(x, z));
        }

        return flag;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        List<Biome.SpawnListEntry> creatures = getDefaultCreatures(creatureType, pos);
        if (dimensionInformation.getExtraMobs().isEmpty() || worldObj == null) {        // null pointer protection here
            return creatures;
        }

        Class<?> creatureTypeClass;
        switch(creatureType) {
        case AMBIENT:
            creatureTypeClass = IAnimals.class;
            break;
        case MONSTER:
            creatureTypeClass = IMob.class;
            break;
        default:
            return creatures;
        }

        creatures = new ArrayList<>(creatures);
        for (MobDescriptor mob : dimensionInformation.getExtraMobs()) {
            Class<? extends EntityLiving> entityClass = mob.entityClass;
            if (creatureTypeClass.isAssignableFrom(entityClass) && worldObj.countEntities(entityClass) < mob.getMaxLoaded()) {
                creatures.add(mob);
            }
        }

        return creatures;
    }

    private List<Biome.SpawnListEntry> getDefaultCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome Biome = this.worldObj.getBiomeForCoordsBody(pos);
        if (creatureType == EnumCreatureType.MONSTER) {
            if (dimensionInformation.isPeaceful()) {
                return Collections.emptyList();
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SCATTERED)) {
                if (this.scatteredFeatureGenerator.isInsideStructure(pos)) {
                    return this.scatteredFeatureGenerator.getMonsters();
                }
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SWAMPHUT)) {
                if (this.genSwampHut.func_175798_a(pos)) {
                    return this.genSwampHut.getScatteredFeatureSpawnList();
                }
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_DESERTTEMPLE)) {
                if (this.genDesertTemple.func_175798_a(pos)) {
                    return this.genDesertTemple.getScatteredFeatureSpawnList();
                }
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_JUNGLETEMPLE)) {
                if (this.genJungleTemple.func_175798_a(pos)) {
                    return this.genJungleTemple.getScatteredFeatureSpawnList();
                }
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_IGLOO)) {
                if (this.genIgloo.func_175798_a(pos)) {
                    return this.genIgloo.getScatteredFeatureSpawnList();
                }
            }

            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_FORTRESS)) {
                if (this.genNetherBridge.isInsideStructure(pos)) {
                    return this.genNetherBridge.getSpawnList();
                }

                if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICK) {
                    return this.genNetherBridge.getSpawnList();
                }
            }
            if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_OCEAN_MONUMENT) && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
                return this.oceanMonumentGenerator.getMonsters();
            }
        } else if (creatureType == EnumCreatureType.AMBIENT) {
            if (dimensionInformation.isNoanimals()) {
                return Collections.emptyList();
            }
        }

        return Biome.getSpawnableList(creatureType);
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_FORTRESS)) {
            this.genNetherBridge.generate(this.worldObj, x, z, null);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_MINESHAFT)) {
            this.mineshaftGenerator.generate(this.worldObj, x, z, null);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_VILLAGE)) {
            this.villageGenerator.generate(this.worldObj, x, z, null);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_STRONGHOLD)) {
            this.strongholdGenerator.generate(this.worldObj, x, z, null);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SCATTERED)) {
            this.scatteredFeatureGenerator.generate(this.worldObj, x, z, null);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_SWAMPHUT)) {
            this.genSwampHut.generate(this.worldObj, x, z, null);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_DESERTTEMPLE)) {
            this.genDesertTemple.generate(this.worldObj, x, z, null);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_JUNGLETEMPLE)) {
            this.genJungleTemple.generate(this.worldObj, x, z, null);
        }
        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_IGLOO)) {
            this.genIgloo.generate(this.worldObj, x, z, null);
        }

        if (dimensionInformation.hasStructureType(StructureType.STRUCTURE_OCEAN_MONUMENT)) {
            this.oceanMonumentGenerator.generate(this.worldObj, x, z, null);
        }
    }

    private static void generateOre(World w, Random rand, WorldGenerator gen, OreGenEvent.GenerateMinable.EventType type, BlockPos pos, OresAPlentyConfiguration.Settings settings) {
        if (settings.getCount() > 0) {
            if (TerrainGen.generateOre(w, rand, gen, pos, type)) {
                genStandardOre1(w, rand, settings.getCount(), gen, settings.getMin(), settings.getMax(), pos);
            }
        }
    }

    private static void genStandardOre1(World worldIn, Random random, int blockCount, WorldGenerator generator, int minHeight, int maxHeight, BlockPos chunkPos) {
        if (maxHeight < minHeight) {
            int i = minHeight;
            minHeight = maxHeight;
            maxHeight = i;
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight;
            } else {
                --minHeight;
            }
        }

        for (int j = 0; j < blockCount; ++j) {
            BlockPos blockpos = chunkPos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16));
            generator.generate(worldIn, random, blockpos);
        }
    }

    private static void genStandardOre2(World worldIn, Random random, int blockCount, WorldGenerator generator, int centerHeight, int spread, BlockPos chunkPos) {
        for (int i = 0; i < blockCount; ++i) {
            BlockPos blockpos = chunkPos.add(random.nextInt(16), random.nextInt(spread) + random.nextInt(spread) + centerHeight - spread, random.nextInt(16));
            generator.generate(worldIn, random, blockpos);
        }
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
//        } else if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
//            return this.woodlandMansionGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        } else if ("Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        } else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        } else {
            return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored) : null;
        }
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.isInsideStructure(pos);
//        } else if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
//            return this.woodlandMansionGenerator.isInsideStructure(pos);
        } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.isInsideStructure(pos);
        } else if ("Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.isInsideStructure(pos);
        } else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.isInsideStructure(pos);
        } else {
            return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.isInsideStructure(pos) : false;
        }
    }
}
