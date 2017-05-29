package mcjty.rftoolsdim.dimensions.world.terrain.lost;

import java.util.Random;

/**
 * A city is defined as a big sphere. Buildings are where the radius is less then 70%
 */
public class City {

    private static boolean isCityCenter(long seed, int chunkX, int chunkZ) {
        Random rand = new Random(seed + chunkZ * 797003437L + chunkX * 295075153L);
        rand.nextFloat();
        rand.nextFloat();
        return rand.nextFloat() < .02f;
    }

    private static float getCityRadius(long seed, int chunkX, int chunkZ) {
        Random rand = new Random(seed + chunkZ * 100001653L + chunkX * 295075153L);
        rand.nextFloat();
        rand.nextFloat();
        return 50 + rand.nextInt(78);
    }

    public static float getCityFactor(long seed, int chunkX, int chunkZ) {
        float factor = 0;
        for (int cx = chunkX - 8; cx <= chunkX + 8; cx++) {
            for (int cz = chunkZ - 8; cz <= chunkZ + 8; cz++) {
                if (isCityCenter(seed, cx, cz)) {
                    float radius = getCityRadius(seed, cx, cz);
                    float sqdist = (cx * 16 - chunkX * 16) * (cx * 16 - chunkX * 16) + (cz * 16 - chunkZ * 16) * (cz * 16 - chunkZ * 16);
                    if (sqdist < radius * radius) {
                        float dist = (float) Math.sqrt(sqdist);
                        factor += (radius - dist) / radius;
                    }
                }
            }
        }
        return factor;
    }

}
