package mcjty.rftoolsdim.dimension.terraintypes;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.BlockState;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.List;

public class NormalChunkGenerator extends BaseChunkGenerator<OverworldGenSettings> {

    private static final float[] MYSTERY_FIELD2 = Util.make(new float[25], (array) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((i * i + j * j) + 0.2F);
                array[i + 2 + (j + 2) * 5] = f;
            }
        }
    });

    private final OctavesNoiseGenerator depthNoise;
    private final boolean isAmplified;

    public NormalChunkGenerator(IWorld worldIn, BiomeProvider provider) {
        super(worldIn, provider, 4, 8, 256, new OverworldGenSettings(), true);  // @todo configurable settings?
        this.randomSeed.skip(2620);
        this.depthNoise = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
        this.isAmplified = worldIn.getWorldInfo().getGenerator() == WorldType.AMPLIFIED;
    }

    @Override
    public void spawnMobs(WorldGenRegion region) {
        int mainX = region.getMainChunkX();
        int mainZ = region.getMainChunkZ();
        Biome biome = region.getBiome((new ChunkPos(mainX, mainZ)).asBlockPos());
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setDecorationSeed(region.getSeed(), mainX << 4, mainZ << 4);
        WorldEntitySpawner.performWorldGenSpawning(region, biome, mainX, mainZ, sharedseedrandom);
    }

    @Override
    protected void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
        double d0 = 684.412F;
        double d1 = 684.412F;
        double d2 = 8.555149841308594D;
        double d3 = 4.277574920654297D;
        int i = -10;
        int j = 3;
        this.calcNoiseColumn(noiseColumn, noiseX, noiseZ, 684.412F, 684.412F, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    @Override
    protected void makeBaseInternal(IWorld worldIn, int seaLevel,
                                    ObjectList<AbstractVillagePiece> villagePieces, ObjectList<JigsawJunction> jigsawJunctions,
                                    int chunkX, int chunkZ, int x, int z,
                                    List<BlockState> baseBlocks, ChunkPrimer primer,
                                    Heightmap heightmapOceanFloor, Heightmap heightmapWorldSurface) {

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        double[][][] adouble = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];
        for (int nz = 0; nz < this.noiseSizeZ + 1; ++nz) {
            adouble[0][nz] = new double[this.noiseSizeY + 1];
            this.fillNoiseColumn(adouble[0][nz], chunkX * this.noiseSizeX, chunkZ * this.noiseSizeZ + nz);
            adouble[1][nz] = new double[this.noiseSizeY + 1];
        }


        for (int nx = 0; nx < this.noiseSizeX; ++nx) {
            for (int nz = 0; nz < this.noiseSizeZ + 1; ++nz) {
                this.fillNoiseColumn(adouble[1][nz], chunkX * this.noiseSizeX + nx + 1, chunkZ * this.noiseSizeZ + nz);
            }

            for (int nz = 0; nz < this.noiseSizeZ; ++nz) {
                ChunkSection section = primer.getSection(15);
                section.lock();

                for (int ny = this.noiseSizeY - 1; ny >= 0; --ny) {
                    double d16 = adouble[0][nz][ny];
                    double d17 = adouble[0][nz + 1][ny];
                    double d18 = adouble[1][nz][ny];
                    double d0 = adouble[1][nz + 1][ny];
                    double d1 = adouble[0][nz][ny + 1];
                    double d2 = adouble[0][nz + 1][ny + 1];
                    double d3 = adouble[1][nz][ny + 1];
                    double d4 = adouble[1][nz + 1][ny + 1];

                    for (int vertNoiseGran = this.verticalNoiseGranularity - 1; vertNoiseGran >= 0; --vertNoiseGran) {
                        int adjustedY = ny * this.verticalNoiseGranularity + vertNoiseGran;
                        int y15 = adjustedY & 15;
                        int sectionIndex = adjustedY >> 4;
                        if (section.getYLocation() >> 4 != sectionIndex) {
                            section.unlock();
                            section = primer.getSection(sectionIndex);
                            section.lock();
                        }

                        double d5 = (double) vertNoiseGran / this.verticalNoiseGranularity;
                        double d6 = MathHelper.lerp(d5, d16, d1);
                        double d7 = MathHelper.lerp(d5, d18, d3);
                        double d8 = MathHelper.lerp(d5, d17, d2);
                        double d9 = MathHelper.lerp(d5, d0, d4);

                        for (int horNoiseGran = 0; horNoiseGran < this.horizontalNoiseGranularity; ++horNoiseGran) {
                            int adjustedX = x + nx * this.horizontalNoiseGranularity + horNoiseGran;
                            int x15 = adjustedX & 15;
                            double d10 = (double) horNoiseGran / this.horizontalNoiseGranularity;
                            double d11 = MathHelper.lerp(d10, d6, d7);
                            double d12 = MathHelper.lerp(d10, d8, d9);

                            for (int l3 = 0; l3 < this.horizontalNoiseGranularity; ++l3) {
                                int adjustedZ = z + nz * this.horizontalNoiseGranularity + l3;
                                int z15 = adjustedZ & 15;
                                double d13 = (double) l3 / this.horizontalNoiseGranularity;
                                double noise = MathHelper.clamp(MathHelper.lerp(d13, d11, d12) / 200.0D, -1.0D, 1.0D);

                                noise = noise / 2.0D - noise * noise * noise / 24.0D;
                                noise = adjustNoise(villagePieces, jigsawJunctions, adjustedX, adjustedY, adjustedZ, noise);

                                BlockState blockstate;
                                if (noise > 0.0D) {
                                    blockstate = baseBlocks.get(worldIn.getRandom().nextInt(baseBlocks.size()));
                                } else if (adjustedY < seaLevel) {
                                    blockstate = this.defaultFluid;
                                } else {
                                    blockstate = AIR;
                                }

                                if (blockstate != AIR) {
                                    mutablePos.setPos(adjustedX, adjustedY, adjustedZ);
                                    if (blockstate.getLightValue(primer, mutablePos) != 0) {
                                        primer.addLightPosition(mutablePos);
                                    }

                                    section.setBlockState(x15, y15, z15, blockstate, false);
                                    heightmapOceanFloor.update(x15, adjustedY, z15, blockstate);
                                    heightmapWorldSurface.update(x15, adjustedY, z15, blockstate);
                                }
                            }
                        }
                    }
                }

                section.unlock();
            }

            double[][] adouble1 = adouble[0];
            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }
    }



    @Override
    protected double[] getBiomeNoiseColumn(int noiseX, int noiseZ) {
        double[] adouble = new double[2];
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;
        int i = 2;
        int seaLevel = this.getSeaLevel();
        float biomeDepth = this.biomeProvider.getNoiseBiome(noiseX, seaLevel, noiseZ).getDepth();

        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                Biome biome = this.biomeProvider.getNoiseBiome(noiseX + dx, seaLevel, noiseZ + dz);
                float depth = biome.getDepth();
                float scale = biome.getScale();
                if (this.isAmplified && depth > 0.0F) {
                    depth = 1.0F + depth * 2.0F;
                    scale = 1.0F + scale * 4.0F;
                }

                float f6 = MYSTERY_FIELD2[dx + 2 + (dz + 2) * 5] / (depth + 2.0F);
                if (biome.getDepth() > biomeDepth) {
                    f6 /= 2.0F;
                }

                f += scale * f6;
                f1 += depth * f6;
                f2 += f6;
            }
        }

        f = f / f2;
        f1 = f1 / f2;
        f = f * 0.9F + 0.1F;
        f1 = (f1 * 4.0F - 1.0F) / 8.0F;
        adouble[0] = f1 + this.getNoiseDepthAt(noiseX, noiseZ);
        adouble[1] = f;
        return adouble;
    }

    private double getNoiseDepthAt(int noiseX, int noiseZ) {
        double dn = this.depthNoise.getValue((noiseX * 200), 10.0D, (noiseZ * 200), 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
        if (dn < 0.0D) {
            dn = -dn * 0.3D;
        }

        dn = dn * 3.0D - 2.0D;
        if (dn < 0.0D) {
            dn = dn / 28.0D;
        } else {
            if (dn > 1.0D) {
                dn = 1.0D;
            }

            dn = dn / 40.0D;
        }

        return dn;
    }

    @Override
    public int getGroundHeight() {
        return this.world.getSeaLevel() + 1;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }
}