package mcjty.rftoolsdim.dimension.features.instances;

import mcjty.lib.varia.MathTools;
import mcjty.rftoolsdim.dimension.features.IFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class TendrilsFeature implements IFeature {

    private final int range = 8;
    private final Random rand = new Random();

    private void generateTendril(long seed, int chunkX, int chunkZ, WorldGenLevel primer, double dx, double dy, double dz, float p_151541_12_, float p_151541_13_, float p_151541_14_, int p_151541_15_, int p_151541_16_, double p_151541_17_,
                                 List<BlockState> states) {
        double centerX = (chunkX * 16 + 8);
        double centerZ = (chunkZ * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;
        Random random = new Random(seed);

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

        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        boolean flag = random.nextInt(6) == 0;
        while (p_151541_15_ < p_151541_16_) {
            double d6 = 1.5D + (Mth.sin(p_151541_15_ * (float) Math.PI / p_151541_16_) * p_151541_12_ * 1.0F);
            double d7 = d6 * p_151541_17_;
            float f5 = Mth.cos(p_151541_14_);
            float f6 = Mth.sin(p_151541_14_);
            dx += (Mth.cos(p_151541_13_) * f5);
            dy += f6;
            dz += (Mth.sin(p_151541_13_) * f5);

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
                this.generateTendril(random.nextLong(), chunkX, chunkZ, primer, dx, dy, dz, random.nextFloat() * 0.5F + 0.5F, p_151541_13_ - ((float) Math.PI / 2F), p_151541_14_ / 3.0F, p_151541_15_, p_151541_16_, 1.0D, states);
                this.generateTendril(random.nextLong(), chunkX, chunkZ, primer, dx, dy, dz, random.nextFloat() * 0.5F + 0.5F, p_151541_13_ + ((float) Math.PI / 2F), p_151541_14_ / 3.0F, p_151541_15_, p_151541_16_, 1.0D, states);
                return;
            }

            if (flag2 || random.nextInt(4) != 0) {
                double d8 = dx - centerX;
                double d9 = dz - centerZ;
                double d10 = (p_151541_16_ - p_151541_15_);
                double d11 = (p_151541_12_ + 2.0F + 16.0F);

                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return;
                }

                if (dx >= centerX - 16.0D - d6 * 2.0D && dz >= centerZ - 16.0D - d6 * 2.0D && dx <= centerX + 16.0D + d6 * 2.0D && dz <= centerZ + 16.0D + d6 * 2.0D) {
                    int i4 = MathTools.floor(dx - d6) - chunkX * 16 - 1;
                    int l1 = MathTools.floor(dx + d6) - chunkX * 16 + 1;
                    int j4 = MathTools.floor(dy - d7) - 1;
                    int i2 = MathTools.floor(dy + d7) + 1;
                    int k4 = MathTools.floor(dz - d6) - chunkZ * 16 - 1;
                    int j2 = MathTools.floor(dz + d6) - chunkZ * 16 + 1;

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

                    int xx;
                    int zz;

                    for (xx = i4; xx < l1; ++xx) {
                        double d13 = ((xx + chunkX * 16) + 0.5D - dx) / d6;

                        for (zz = k4; zz < j2; ++zz) {
                            double d14 = ((zz + chunkZ * 16) + 0.5D - dz) / d6;
                            int yy = i2;

                            if (d13 * d13 + d14 * d14 < 1.0D) {
                                for (int l3 = i2 - 1; l3 >= j4; --l3) {
                                    double d12 = (l3 + 0.5D - dy) / d7;

                                    if (d12 > -0.7D && d13 * d13 + d12 * d12 + d14 * d14 < 1.0D) {

                                        mpos.set(chunkX * 16 + xx, yy, chunkZ * 16 + zz);
                                        BlockState block = primer.getBlockState(mpos);

                                        if (block == air) {
                                            primer.setBlock(mpos, IFeature.select(states, random), 0);
                                        }
                                    }

                                    --yy;
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
    public boolean generate(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos,
                            List<BlockState> states, List<BlockState> liquids, long prime) {

        ChunkPos cp = new ChunkPos(pos);
        int chunkX = cp.x;
        int chunkZ = cp.z;

        this.rand.setSeed(reader.getSeed());

        long rnd1 = this.rand.nextLong();
        long rnd2 = this.rand.nextLong();

        for (int cx = chunkX - range ; cx <= chunkX + range ; ++cx) {
            for (int cz = chunkZ - range ; cz <= chunkZ + range ; ++cz) {
                long s1 = cx * rnd1;
                long s2 = cz * rnd2;
                this.rand.setSeed(s1 ^ s2 ^ reader.getSeed());
                this.fillChunk(cx, cz, chunkX, chunkZ, reader, states);
            }
        }
        return true;
    }

    private void fillChunk(int cx, int cz, int chunkX, int chunkZ, WorldGenLevel reader, List<BlockState> states) {
        int i1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);

        if (this.rand.nextInt(7) != 0) {
            i1 = 0;
        }

        for (int j1 = 0; j1 < i1; ++j1) {
            double dx = (cx * 16 + this.rand.nextInt(16));
            double dy = this.rand.nextInt(this.rand.nextInt(120) + 8);
            double dz = (cz * 16 + this.rand.nextInt(16));
            int max = 1;

            if (this.rand.nextInt(4) == 0) {
                this.generateTendril(this.rand.nextLong(), chunkX, chunkZ, reader, dx, dy, dz, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D, states);
                max += this.rand.nextInt(4);
            }

            for (int cnt = 0; cnt < max; ++cnt) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0) {
                    f2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.generateTendril(this.rand.nextLong(), chunkX, chunkZ, reader, dx, dy, dz, f2, f, f1, 0, 0, 1.0D, states);
            }
        }
    }
}
