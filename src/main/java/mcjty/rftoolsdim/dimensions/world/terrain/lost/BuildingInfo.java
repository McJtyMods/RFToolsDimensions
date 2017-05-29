package mcjty.rftoolsdim.dimensions.world.terrain.lost;

import java.util.Random;

public class BuildingInfo {
    public final int chunkX;
    public final int chunkZ;
    public final long seed;

    public final boolean isCity;
    public final boolean hasBuilding;
    public final int fountainType;
    public final int floors;
    public final int floorsBelowGround;
    public final int[] floorTypes;
    public final boolean[] connectionAtX;
    public final boolean[] connectionAtZ;
    public final int topType;
    public final int glassType;
    public final int glassColor;
    public final int buildingStyle;

    private BuildingInfo xmin = null;
    private BuildingInfo xmax = null;
    private BuildingInfo zmin = null;
    private BuildingInfo zmax = null;

    public BuildingInfo getXmin() {
        if (xmin == null) {
            xmin = new BuildingInfo(chunkX-1, chunkZ, seed);
        }
        return xmin;
    }

    public BuildingInfo getXmax() {
        if (xmax == null) {
            xmax = new BuildingInfo(chunkX+1, chunkZ, seed);
        }
        return xmax;
    }

    public BuildingInfo getZmin() {
        if (zmin == null) {
            zmin = new BuildingInfo(chunkX, chunkZ-1, seed);
        }
        return zmin;
    }

    public BuildingInfo getZmax() {
        if (zmax == null) {
            zmax = new BuildingInfo(chunkX, chunkZ+1, seed);
        }
        return zmax;
    }

    public BuildingInfo(int chunkX, int chunkZ, long seed) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.seed = seed;
        Random rand = getBuildingRandom(chunkX, chunkZ, seed);
        float cityFactor = City.getCityFactor(seed, chunkX, chunkZ);
        isCity = cityFactor > .2f;
        hasBuilding = isCity && (chunkX != 0 || chunkZ != 0) && rand.nextFloat() < .3f;
        if (rand.nextFloat() < .05f) {
            fountainType = rand.nextInt(LostCityData.FOUNTAINS.length);
        } else {
            fountainType = -1;
        }
        floors = rand.nextInt((int) (4 + (cityFactor + .1f) * 3));
        floorsBelowGround = rand.nextInt(4);
        floorTypes = new int[floors + floorsBelowGround + 2];
        connectionAtX = new boolean[floors + floorsBelowGround + 2];
        connectionAtZ = new boolean[floors + floorsBelowGround + 2];
        for (int i = 0; i <= floors + floorsBelowGround + 1; i++) {
            floorTypes[i] = rand.nextInt(LostCityData.FLOORS.length);
            connectionAtX[i] = rand.nextFloat() < .6f;
            connectionAtZ[i] = rand.nextFloat() < .6f;
        }
        topType = rand.nextInt(LostCityData.TOPS.length);
        glassType = rand.nextInt(4);
        glassColor = rand.nextInt(5);
        buildingStyle = rand.nextInt(4);
    }

    // Return true if the road from a neighbouring chunk can extend into this chunk
    public boolean doesRoadExtendTo() {
        return isCity && !hasBuilding;
    }

    public static Random getBuildingRandom(int chunkX, int chunkZ, long seed) {
        Random rand = new Random(seed + chunkZ * 341873128712L + chunkX * 132897987541L);
        rand.nextFloat();
        rand.nextFloat();
        return rand;
    }

    public boolean hasConnectionAtX(int level) {
        if (level >= connectionAtX.length) {
            return false;
        }
        return connectionAtX[level];
    }

    public boolean hasConnectionAtZ(int level) {
        if (level >= connectionAtZ.length) {
            return false;
        }
        return connectionAtZ[level];
    }
}
