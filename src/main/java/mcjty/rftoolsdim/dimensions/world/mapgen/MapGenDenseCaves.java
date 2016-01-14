package mcjty.rftoolsdim.dimensions.world.mapgen;

import com.google.common.base.Objects;
import mcjty.lib.varia.BlockMeta;
import mcjty.rftoolsdim.dimensions.world.GenericChunkProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import java.util.Random;

public class MapGenDenseCaves extends MapGenCaves {
    private final GenericChunkProvider provider;

    public MapGenDenseCaves(GenericChunkProvider provider) {
        this.provider = provider;
    }

    protected void func_151541_a(long nextLong, int chunkX, int chunkZ, ChunkPrimer chunkPrimer, double p_151541_6_, double p_151541_8_, double p_151541_10_, float p_151541_12_, float p_151541_13_, float p_151541_14_, int p_151541_15_, int p_151541_16_, double p_151541_17_) {
        double d4 = (chunkX * 16 + 8);
        double d5 = (chunkZ * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;
        Random random = new Random(nextLong);

        if (p_151541_16_ <= 0) {
            int j1 = this.range * 16 - 16;
            p_151541_16_ = j1 - random.nextInt(j1 / 4);
        }

        boolean flag2 = false;

        if (p_151541_15_ == -1) {
            p_151541_15_ = p_151541_16_ / 2;
            flag2 = true;
        }

        int k1 = random.nextInt(p_151541_16_ / 2) + p_151541_16_ / 4;

        boolean flag = random.nextInt(6) == 0;
        while (p_151541_15_ < p_151541_16_) {
            double d6 = 1.5D + (MathHelper.sin(p_151541_15_ * (float) Math.PI / p_151541_16_) * p_151541_12_ * 1.0F);
            double d7 = d6 * p_151541_17_;
            float f5 = MathHelper.cos(p_151541_14_);
            float f6 = MathHelper.sin(p_151541_14_);
            p_151541_6_ += (MathHelper.cos(p_151541_13_) * f5);
            p_151541_8_ += f6;
            p_151541_10_ += (MathHelper.sin(p_151541_13_) * f5);

            if (flag) {
                p_151541_14_ *= 0.92F;
            } else {
                p_151541_14_ *= 0.7F;
            }

            p_151541_14_ += f4 * 0.1F;
            p_151541_13_ += f3 * 0.1F;
            f4 *= 0.9F;
            f3 *= 0.75F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag2 && p_151541_15_ == k1 && p_151541_12_ > 1.0F && p_151541_16_ > 0) {
                this.func_151541_a(random.nextLong(), chunkX, chunkZ, chunkPrimer, p_151541_6_, p_151541_8_, p_151541_10_, random.nextFloat() * 0.5F + 0.5F, p_151541_13_ - ((float) Math.PI / 2F), p_151541_14_ / 3.0F, p_151541_15_, p_151541_16_, 1.0D);
                this.func_151541_a(random.nextLong(), chunkX, chunkZ, chunkPrimer, p_151541_6_, p_151541_8_, p_151541_10_, random.nextFloat() * 0.5F + 0.5F, p_151541_13_ + ((float) Math.PI / 2F), p_151541_14_ / 3.0F, p_151541_15_, p_151541_16_, 1.0D);
                return;
            }

            if (flag2 || random.nextInt(4) != 0) {
                double d8 = p_151541_6_ - d4;
                double d9 = p_151541_10_ - d5;
                double d10 = (p_151541_16_ - p_151541_15_);
                double d11 = (p_151541_12_ + 2.0F + 16.0F);

                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return;
                }

                if (p_151541_6_ >= d4 - 16.0D - d6 * 2.0D && p_151541_10_ >= d5 - 16.0D - d6 * 2.0D && p_151541_6_ <= d4 + 16.0D + d6 * 2.0D && p_151541_10_ <= d5 + 16.0D + d6 * 2.0D) {
                    int i4 = MathHelper.floor_double(p_151541_6_ - d6) - chunkX * 16 - 1;
                    int l1 = MathHelper.floor_double(p_151541_6_ + d6) - chunkX * 16 + 1;
                    int j4 = MathHelper.floor_double(p_151541_8_ - d7) - 1;
                    int i2 = MathHelper.floor_double(p_151541_8_ + d7) + 1;
                    int k4 = MathHelper.floor_double(p_151541_10_ - d6) - chunkZ * 16 - 1;
                    int j2 = MathHelper.floor_double(p_151541_10_ + d6) - chunkZ * 16 + 1;

                    if (i4 < 0) {
                        i4 = 0;
                    }

                    if (l1 > 16) {
                        l1 = 16;
                    }

                    if (j4 < 1) {
                        j4 = 1;
                    }

                    if (i2 > 248) {
                        i2 = 248;
                    }

                    if (k4 < 0) {
                        k4 = 0;
                    }

                    if (j2 > 16) {
                        j2 = 16;
                    }

                    boolean flag3 = false;
                    int k2;
                    int j3;

                    for (k2 = i4; !flag3 && k2 < l1; ++k2) {
                        for (int l2 = k4; !flag3 && l2 < j2; ++l2) {
                            for (int i3 = i2 + 1; !flag3 && i3 >= j4 - 1; --i3) {
//                                j3 = (k2 * 16 + l2) * 256 + i3;

                                if (i3 >= 0 && i3 < 256) {

                                    if (isOceanBlock(chunkPrimer, k2, i3, l2, chunkX, chunkZ)) {
                                        flag3 = true;
                                    }

                                    if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1) {
                                        i3 = j4;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag3) {
                        for (k2 = i4; k2 < l1; ++k2) {
                            double d13 = ((k2 + chunkX * 16) + 0.5D - p_151541_6_) / d6;

                            for (j3 = k4; j3 < j2; ++j3) {
                                double d14 = ((j3 + chunkZ * 16) + 0.5D - p_151541_10_) / d6;
//                                int k3 = (k2 * 16 + j3) * 256 + i2;
                                boolean flag1 = false;

                                if (d13 * d13 + d14 * d14 < 1.0D) {
                                    for (int l3 = i2 - 1; l3 >= j4; --l3) {
                                        double d12 = (l3 + 0.5D - p_151541_8_) / d7;

                                        if (d12 > -0.7D && d13 * d13 + d12 * d12 + d14 * d14 < 1.0D) {
                                            IBlockState iblockstate1 = chunkPrimer.getBlockState(k2, l3, j3);
                                            IBlockState iblockstate2 = (IBlockState) Objects.firstNonNull(chunkPrimer.getBlockState(k2, l3 + 1, j3), Blocks.air.getDefaultState());

                                            if (isTopBlock(chunkPrimer, k2, l3, j3, chunkX, chunkZ)) {
                                                flag1 = true;
                                            }
                                            digBlock(chunkPrimer, k2, l3, j3, chunkX, chunkZ, flag1, iblockstate1, iblockstate2);
                                        }

//                                        --k3;
                                    }
                                }
                            }
                        }

                        if (flag2) {
                            break;
                        }
                    }
                }
            }
            ++p_151541_15_;
        }
    }

    @Override
    protected void recursiveGenerate(World worldIn, int cx, int cz, int chunkX, int chunkZ, ChunkPrimer chunkPrimerIn) {
        int i1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(5) + 1) + 1);

//        if (this.rand.nextInt(2) != 0) {
//            i1 = 0;
//        }
        i1 += 6;

        for (int j1 = 0; j1 < i1; ++j1) {
            double x = (cx * 16 + this.rand.nextInt(16));
            double y = this.rand.nextInt(this.rand.nextInt(120) + 8);
            double z = (cz * 16 + this.rand.nextInt(16));
            int k1 = 1;

            if (this.rand.nextInt(4) == 0) {
                this.func_151541_a(this.rand.nextLong(), chunkX, chunkZ, chunkPrimerIn, x, y, z, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
                k1 += this.rand.nextInt(4);
            }

            for (int l1 = 0; l1 < k1; ++l1) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0) {
                    f2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.func_151541_a(this.rand.nextLong(), chunkX, chunkZ, chunkPrimerIn, x, y, z, f2, f, f1, 0, 0, 1.0D);
            }
        }
    }

    //Exception biomes to make sure we generate like vanilla
    private boolean isExceptionBiome(BiomeGenBase biome) {
        if (biome == BiomeGenBase.mushroomIsland) return true;
        if (biome == BiomeGenBase.beach) return true;
        if (biome == BiomeGenBase.desert) return true;
        return false;
    }

    //Determine if the block at the specified location is the top block for the biome, we take into account
    //Vanilla bugs to make sure that we generate the map the same way vanilla does.
    private boolean isTopBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        net.minecraft.world.biome.BiomeGenBase biome = worldObj.getBiomeGenForCoords(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState state = data.getBlockState(x, y, z);
        return (isExceptionBiome(biome) ? state.getBlock() == Blocks.grass : state.getBlock() == biome.topBlock);
    }
}