package mcjty.rftoolsdim.tools;

import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.Random;

public class PerlinNoiseGenerator14 {
   private final SimplexNoise[] noiseLevels;
   private final int levels;

   public PerlinNoiseGenerator14(long seed, int levelsIn) {
      this.levels = levelsIn;
      this.noiseLevels = new SimplexNoise[levelsIn];

      for(int i = 0; i < levelsIn; ++i) {
         this.noiseLevels[i] = new SimplexNoise(new LegacyRandomSource(seed));
      }

   }

   // Range from approx -13.5 to 13.5
   public double getValue(double x, double z) {
      return this.noiseAt(x, z, false);
   }

   public double noiseAt(double x, double y, boolean useNoiseOffsets) {
      double d0 = 0.0D;
      double d1 = 1.0D;

      for(int i = 0; i < this.levels; ++i) {
         d0 += this.noiseLevels[i].getValue(x * d1 + (useNoiseOffsets ? this.noiseLevels[i].xo : 0.0D), y * d1 + (useNoiseOffsets ? this.noiseLevels[i].yo : 0.0D)) / d1;
         d1 /= 2.0D;
      }

      return d0;
   }

   public double getSurfaceNoiseValue(double x, double y) {
      return this.noiseAt(x, y, true) * 0.55D;
   }

   public static void main(String[] args) {
      PerlinNoiseGenerator14 noise = new PerlinNoiseGenerator14(343423384, 4);
      Random random = new Random(332);
      double min = 100000000;
      double max = -100000000;
      for (int i = 0 ; i < 10000000 ; i++) {
         double v = noise.getValue(random.nextInt(1000) - 500, random.nextInt(1000) - 500);
         min = Math.min(min, v);
         max = Math.max(max, v);
      }
      System.out.println("min = " + min);
      System.out.println("max = " + max);

   }
}