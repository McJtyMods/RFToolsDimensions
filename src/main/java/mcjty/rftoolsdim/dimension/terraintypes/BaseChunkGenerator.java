package mcjty.rftoolsdim.dimension.terraintypes;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import mcjty.rftoolsdim.dimension.DimensionInformation;
import mcjty.rftoolsdim.dimension.DimensionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;

import java.util.List;
import java.util.Random;

public abstract class BaseChunkGenerator<T extends GenerationSettings> extends ChunkGenerator<T> {

    private static final float[] MYSTERY_FIELD = Util.make(new float[13824], (p_222557_0_) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    p_222557_0_[i * 24 * 24 + j * 24 + k] = (float) func_222554_b(j - 12, k - 12, i - 12);
                }
            }
        }

    });

    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
    private final PatrolSpawner patrolSpawner = new PatrolSpawner();
    private final CatSpawner catSpawner = new CatSpawner();
    private final VillageSiege villageSiege = new VillageSiege();

    protected final int verticalNoiseGranularity;
    protected final int horizontalNoiseGranularity;
    protected final int noiseSizeX;
    protected final int noiseSizeY;
    protected final int noiseSizeZ;
    protected final SharedSeedRandom randomSeed;
    private final OctavesNoiseGenerator field_222568_o;
    private final OctavesNoiseGenerator field_222569_p;
    private final OctavesNoiseGenerator field_222570_q;
    private final INoiseGenerator surfaceDepthNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;

    public BaseChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn, int horizontalNoiseGranularityIn, int verticalNoiseGranularityIn, int p_i49931_5_, T settingsIn, boolean usePerlin) {
        super(worldIn, biomeProviderIn, settingsIn);
        this.verticalNoiseGranularity = verticalNoiseGranularityIn;
        this.horizontalNoiseGranularity = horizontalNoiseGranularityIn;
        this.defaultBlock = settingsIn.getDefaultBlock();
        this.defaultFluid = settingsIn.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
        this.noiseSizeY = p_i49931_5_ / this.verticalNoiseGranularity;
        this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
        this.randomSeed = new SharedSeedRandom(this.seed);
        this.field_222568_o = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
        this.field_222569_p = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
        this.field_222570_q = new OctavesNoiseGenerator(this.randomSeed, 7, 0);
        this.surfaceDepthNoise = usePerlin ? new PerlinNoiseGenerator(this.randomSeed, 3, 0) : new OctavesNoiseGenerator(this.randomSeed, 3, 0);
    }

    private double generateNoiseHelper(int noiseX, int noiseY, int noiseZ, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 1.0D;

        for (int i = 0; i < 16; ++i) {
            double d4 = OctavesNoiseGenerator.maintainPrecision(noiseX * p_222552_4_ * d3);
            double d5 = OctavesNoiseGenerator.maintainPrecision(noiseY * p_222552_6_ * d3);
            double d6 = OctavesNoiseGenerator.maintainPrecision(noiseZ * p_222552_4_ * d3);
            double d7 = p_222552_6_ * d3;
            ImprovedNoiseGenerator improvednoisegenerator = this.field_222568_o.getOctave(i);
            if (improvednoisegenerator != null) {
                d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, noiseY * d7) / d3;
            }

            ImprovedNoiseGenerator improvednoisegenerator1 = this.field_222569_p.getOctave(i);
            if (improvednoisegenerator1 != null) {
                d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, noiseY * d7) / d3;
            }

            if (i < 8) {
                ImprovedNoiseGenerator improvednoisegenerator2 = this.field_222570_q.getOctave(i);
                if (improvednoisegenerator2 != null) {
                    d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision(noiseX * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision(noiseY * p_222552_10_ * d3), OctavesNoiseGenerator.maintainPrecision(noiseZ * p_222552_8_ * d3), p_222552_10_ * d3, noiseY * p_222552_10_ * d3) / d3;
                }
            }

            d3 /= 2.0D;
        }

        return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
    }

    protected double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
        double[] adouble = new double[this.noiseSizeY + 1];
        this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
        return adouble;
    }

    protected void calcNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ, double p_222546_4_, double p_222546_6_, double p_222546_8_, double p_222546_10_, int p_222546_12_, int p_222546_13_) {
        double[] adouble = this.getBiomeNoiseColumn(noiseX, noiseZ);
        double d0 = adouble[0];
        double d1 = adouble[1];
        double d2 = this.func_222551_g();
        double d3 = this.func_222553_h();

        for (int i = 0; i < this.noiseSizeY(); ++i) {
            double d4 = this.generateNoiseHelper(noiseX, i, noiseZ, p_222546_4_, p_222546_6_, p_222546_8_, p_222546_10_);
            d4 = d4 - this.func_222545_a(d0, d1, i);
            if (i > d2) {
                d4 = MathHelper.clampedLerp(d4, p_222546_13_, (i - d2) / p_222546_12_);
            } else if (i < d3) {
                d4 = MathHelper.clampedLerp(d4, -30.0D, (d3 - i) / (d3 - 1.0D));
            }

            noiseColumn[i] = d4;
        }

    }

    protected void handleIllagerStructures(IWorld worldIn, IChunk chunkIn, ObjectList<AbstractVillagePiece> villagePieces,
                                           ObjectList<JigsawJunction> jigsawJunctions, ChunkPos chunkpos, int x, int z) {
        for (Structure<?> structure : Feature.ILLAGER_STRUCTURES) {
            String s = structure.getStructureName();
            LongIterator longiterator = chunkIn.getStructureReferences(s).iterator();

            while (longiterator.hasNext()) {
                long j1 = longiterator.nextLong();
                ChunkPos chunkpos1 = new ChunkPos(j1);
                IChunk ichunk = worldIn.getChunk(chunkpos1.x, chunkpos1.z);
                StructureStart structurestart = ichunk.getStructureStart(s);
                if (structurestart != null && structurestart.isValid()) {
                    for (StructurePiece structurepiece : structurestart.getComponents()) {
                        if (structurepiece.func_214810_a(chunkpos, 12) && structurepiece instanceof AbstractVillagePiece) {
                            AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece) structurepiece;
                            JigsawPattern.PlacementBehaviour behaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
                            if (behaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                                villagePieces.add(abstractvillagepiece);
                            }

                            for (JigsawJunction jigsawjunction : abstractvillagepiece.getJunctions()) {
                                int sourceX = jigsawjunction.getSourceX();
                                int sourceZ = jigsawjunction.getSourceZ();
                                if (sourceX > x - 12 && sourceZ > z - 12 && sourceX < x + 15 + 12 && sourceZ < z + 15 + 12) {
                                    jigsawJunctions.add(jigsawjunction);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected static final BlockState AIR = Blocks.AIR.getDefaultState();

    @Override
    public void makeBase(IWorld worldIn, IChunk chunkIn) {
        int seaLevel = this.getSeaLevel();
        ObjectList<AbstractVillagePiece> villagePieces = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> jigsawJunctions = new ObjectArrayList<>(32);
        ChunkPos chunkpos = chunkIn.getPos();
        int chunkX = chunkpos.x;
        int chunkZ = chunkpos.z;
        int x = chunkX << 4;
        int z = chunkZ << 4;

        DimensionInformation info = DimensionManager.get(worldIn.getWorld()).getDimensionInformation(worldIn.getWorld());
        List<BlockState> baseBlocks = info.getBaseBlocks();

        handleIllagerStructures(worldIn, chunkIn, villagePieces, jigsawJunctions, chunkpos, x, z);

        ChunkPrimer primer = (ChunkPrimer) chunkIn;
        Heightmap heightmapOceanFloor = primer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmapWorldSurface = primer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        makeBaseInternal(worldIn, seaLevel, villagePieces, jigsawJunctions, chunkX, chunkZ, x, z, baseBlocks, primer, heightmapOceanFloor, heightmapWorldSurface);
    }

    protected abstract void makeBaseInternal(IWorld worldIn, int seaLevel,
                                    ObjectList<AbstractVillagePiece> villagePieces, ObjectList<JigsawJunction> jigsawJunctions,
                                    int chunkX, int chunkZ, int x, int z,
                                    List<BlockState> baseBlocks, ChunkPrimer primer,
                                    Heightmap heightmapOceanFloor, Heightmap heightmapWorldSurface);

    protected double adjustNoise(ObjectList<AbstractVillagePiece> villagePieces, ObjectList<JigsawJunction> jigsawJunctions, int adjustedX, int adjustedY, int adjustedZ, double noise) {
        for (AbstractVillagePiece piece : villagePieces) {
            MutableBoundingBox box = piece.getBoundingBox();
            int xx = Math.max(0, Math.max(box.minX - adjustedX, adjustedX - box.maxX));
            int yy = adjustedY - (box.minY + piece.getGroundLevelDelta());
            int zz = Math.max(0, Math.max(box.minZ - adjustedZ, adjustedZ - box.maxZ));
            noise += mysteriousFunction(xx, yy, zz) * 0.8D;
        }

        for (JigsawJunction junction : jigsawJunctions) {
            int xx = adjustedX - junction.getSourceX();
            int yy = adjustedY - junction.getSourceGroundY();
            int zz = adjustedZ - junction.getSourceZ();
            noise += mysteriousFunction(xx, yy, zz) * 0.4D;
        }
        return noise;
    }

    protected abstract double[] getBiomeNoiseColumn(int noiseX, int noiseZ);

    protected double func_222545_a(double x, double y, int z) {
        double d0 = 8.5D;
        double d1 = (z - (8.5D + x * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / y;
        if (d1 < 0.0D) {
            d1 *= 4.0D;
        }
        return d1;
    }

    protected double func_222551_g() {
        return (this.noiseSizeY() - 4);
    }

    protected double func_222553_h() {
        return 0.0D;
    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
        int i = Math.floorDiv(p_222529_1_, this.horizontalNoiseGranularity);
        int j = Math.floorDiv(p_222529_2_, this.horizontalNoiseGranularity);
        int k = Math.floorMod(p_222529_1_, this.horizontalNoiseGranularity);
        int l = Math.floorMod(p_222529_2_, this.horizontalNoiseGranularity);
        double d0 = (double) k / this.horizontalNoiseGranularity;
        double d1 = (double) l / this.horizontalNoiseGranularity;
        double[][] adouble = new double[][]{this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};
        int i1 = this.getSeaLevel();

        for (int j1 = this.noiseSizeY - 1; j1 >= 0; --j1) {
            double d2 = adouble[0][j1];
            double d3 = adouble[1][j1];
            double d4 = adouble[2][j1];
            double d5 = adouble[3][j1];
            double d6 = adouble[0][j1 + 1];
            double d7 = adouble[1][j1 + 1];
            double d8 = adouble[2][j1 + 1];
            double d9 = adouble[3][j1 + 1];

            for (int k1 = this.verticalNoiseGranularity - 1; k1 >= 0; --k1) {
                double d10 = (double) k1 / this.verticalNoiseGranularity;
                double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int l1 = j1 * this.verticalNoiseGranularity + k1;
                if (d11 > 0.0D || l1 < i1) {
                    BlockState blockstate;
                    if (d11 > 0.0D) {
                        blockstate = this.defaultBlock;
                    } else {
                        blockstate = this.defaultFluid;
                    }

                    if (heightmapType.getHeightLimitPredicate().test(blockstate)) {
                        return l1 + 1;
                    }
                }
            }
        }

        return 0;
    }

    protected abstract void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ);

    public int noiseSizeY() {
        return this.noiseSizeY + 1;
    }

    @Override
    public void func_225551_a_(WorldGenRegion region, IChunk chunk) {
        ChunkPos chunkpos = chunk.getPos();
        int chunkX = chunkpos.x;
        int chunkZ = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setBaseChunkSeed(chunkX, chunkZ);
        ChunkPos chunkpos1 = chunk.getPos();
        int x = chunkpos1.getXStart();
        int z = chunkpos1.getZStart();
        double d0 = 0.0625D;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int cx = 0; cx < 16; ++cx) {
            for (int cz = 0; cz < 16; ++cz) {
                int xx = x + cx;
                int zz = z + cz;
                int yy = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, cx, cz) + 1;
                double d1 = this.surfaceDepthNoise.noiseAt(xx * 0.0625D, zz * 0.0625D, 0.0625D, cx * 0.0625D) * 15.0D;
                region.getBiome(mutablePos.setPos(x + cx, yy, z + cz)).buildSurface(sharedseedrandom, chunk, xx, zz, yy, d1, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
            }
        }

        this.makeBedrock(chunk, sharedseedrandom);
    }

    protected void makeBedrock(IChunk chunkIn, Random rand) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        int x = chunkIn.getPos().getXStart();
        int z = chunkIn.getPos().getZStart();
        T t = this.getSettings();
        int k = t.getBedrockFloorHeight();
        int l = t.getBedrockRoofHeight();

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(x, 0, z, x + 15, 0, z + 15)) {
            if (l > 0) {
                for (int i1 = l; i1 >= l - 4; --i1) {
                    if (i1 >= l - rand.nextInt(5)) {
                        chunkIn.setBlockState(mutablePos.setPos(blockpos.getX(), i1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                    }
                }
            }

            if (k < 256) {
                for (int j1 = k + 4; j1 >= k; --j1) {
                    if (j1 <= k + rand.nextInt(5)) {
                        chunkIn.setBlockState(mutablePos.setPos(blockpos.getX(), j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                    }
                }
            }
        }

    }

    @Override
    public void decorate(WorldGenRegion region) {
        super.decorate(region);

        int chunkX = region.getMainChunkX() * 16;
        int chunkZ = region.getMainChunkZ() * 16;
        BlockPos blockpos = new BlockPos(chunkX, 0, chunkZ);
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), chunkX, chunkZ);

        DimensionInformation info = DimensionManager.get(region.getWorld()).getDimensionInformation(region.getWorld());
        int i = 0;
        for(GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
            try {
                for (ConfiguredFeature<?, ?> configuredFeature : info.getFeatures()) {
                    sharedseedrandom.setFeatureSeed(i1, i, stage.ordinal());
                    configuredFeature.place(region, this, sharedseedrandom, blockpos);
                }
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.makeCrashReport(exception, "Biome decoration");
                crashreport.makeCategory("Generation").addDetail("CenterX", chunkX).addDetail("CenterZ", chunkZ).addDetail("Step", stage).addDetail("Seed", i1);
                throw new ReportedException(crashreport);
            }
            ++i;
        }
    }


    @Override
    public void spawnMobs(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
        this.phantomSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
        this.patrolSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
        this.catSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
        this.villageSiege.func_225477_a(worldIn, spawnHostileMobs, spawnPeacefulMobs);
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification creatureType, BlockPos pos) {
        if (Feature.SWAMP_HUT.func_202383_b(this.world, pos)) {
            if (creatureType == EntityClassification.MONSTER) {
                return Feature.SWAMP_HUT.getSpawnList();
            }

            if (creatureType == EntityClassification.CREATURE) {
                return Feature.SWAMP_HUT.getCreatureSpawnList();
            }
        } else if (creatureType == EntityClassification.MONSTER) {
            if (Feature.PILLAGER_OUTPOST.isPositionInStructure(this.world, pos)) {
                return Feature.PILLAGER_OUTPOST.getSpawnList();
            }

            if (Feature.OCEAN_MONUMENT.isPositionInStructure(this.world, pos)) {
                return Feature.OCEAN_MONUMENT.getSpawnList();
            }
        }

        return super.getPossibleCreatures(creatureType, pos);
    }

    public static double mysteriousFunction(int x, int y, int z) {
        int xx = x + 12;
        int yy = y + 12;
        int zz = z + 12;
        if (xx >= 0 && xx < 24) {
            if (yy >= 0 && yy < 24) {
                return zz >= 0 && zz < 24 ? MYSTERY_FIELD[zz * 24 * 24 + xx * 24 + yy] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private static double func_222554_b(int x, int y, int z) {
        double d0 = (x * x + z * z);
        double d1 = y + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
        double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
        return d4 * d3;
    }
}