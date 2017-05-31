package mcjty.rftoolsdim.config;

import net.minecraftforge.common.config.Configuration;

public class LostCityConfiguration {

    public static final String CATEGORY_LOSTCITY = "lostcity";

    public static float VINE_CHANCE = 0.005f;
    public static int WATERLEVEL_OFFSET = 8;

    public static float EXPLOSION_CHANCE = .005f;
    public static int EXPLOSION_MINRADIUS = 17;
    public static int EXPLOSION_MAXRADIUS = 80;
    public static int EXPLOSION_MINHEIGHT = 70;
    public static int EXPLOSION_MAXHEIGHT = 120;

    public static float MINI_EXPLOSION_CHANCE = .05f;
    public static int MINI_EXPLOSION_MINRADIUS = 5;
    public static int MINI_EXPLOSION_MAXRADIUS = 15;
    public static int MINI_EXPLOSION_MINHEIGHT = 60;
    public static int MINI_EXPLOSION_MAXHEIGHT = 100;

    public static float STYLE_CHANCE_CRACKED = 0.06f;
    public static float STYLE_CHANCE_MOSSY = 0.05f;

    public static float CITY_CHANCE = .02f;
    public static int CITY_MINRADIUS = 50;
    public static int CITY_MAXRADIUS = 128;
    public static float CITY_THRESSHOLD = .2f;

    public static float BUILDING_CHANCE = .3f;
    public static int BUILDING_MINFLOORS = 0;
    public static int BUILDING_MAXFLOORS = 10;
    public static int BUILDING_MINFLOORS_CHANCE = 4;
    public static int BUILDING_MAXFLOORS_CHANCE = 7;
    public static int BUILDING_MINCELLARS = 0;
    public static int BUILDING_MAXCELLARS = 4;
    public static float BUILDING_DOORWAYCHANCE = .6f;

    public static float CORRIDOR_CHANCE = .7f;
    public static float FOUNTAIN_CHANCE = .05f;

    public static void init(Configuration cfg) {
        STYLE_CHANCE_CRACKED = cfg.getFloat("styleChanceCracked", CATEGORY_LOSTCITY, STYLE_CHANCE_CRACKED, 0.0f, 1.0f, "The chance that a brick will be cracked");
        STYLE_CHANCE_MOSSY = cfg.getFloat("styleChanceMossy", CATEGORY_LOSTCITY, STYLE_CHANCE_MOSSY, 0.0f, 1.0f, "The chance that a brick will be mossy");

        VINE_CHANCE = cfg.getFloat("vineChance", CATEGORY_LOSTCITY, VINE_CHANCE, 0.0f, 1.0f, "The chance that a block on the outside of a building will be covered with a vine");

        WATERLEVEL_OFFSET = cfg.getInt("waterLevelOffset", CATEGORY_LOSTCITY, WATERLEVEL_OFFSET, 1, 30, "How much lower the water level is compared to the ground level (63)");

        EXPLOSION_CHANCE = cfg.getFloat("explosionChance", CATEGORY_LOSTCITY, EXPLOSION_CHANCE, 0.0f, 1.0f, "The chance that a chunk will contain an explosion");
        EXPLOSION_MINRADIUS = cfg.getInt("explosionMinRadius", CATEGORY_LOSTCITY, EXPLOSION_MINRADIUS, 1, 1000, "The minimum radius of an explosion");
        EXPLOSION_MAXRADIUS = cfg.getInt("explosionMaxRadius", CATEGORY_LOSTCITY, EXPLOSION_MAXRADIUS, 1, 3000, "The maximum radius of an explosion");
        EXPLOSION_MINHEIGHT = cfg.getInt("explosionMinHeight", CATEGORY_LOSTCITY, EXPLOSION_MINHEIGHT, 1, 256, "The minimum height of an explosion");
        EXPLOSION_MAXHEIGHT = cfg.getInt("explosionMaxHeight", CATEGORY_LOSTCITY, EXPLOSION_MAXHEIGHT, 1, 256, "The maximum height of an explosion");

        MINI_EXPLOSION_CHANCE = cfg.getFloat("miniExplosionChance", CATEGORY_LOSTCITY, MINI_EXPLOSION_CHANCE, 0.0f, 1.0f, "The chance that a chunk will contain a mini explosion");
        MINI_EXPLOSION_MINRADIUS = cfg.getInt("miniExplosionMinRadius", CATEGORY_LOSTCITY, MINI_EXPLOSION_MINRADIUS, 1, 1000, "The minimum radius of a mini explosion");
        MINI_EXPLOSION_MAXRADIUS = cfg.getInt("miniExplosionMaxRadius", CATEGORY_LOSTCITY, MINI_EXPLOSION_MAXRADIUS, 1, 3000, "The maximum radius of a mini explosion");
        MINI_EXPLOSION_MINHEIGHT = cfg.getInt("miniExplosionMinHeight", CATEGORY_LOSTCITY, MINI_EXPLOSION_MINHEIGHT, 1, 256, "The minimum height of a mini explosion");
        MINI_EXPLOSION_MAXHEIGHT = cfg.getInt("miniExplosionMaxHeight", CATEGORY_LOSTCITY, MINI_EXPLOSION_MAXHEIGHT, 1, 256, "The maximum height of a mini explosion");

        CITY_CHANCE = cfg.getFloat("cityChance", CATEGORY_LOSTCITY, CITY_CHANCE, 0.0f, 1.0f, "The chance this chunk will be the center of a city");
        CITY_MINRADIUS = cfg.getInt("cityMinRadius", CATEGORY_LOSTCITY, CITY_MINRADIUS, 1, 1000, "The minimum radius of a city");
        CITY_MAXRADIUS = cfg.getInt("cityMaxRadius", CATEGORY_LOSTCITY, CITY_MAXRADIUS, 1, 2000, "The maximum radius of a city");
        CITY_THRESSHOLD = cfg.getFloat("cityThresshold", CATEGORY_LOSTCITY, CITY_THRESSHOLD, 0.0f, 1.0f, "The center and radius of a city define a sphere. " +
                "This thresshold indicates from which point a city is considered a city. " +
                "This is important for calculating where cities are based on overlapping city circles (where the city thressholds are added)");

        BUILDING_CHANCE = cfg.getFloat("buildingChance", CATEGORY_LOSTCITY, BUILDING_CHANCE, 0.0f, 1.0f, "The chance that a chunk in a city will have a building. Otherwise it will be a street");
        BUILDING_MINFLOORS = cfg.getInt("buildingMinFloors", CATEGORY_LOSTCITY, BUILDING_MINFLOORS, 0, 30, "The minimum number of floors (above ground) for a building (0 means the first floor only)");
        BUILDING_MAXFLOORS = cfg.getInt("buildingMaxFloors", CATEGORY_LOSTCITY, BUILDING_MAXFLOORS, 0, 30, "A cap for the amount of floors a city can have (above ground)");
        BUILDING_MINFLOORS_CHANCE = cfg.getInt("buildingMinFloorsChance", CATEGORY_LOSTCITY, BUILDING_MINFLOORS_CHANCE, 1, 30, "The amount of floors of a building is equal to: " +
                "MINFLOORS + random(MINFLOORS_CHANCE + (cityFactor + .1f) * (MAXFLOORS_CHANCE - MINFLOORS_CHANCE))");

        BUILDING_MINCELLARS = cfg.getInt("buildingMinCellars", CATEGORY_LOSTCITY, BUILDING_MINCELLARS, 0, 7, "The minimum number of cellars (below ground). 0 means no cellar");
        BUILDING_MAXCELLARS = cfg.getInt("buildingMaxCellars", CATEGORY_LOSTCITY, BUILDING_MAXCELLARS, 0, 7, "The maximum number of cellars (below ground). 0 means no cellar");
        BUILDING_DOORWAYCHANCE = cfg.getFloat("buildingDoorwayChance", CATEGORY_LOSTCITY, BUILDING_DOORWAYCHANCE, 0.0f, 1.0f, "The chance that a doorway will be generated at a side of a building (on any level). Only when possible");

        CORRIDOR_CHANCE = cfg.getFloat("corridorChance", CATEGORY_LOSTCITY, CORRIDOR_CHANCE, 0.0f, 1.0f, "The chance that a chunk can possibly contain a corridor. " +
                "There actually being a corridor also depends on the presence of adjacent corridors");

        FOUNTAIN_CHANCE = cfg.getFloat("fountainChance", CATEGORY_LOSTCITY, FOUNTAIN_CHANCE, 0.0f, 1.0f, "The chance that a street section contains a fountain");
    }
}
