package mcjty.rftoolsdim.dimension.terraintypes;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import mcjty.rftoolsdim.dimension.DimensionInformation;
import mcjty.rftoolsdim.dimension.DimensionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
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
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.List;

public class NormalChunkGenerator extends NoiseChunkGenerator<OverworldGenSettings> {
    private static final float[] field_222561_h = Util.make(new float[13824], (p_222557_0_) -> {
        for(int i = 0; i < 24; ++i) {
            for(int j = 0; j < 24; ++j) {
                for(int k = 0; k < 24; ++k) {
                    p_222557_0_[i * 24 * 24 + j * 24 + k] = (float)func_222554_b(j - 12, k - 12, i - 12);
                }
            }
        }

    });
    private static final float[] field_222576_h = Util.make(new float[25], (p_222575_0_) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
                p_222575_0_[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private final OctavesNoiseGenerator depthNoise;
    private final boolean isAmplified;
    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
    private final PatrolSpawner patrolSpawner = new PatrolSpawner();
    private final CatSpawner catSpawner = new CatSpawner();
    private final VillageSiege field_225495_n = new VillageSiege();

    private final int verticalNoiseGranularity;
    private final int horizontalNoiseGranularity;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;

    public NormalChunkGenerator(IWorld worldIn, BiomeProvider provider) {
        super(worldIn, provider, 4, 8, 256, new OverworldGenSettings(), true);  // @todo configurable settings?
        this.randomSeed.skip(2620);
        this.depthNoise = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
        this.isAmplified = worldIn.getWorldInfo().getGenerator() == WorldType.AMPLIFIED;

        // @todo get rid of Noise parent?
        this.verticalNoiseGranularity = 8;
        this.horizontalNoiseGranularity = 4;
        this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
        this.noiseSizeY = 256 / this.verticalNoiseGranularity;
        this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
    }

    public void spawnMobs(WorldGenRegion region) {
        int i = region.getMainChunkX();
        int j = region.getMainChunkZ();
        Biome biome = region.getBiome((new ChunkPos(i, j)).asBlockPos());
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setDecorationSeed(region.getSeed(), i << 4, j << 4);
        WorldEntitySpawner.performWorldGenSpawning(region, biome, i, j, sharedseedrandom);
    }

    protected void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
        double d0 = (double) 684.412F;
        double d1 = (double) 684.412F;
        double d2 = 8.555149841308594D;
        double d3 = 4.277574920654297D;
        int i = -10;
        int j = 3;
        this.calcNoiseColumn(noiseColumn, noiseX, noiseZ, (double) 684.412F, (double) 684.412F, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
        double d0 = 8.5D;
        double d1 = ((double) p_222545_5_ - (8.5D + p_222545_1_ * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / p_222545_3_;
        if (d1 < 0.0D) {
            d1 *= 4.0D;
        }

        return d1;
    }

    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    @Override
    public void makeBase(IWorld worldIn, IChunk chunkIn) {
        int i = this.getSeaLevel();
        ObjectList<AbstractVillagePiece> objectlist = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
        ChunkPos chunkpos = chunkIn.getPos();
        int j = chunkpos.x;
        int k = chunkpos.z;
        int l = j << 4;
        int i1 = k << 4;

        DimensionInformation info = DimensionManager.get(worldIn.getWorld()).getDimensionInformation(worldIn.getWorld());
        List<BlockState> baseBlocks = info.getBaseBlocks();

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
                            JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
                            if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                                objectlist.add(abstractvillagepiece);
                            }

                            for (JigsawJunction jigsawjunction : abstractvillagepiece.getJunctions()) {
                                int k1 = jigsawjunction.getSourceX();
                                int l1 = jigsawjunction.getSourceZ();
                                if (k1 > l - 12 && l1 > i1 - 12 && k1 < l + 15 + 12 && l1 < i1 + 15 + 12) {
                                    objectlist1.add(jigsawjunction);
                                }
                            }
                        }
                    }
                }
            }
        }

        double[][][] adouble = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

        for (int j5 = 0; j5 < this.noiseSizeZ + 1; ++j5) {
            adouble[0][j5] = new double[this.noiseSizeY + 1];
            this.fillNoiseColumn(adouble[0][j5], j * this.noiseSizeX, k * this.noiseSizeZ + j5);
            adouble[1][j5] = new double[this.noiseSizeY + 1];
        }

        ChunkPrimer chunkprimer = (ChunkPrimer) chunkIn;
        Heightmap heightmap = chunkprimer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkprimer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        ObjectListIterator<AbstractVillagePiece> objectlistiterator = objectlist.iterator();
        ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

        for (int k5 = 0; k5 < this.noiseSizeX; ++k5) {
            for (int l5 = 0; l5 < this.noiseSizeZ + 1; ++l5) {
                this.fillNoiseColumn(adouble[1][l5], j * this.noiseSizeX + k5 + 1, k * this.noiseSizeZ + l5);
            }

            for (int i6 = 0; i6 < this.noiseSizeZ; ++i6) {
                ChunkSection chunksection = chunkprimer.getSection(15);
                chunksection.lock();

                for (int j6 = this.noiseSizeY - 1; j6 >= 0; --j6) {
                    double d16 = adouble[0][i6][j6];
                    double d17 = adouble[0][i6 + 1][j6];
                    double d18 = adouble[1][i6][j6];
                    double d0 = adouble[1][i6 + 1][j6];
                    double d1 = adouble[0][i6][j6 + 1];
                    double d2 = adouble[0][i6 + 1][j6 + 1];
                    double d3 = adouble[1][i6][j6 + 1];
                    double d4 = adouble[1][i6 + 1][j6 + 1];

                    for (int i2 = this.verticalNoiseGranularity - 1; i2 >= 0; --i2) {
                        int j2 = j6 * this.verticalNoiseGranularity + i2;
                        int k2 = j2 & 15;
                        int l2 = j2 >> 4;
                        if (chunksection.getYLocation() >> 4 != l2) {
                            chunksection.unlock();
                            chunksection = chunkprimer.getSection(l2);
                            chunksection.lock();
                        }

                        double d5 = (double) i2 / (double) this.verticalNoiseGranularity;
                        double d6 = MathHelper.lerp(d5, d16, d1);
                        double d7 = MathHelper.lerp(d5, d18, d3);
                        double d8 = MathHelper.lerp(d5, d17, d2);
                        double d9 = MathHelper.lerp(d5, d0, d4);

                        for (int i3 = 0; i3 < this.horizontalNoiseGranularity; ++i3) {
                            int j3 = l + k5 * this.horizontalNoiseGranularity + i3;
                            int k3 = j3 & 15;
                            double d10 = (double) i3 / (double) this.horizontalNoiseGranularity;
                            double d11 = MathHelper.lerp(d10, d6, d7);
                            double d12 = MathHelper.lerp(d10, d8, d9);

                            for (int l3 = 0; l3 < this.horizontalNoiseGranularity; ++l3) {
                                int i4 = i1 + i6 * this.horizontalNoiseGranularity + l3;
                                int j4 = i4 & 15;
                                double d13 = (double) l3 / (double) this.horizontalNoiseGranularity;
                                double d14 = MathHelper.lerp(d13, d11, d12);
                                double d15 = MathHelper.clamp(d14 / 200.0D, -1.0D, 1.0D);

                                int k4;
                                int l4;
                                int i5;
                                for (d15 = d15 / 2.0D - d15 * d15 * d15 / 24.0D; objectlistiterator.hasNext(); d15 += func_222556_a(k4, l4, i5) * 0.8D) {
                                    AbstractVillagePiece abstractvillagepiece1 = objectlistiterator.next();
                                    MutableBoundingBox mutableboundingbox = abstractvillagepiece1.getBoundingBox();
                                    k4 = Math.max(0, Math.max(mutableboundingbox.minX - j3, j3 - mutableboundingbox.maxX));
                                    l4 = j2 - (mutableboundingbox.minY + abstractvillagepiece1.getGroundLevelDelta());
                                    i5 = Math.max(0, Math.max(mutableboundingbox.minZ - i4, i4 - mutableboundingbox.maxZ));
                                }

                                objectlistiterator.back(objectlist.size());

                                while (objectlistiterator1.hasNext()) {
                                    JigsawJunction jigsawjunction1 = objectlistiterator1.next();
                                    int k6 = j3 - jigsawjunction1.getSourceX();
                                    k4 = j2 - jigsawjunction1.getSourceGroundY();
                                    l4 = i4 - jigsawjunction1.getSourceZ();
                                    d15 += func_222556_a(k6, k4, l4) * 0.4D;
                                }

                                objectlistiterator1.back(objectlist1.size());
                                BlockState blockstate;
                                if (d15 > 0.0D) {
                                    blockstate = baseBlocks.get(worldIn.getRandom().nextInt(baseBlocks.size()));
                                } else if (j2 < i) {
                                    blockstate = this.defaultFluid;
                                } else {
                                    blockstate = AIR;
                                }

                                if (blockstate != AIR) {
                                    blockpos$mutable.setPos(j3, j2, i4);
                                    if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
                                        chunkprimer.addLightPosition(blockpos$mutable);
                                    }

                                    chunksection.setBlockState(k3, k2, j4, blockstate, false);
                                    heightmap.update(k3, j2, j4, blockstate);
                                    heightmap1.update(k3, j2, j4, blockstate);
                                }
                            }
                        }
                    }
                }

                chunksection.unlock();
            }

            double[][] adouble1 = adouble[0];
            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }
    }

    protected double[] getBiomeNoiseColumn(int noiseX, int noiseZ) {
        double[] adouble = new double[2];
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;
        int i = 2;
        int j = this.getSeaLevel();
        float f3 = this.biomeProvider.getNoiseBiome(noiseX, j, noiseZ).getDepth();

        for (int k = -2; k <= 2; ++k) {
            for (int l = -2; l <= 2; ++l) {
                Biome biome = this.biomeProvider.getNoiseBiome(noiseX + k, j, noiseZ + l);
                float f4 = biome.getDepth();
                float f5 = biome.getScale();
                if (this.isAmplified && f4 > 0.0F) {
                    f4 = 1.0F + f4 * 2.0F;
                    f5 = 1.0F + f5 * 4.0F;
                }

                float f6 = field_222576_h[k + 2 + (l + 2) * 5] / (f4 + 2.0F);
                if (biome.getDepth() > f3) {
                    f6 /= 2.0F;
                }

                f += f5 * f6;
                f1 += f4 * f6;
                f2 += f6;
            }
        }

        f = f / f2;
        f1 = f1 / f2;
        f = f * 0.9F + 0.1F;
        f1 = (f1 * 4.0F - 1.0F) / 8.0F;
        adouble[0] = (double) f1 + this.getNoiseDepthAt(noiseX, noiseZ);
        adouble[1] = (double) f;
        return adouble;
    }

    private double getNoiseDepthAt(int noiseX, int noiseZ) {
        double d0 = this.depthNoise.getValue((double) (noiseX * 200), 10.0D, (double) (noiseZ * 200), 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
        if (d0 < 0.0D) {
            d0 = -d0 * 0.3D;
        }

        d0 = d0 * 3.0D - 2.0D;
        if (d0 < 0.0D) {
            d0 = d0 / 28.0D;
        } else {
            if (d0 > 1.0D) {
                d0 = 1.0D;
            }

            d0 = d0 / 40.0D;
        }

        return d0;
    }

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

    public void spawnMobs(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
        this.phantomSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
        this.patrolSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
        this.catSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
        this.field_225495_n.func_225477_a(worldIn, spawnHostileMobs, spawnPeacefulMobs);
    }

    public int getGroundHeight() {
        return this.world.getSeaLevel() + 1;
    }

    public int getSeaLevel() {
        return 63;
    }

    private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
        int i = p_222556_0_ + 12;
        int j = p_222556_1_ + 12;
        int k = p_222556_2_ + 12;
        if (i >= 0 && i < 24) {
            if (j >= 0 && j < 24) {
                return k >= 0 && k < 24 ? (double)field_222561_h[k * 24 * 24 + i * 24 + j] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
        double d0 = (double)(p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
        double d1 = (double)p_222554_1_ + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
        double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
        return d4 * d3;
    }
}