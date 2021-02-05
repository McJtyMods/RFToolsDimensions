package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class NormalChunkGenerator extends BaseChunkGenerator {

    public static final Codec<NormalChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(NormalChunkGenerator::getBiomeRegistry),
                    DimensionSettings.SETTINGS_CODEC.fieldOf("settings").forGetter(NormalChunkGenerator::getSettings)
            ).apply(instance, NormalChunkGenerator::new));

    private static final float[] FLOATS1 = Util.make(new float[13824], (floats) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    floats[i * 24 * 24 + j * 24 + k] = (float) func_222554_b(j - 12, k - 12, i - 12);
                }
            }
        }

    });
    private static final float[] FLOAT25 = Util.make(new float[25], (floats) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
                floats[i + 2 + (j + 2) * 5] = f;
            }
        }
    });

    public static final int SETTING_DEFAULT_OVERWORLD = 0;
    public static final int SETTING_FLAT_OVERWORLD = 1;
    public static final int SETTING_ELEVATED_OVERWORLD = 2;
    public static final int SETTING_DEFAULT_ISLANDS = 3;
    public static final int SETTING_FLAT_ISLANDS = 4;
    public static final int SETTING_ELEVATED_ISLANDS = 5;

    /*
    Sampling:
         xz_scale (double): Scales the X and Z axis of the noise. Higher values results in more intricate
                         horizontal shapes. Works similarly to coordinate scale from old customized worlds.
         xz_factor (double): Smoothes the noise on the horizontal axis. Works similarly to main noise scale
                         X/Z from old customized worlds.
         y_scale (double): Scales the Y axis of the noise. Higher values result in more intricate vertical
                         shapes. Works similarly to height scale from old customized worlds.
         y_factor (double): Smoothes the noise on the vertical axis. Works similarly to main noise scale Y
                         from old customized worlds.
    Bottom Slide:
         target (integer): The value of the curve. Negative values remove the floor and round off the bottom
                         of the islands, positive values make a floor. Higher values produce larger effects.
         size (integer): Defines the size of the affected area from the bottom of the world. Uses the same
                         formula as in top_slide.
         offset (integer): Moves the affected area from the bottom of the world. Uses the same formula as in top_slide.
                         For bottom_slide, positive values move the area up and negative values bring it down.
    Top Slide:
         target (integer): The value of the curve. Negative values round off the top of the hills in the
                         affected area, positive values create a roof. Higher values produce larger effects.
         size (integer): Defines the size of the affected area from the top of the world. size is calculated
                         using the formula size = <height in blocks> * 0.25 / size_vertical.
         offset (integer): Moves the affected area from the top of the world. offset uses the same formula as
                         size so offset = <height in blocks> * 0.25 / size_vertical. For top_slide, positive
                         values move the area down and negative values bring it up.

    height (integer): Changes the max height of generated terrain by squashing the world. For example, with
                     height=128, the ground is set to Y=32. this does not affect sea level.[needs testing]
    size_horizontal (integer): Changes the X/Z scale of the landmass, but not the biomes.[needs testing]
    size_vertical (integer): Changes the Y scale of the landmass. Values between 1 and 15 gradually increase
                     the hill height, above 20 are all above the normal sea level of 63, and higher than 32
                     give normal land levels of 100+.[needs testing]
    density_factor (double): Changes the gradient of terrain density from the bottom to the top of the world.
                     Positive values result in terrain that is solid underneath with shapes that shrink at
                     higher altitudes, negative values result in terrain that is solid on top with empty space
                     underneath. Greater positive or negative values result in a sharper transition.
    density_offset (double; values between -1 and 1): Moves the center height for terrain density relative to
                     Y=128, by an amount inversely proportional to density_factor.[needs testing]
    random_density_offset (boolean; optional):[needs testing]
    simplex_surface_noise (boolean):[needs testing]
    island_noise_override (boolean; optional): Causes the world to generate like The End with a big island in the
                     center and smaller ones around.
    amplified (boolean; optional): Toggles between amplified and normal terrain generation. Can be used alongside
                     large biomes in `vanilla_layered` types, and in any dimension (Nether, End, and custom).

    new NoiseSettings(
        height,
        sampling = new ScalingSettings(xz_scale, y_scale, xz_factor, y_factor),
        top_slide = new SlideSettings(target, size, offet),         // Settings for the curve at the top of the world
        bottom_slide = new SlideSettings(target, size, offset),     // Settings for the curve at the bottom of the world
        size_horizontal, size_vertical, density_factor, density_offset,
        simplex_surface_noise, random_density_offset, island_noise_override, amplified),

     */


    private static final NoiseSettings[] SETTINGS = new NoiseSettings[]{
            new NoiseSettings(256,
                    new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
                    new SlideSettings(-10, 3, 0),
                    new SlideSettings(-30, 0, 0),
                    1, 2, 1.0D, -0.46875D, true, true, false, false),
            new NoiseSettings(128,
                    new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
                    new SlideSettings(-10, 3, 0),
                    new SlideSettings(-30, 0, 0),
                    1, 2, 1.0D, -0.46875D, true, true, false, false),
            new NoiseSettings(256,
                    new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
                    new SlideSettings(-10, 3, 0),
                    new SlideSettings(-30, 0, 0),
                    1, 2, 1.0D, -0.46875D, true, true, false, true),
            new NoiseSettings(128,
                    new ScalingSettings(2.0D, 1.0D, 80.0D, 160.0D),
                    new SlideSettings(-3000, 64, -46),
                    new SlideSettings(-30, 7, 1),
                    2, 1, 0.0D, 0.0D, true, false, false, false),
            new NoiseSettings(64,
                    new ScalingSettings(2.0D, 1.0D, 80.0D, 160.0D),
                    new SlideSettings(-3000, 64, -46),
                    new SlideSettings(-30, 7, 1),
                    2, 1, 0.0D, 0.0D, true, false, false, false),
            new NoiseSettings(128,
                    new ScalingSettings(2.0D, 1.0D, 80.0D, 160.0D),
                    new SlideSettings(-3000, 64, -46),
                    new SlideSettings(-30, 7, 1),
                    2, 1, 0.0D, 0.0D, true, false, false, true)
    };
    private final int noiseIndex;

    private final OctavesNoiseGenerator oct1;
    private final OctavesNoiseGenerator oct2;
    private final OctavesNoiseGenerator oct3;
    private final OctavesNoiseGenerator oct4;
    private final SimplexNoiseGenerator simplexNoise;

    private final int verticalNoiseGranularity;
    private final int horizontalNoiseGranularity;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;

    public NormalChunkGenerator(MinecraftServer server, DimensionSettings settings) {
        this(server.func_244267_aX().getRegistry(Registry.BIOME_KEY), settings, SETTING_DEFAULT_OVERWORLD);
    }

    public NormalChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        this(registry, settings, SETTING_DEFAULT_ISLANDS);
    }

    protected NormalChunkGenerator(MinecraftServer server, DimensionSettings settings, int idx) {
        this(server.func_244267_aX().getRegistry(Registry.BIOME_KEY), settings, idx);
    }

    protected NormalChunkGenerator(Registry<Biome> registry, DimensionSettings settings, int idx) {
        super(registry, settings);

        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.FLATTER)) {
            idx++;
        } else if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.ELEVATED)) {
            idx += 2;
        }
        noiseIndex = idx;
        NoiseSettings ns = SETTINGS[idx];

        this.verticalNoiseGranularity = ns.func_236175_f_() * 4;
        this.horizontalNoiseGranularity = ns.func_236174_e_() * 4;
        this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
        this.noiseSizeY = ns.func_236169_a_() / this.verticalNoiseGranularity;
        this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;

        this.oct1 = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
        this.oct2 = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
        this.oct4 = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-7, 0));

        this.surfaceDepthNoise = ns.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0));
        randomSeed.skip(2620);
        this.oct3 = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));

        // @todo For end islands this might be useful
        if (ns.func_236180_k_()) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(settings.getSeed());
            sharedseedrandom.skip(17292);
            this.simplexNoise = new SimplexNoiseGenerator(sharedseedrandom);
        } else {
            this.simplexNoise = null;
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long l) {
        return new NormalChunkGenerator(getBiomeRegistry(), getSettings());
    }

    @Override
    public void func_230352_b_(IWorld world, StructureManager structureManager, IChunk chunk) {
        ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
        ChunkPos chunkpos = chunk.getPos();
        int chunkX = chunkpos.x;
        int chunkZ = chunkpos.z;
        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        for (Structure<?> structure : Structure.field_236384_t_) {
            structureManager.func_235011_a_(SectionPos.from(chunkpos, 0), structure).forEach((p_236089_5_) -> {
                for (StructurePiece piece : p_236089_5_.getComponents()) {
                    if (piece.func_214810_a(chunkpos, 12)) {
                        if (piece instanceof AbstractVillagePiece) {
                            AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece) piece;
                            JigsawPattern.PlacementBehaviour placementBehaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
                            if (placementBehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                                objectlist.add(abstractvillagepiece);
                            }

                            for (JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
                                int sourceX = jigsawjunction1.getSourceX();
                                int sourceZ = jigsawjunction1.getSourceZ();
                                if (sourceX > cx - 12 && sourceZ > cz - 12 && sourceX < cx + 15 + 12 && sourceZ < cz + 15 + 12) {
                                    objectlist1.add(jigsawjunction1);
                                }
                            }
                        } else {
                            objectlist.add(piece);
                        }
                    }
                }

            });
        }

        double[][][] adouble = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

        for (int nz = 0; nz < this.noiseSizeZ + 1; ++nz) {
            adouble[0][nz] = new double[this.noiseSizeY + 1];
            this.fillNoiseColumn(adouble[0][nz], chunkX * this.noiseSizeX, chunkZ * this.noiseSizeZ + nz);
            adouble[1][nz] = new double[this.noiseSizeY + 1];
        }

        ChunkPrimer chunkprimer = (ChunkPrimer) chunk;
        Heightmap heightmap = chunkprimer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkprimer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> iterator = objectlist.iterator();
        ObjectListIterator<JigsawJunction> iterator1 = objectlist1.iterator();

        for (int nx = 0; nx < this.noiseSizeX; ++nx) {
            for (int nz = 0; nz < this.noiseSizeZ + 1; ++nz) {
                this.fillNoiseColumn(adouble[1][nz], chunkX * this.noiseSizeX + nx + 1, chunkZ * this.noiseSizeZ + nz);
            }

            for (int nz = 0; nz < this.noiseSizeZ; ++nz) {
                ChunkSection chunksection = chunkprimer.getSection(15);
                chunksection.lock();

                for (int ny = this.noiseSizeY - 1; ny >= 0; --ny) {
                    double d0 = adouble[0][nz][ny];
                    double d1 = adouble[0][nz + 1][ny];
                    double d2 = adouble[1][nz][ny];
                    double d3 = adouble[1][nz + 1][ny];
                    double d4 = adouble[0][nz][ny + 1];
                    double d5 = adouble[0][nz + 1][ny + 1];
                    double d6 = adouble[1][nz][ny + 1];
                    double d7 = adouble[1][nz + 1][ny + 1];

                    for (int vertN = this.verticalNoiseGranularity - 1; vertN >= 0; --vertN) {
                        int yy = ny * this.verticalNoiseGranularity + vertN;
                        int j2 = yy & 15;
                        int k2 = yy >> 4;
                        if (chunksection.getYLocation() >> 4 != k2) {
                            chunksection.unlock();
                            chunksection = chunkprimer.getSection(k2);
                            chunksection.lock();
                        }

                        double d8 = (double) vertN / (double) this.verticalNoiseGranularity;
                        double d9 = MathHelper.lerp(d8, d0, d4);
                        double d10 = MathHelper.lerp(d8, d2, d6);
                        double d11 = MathHelper.lerp(d8, d1, d5);
                        double d12 = MathHelper.lerp(d8, d3, d7);

                        for (int horN = 0; horN < this.horizontalNoiseGranularity; ++horN) {
                            int i3 = cx + nx * this.horizontalNoiseGranularity + horN;
                            int xx = i3 & 15;
                            double d13 = (double) horN / (double) this.horizontalNoiseGranularity;
                            double d14 = MathHelper.lerp(d13, d9, d10);
                            double d15 = MathHelper.lerp(d13, d11, d12);

                            for (int horZ = 0; horZ < this.horizontalNoiseGranularity; ++horZ) {
                                int l3 = cz + nz * this.horizontalNoiseGranularity + horZ;
                                int zz = l3 & 15;
                                double d16 = (double) horZ / (double) this.horizontalNoiseGranularity;
                                double d17 = MathHelper.lerp(d16, d14, d15);
                                double d18 = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

                                int j4;
                                int k4;
                                int l4;
                                for (d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; iterator.hasNext(); d18 += func_222556_a(j4, k4, l4) * 0.8D) {
                                    StructurePiece structurepiece = iterator.next();
                                    MutableBoundingBox mbox = structurepiece.getBoundingBox();
                                    j4 = Math.max(0, Math.max(mbox.minX - i3, i3 - mbox.maxX));
                                    k4 = yy - (mbox.minY + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece) structurepiece).getGroundLevelDelta() : 0));
                                    l4 = Math.max(0, Math.max(mbox.minZ - l3, l3 - mbox.maxZ));
                                }

                                iterator.back(objectlist.size());

                                while (iterator1.hasNext()) {
                                    JigsawJunction jigsawjunction = iterator1.next();
                                    int k5 = i3 - jigsawjunction.getSourceX();
                                    j4 = yy - jigsawjunction.getSourceGroundY();
                                    k4 = l3 - jigsawjunction.getSourceZ();
                                    d18 += func_222556_a(k5, j4, k4) * 0.4D;
                                }

                                iterator1.back(objectlist1.size());
                                BlockState blockstate = this.func_236086_a_(d18, yy);
                                if (blockstate != Blocks.AIR.getDefaultState()) {
                                    mpos.setPos(i3, yy, l3);
                                    if (blockstate.getLightValue(chunkprimer, mpos) != 0) {
                                        chunkprimer.addLightPosition(mpos);
                                    }

                                    chunksection.setBlockState(xx, j2, zz, blockstate, false);
                                    heightmap.update(xx, yy, zz, blockstate);
                                    heightmap1.update(xx, yy, zz, blockstate);
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

    @Override
    public int getHeight(int x, int z, Heightmap.Type type) {
        return this.func_236087_a_(x, z, null, type.getHeightLimitPredicate());
    }

    @Override
    public IBlockReader func_230348_a_(int x, int z) {
        BlockState[] ablockstate = new BlockState[this.noiseSizeY * this.verticalNoiseGranularity];
        this.func_236087_a_(x, z, ablockstate, null);
        return new Blockreader(ablockstate);
    }

    private static double func_222554_b(int x, int y, int z) {
        double d0 = x * x + z * z;
        double d1 = (double) y + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
        double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
        return d4 * d3;
    }

    private void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
//        NoiseSettings noisesettings = this.field_236080_h_.get().getNoise();
        double d0;
        double d1;
        if (this.simplexNoise != null) {
            d0 = EndBiomeProvider.getRandomNoise(this.simplexNoise, noiseX, noiseZ) - 8.0F;
            if (d0 > 0.0D) {
                d1 = 0.25D;
            } else {
                d1 = 1.0D;
            }
        } else {
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
                    float f6;
                    float f7;
                    if (SETTINGS[noiseIndex].func_236181_l_() && f4 > 0.0F) {
                        f6 = 1.0F + f4 * 2.0F;
                        f7 = 1.0F + f5 * 4.0F;
                    } else {
                        f6 = f4;
                        f7 = f5;
                    }

                    float f8 = f4 > f3 ? 0.5F : 1.0F;
                    float f9 = f8 * FLOAT25[k + 2 + (l + 2) * 5] / (f6 + 2.0F);
                    f += f7 * f9;
                    f1 += f6 * f9;
                    f2 += f9;
                }
            }

            float f10 = f1 / f2;
            float f11 = f / f2;
            double d16 = f10 * 0.5F - 0.125F;
            double d18 = f11 * 0.9F + 0.1F;
            d0 = d16 * 0.265625D;
            d1 = 96.0D / d18;
        }

        NoiseSettings ns = SETTINGS[noiseIndex];
        double d12 = 684.412D * ns.func_236171_b_().func_236151_a_();
        double d13 = 684.412D * ns.func_236171_b_().func_236153_b_();
        double d14 = d12 / ns.func_236171_b_().func_236154_c_();
        double d15 = d13 / ns.func_236171_b_().func_236155_d_();
        double d17 = ns.func_236172_c_().func_236186_a_();
        double d19 = ns.func_236172_c_().func_236188_b_();
        double d20 = ns.func_236172_c_().func_236189_c_();
        double d21 = ns.func_236173_d_().func_236186_a_();
        double d2 = ns.func_236173_d_().func_236188_b_();
        double d3 = ns.func_236173_d_().func_236189_c_();
        double d4 = ns.func_236179_j_() ? this.func_236095_c_(noiseX, noiseZ) : 0.0D;
        double d5 = ns.func_236176_g_();
        double d6 = ns.func_236177_h_();

        for (int i1 = 0; i1 <= this.noiseSizeY; ++i1) {
            double d7 = this.func_222552_a(noiseX, i1, noiseZ, d12, d13, d14, d15);
            double d8 = 1.0D - (double) i1 * 2.0D / (double) this.noiseSizeY + d4;
            double d9 = d8 * d5 + d6;
            double d10 = (d9 + d0) * d1;
            if (d10 > 0.0D) {
                d7 = d7 + d10 * 4.0D;
            } else {
                d7 = d7 + d10;
            }

            if (d19 > 0.0D) {
                double d11 = ((double) (this.noiseSizeY - i1) - d20) / d19;
                d7 = MathHelper.clampedLerp(d17, d7, d11);
            }

            if (d2 > 0.0D) {
                double d22 = ((double) i1 - d3) / d2;
                d7 = MathHelper.clampedLerp(d21, d7, d22);
            }

            noiseColumn[i1] = d7;
        }

    }

    private double func_236095_c_(int x, int z) {
        double d0 = this.oct3.getValue(x * 200, 10.0D, z * 200, 1.0D, 0.0D, true);
        double d1;
        if (d0 < 0.0D) {
            d1 = -d0 * 0.3D;
        } else {
            d1 = d0;
        }

        double d2 = d1 * 24.575625D - 2.0D;
        return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
    }

    private double func_222552_a(int x, int y, int z, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        boolean flag = true;
        double d3 = 1.0D;

        for (int i = 0; i < 16; ++i) {
            double d4 = OctavesNoiseGenerator.maintainPrecision((double) x * p_222552_4_ * d3);
            double d5 = OctavesNoiseGenerator.maintainPrecision((double) y * p_222552_6_ * d3);
            double d6 = OctavesNoiseGenerator.maintainPrecision((double) z * p_222552_4_ * d3);
            double d7 = p_222552_6_ * d3;
            ImprovedNoiseGenerator improvednoisegenerator = this.oct1.getOctave(i);
            if (improvednoisegenerator != null) {
                d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, (double) y * d7) / d3;
            }

            ImprovedNoiseGenerator improvednoisegenerator1 = this.oct2.getOctave(i);
            if (improvednoisegenerator1 != null) {
                d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, (double) y * d7) / d3;
            }

            if (i < 8) {
                ImprovedNoiseGenerator improvednoisegenerator2 = this.oct4.getOctave(i);
                if (improvednoisegenerator2 != null) {
                    d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision((double) x * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision((double) y * p_222552_10_ * d3), OctavesNoiseGenerator.maintainPrecision((double) z * p_222552_8_ * d3), p_222552_10_ * d3, (double) y * p_222552_10_ * d3) / d3;
                }
            }

            d3 /= 2.0D;
        }

        return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
    }

    private double[] func_222547_b(int x, int y) {
        double[] adouble = new double[this.noiseSizeY + 1];
        this.fillNoiseColumn(adouble, x, y);
        return adouble;
    }

    @Override
    protected void makeBedrock(IChunk chunkIn) {
//        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
//        int i = chunkIn.getPos().getXStart();
//        int j = chunkIn.getPos().getZStart();
//        int k = 3;//@todo @@@@@@@@@@@@@@@@@@@@@@@@@ dimensionsettings.func_236118_f_();
//        int l = 3;//@todo @@@@@@@@@@@@@@@@@@@@@@@@@ this.field_236085_x_ - 1 - dimensionsettings.func_236117_e_();
//        int i1 = 5;
//        boolean flag = l + 4 >= 0 && l < noisesettings.func_236169_a_();
//        boolean flag1 = k + 4 >= 0 && k < noisesettings.func_236169_a_();
//        if (flag || flag1) {
//            for(BlockPos blockpos : BlockPos.getAllInBoxMutable(i, 0, j, i + 15, 0, j + 15)) {
//                if (flag) {
//                    for(int j1 = 0; j1 < 5; ++j1) {
//                        if (j1 <= randomSeed.nextInt(5)) {
//                            chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), l - j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
//                        }
//                    }
//                }
//
//                if (flag1) {
//                    for(int k1 = 4; k1 >= 0; --k1) {
//                        if (k1 <= randomSeed.nextInt(5)) {
//                            chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), k + k1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
//                        }
//                    }
//                }
//            }
//
//        }
    }

    private static double func_222556_a(int x, int y, int z) {
        int xx = x + 12;
        int yy = y + 12;
        int zz = z + 12;
        if (xx >= 0 && xx < 24) {
            if (yy >= 0 && yy < 24) {
                return zz >= 0 && zz < 24 ? (double) FLOATS1[zz * 24 * 24 + xx * 24 + yy] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private BlockState func_236086_a_(double p_236086_1_, int p_236086_3_) {
        BlockState blockstate;
        if (p_236086_1_ > 0.0D) {
            blockstate = getDefaultBlock(); // @todo 1.16 check
        } else if (p_236086_3_ < this.getSeaLevel()) {
            blockstate = getBaseLiquid();
        } else {
            blockstate = Blocks.AIR.getDefaultState();
        }

        return blockstate;
    }

    private int func_236087_a_(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> tester) {
        int i = Math.floorDiv(x, this.horizontalNoiseGranularity);
        int j = Math.floorDiv(z, this.horizontalNoiseGranularity);
        int k = Math.floorMod(x, this.horizontalNoiseGranularity);
        int l = Math.floorMod(z, this.horizontalNoiseGranularity);
        double d0 = (double) k / (double) this.horizontalNoiseGranularity;
        double d1 = (double) l / (double) this.horizontalNoiseGranularity;
        double[][] adouble = new double[][]{this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};

        for (int i1 = this.noiseSizeY - 1; i1 >= 0; --i1) {
            double d2 = adouble[0][i1];
            double d3 = adouble[1][i1];
            double d4 = adouble[2][i1];
            double d5 = adouble[3][i1];
            double d6 = adouble[0][i1 + 1];
            double d7 = adouble[1][i1 + 1];
            double d8 = adouble[2][i1 + 1];
            double d9 = adouble[3][i1 + 1];

            for (int j1 = this.verticalNoiseGranularity - 1; j1 >= 0; --j1) {
                double d10 = (double) j1 / (double) this.verticalNoiseGranularity;
                double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int k1 = i1 * this.verticalNoiseGranularity + j1;
                BlockState blockstate = this.func_236086_a_(d11, k1);
                if (states != null) {
                    states[k1] = blockstate;
                }

                if (tester != null && tester.test(blockstate)) {
                    return k1 + 1;
                }
            }
        }

        return 0;
    }

}
