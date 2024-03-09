package mcjty.rftoolsdim.compat;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

public class LostCityCompat {

    private static boolean hasLostCities = false;

    public static void register() {
        hasLostCities = ModList.get().isLoaded("lostcities");
        if (hasLostCities()) {
            registerInternal();
        }
    }

    private static boolean registered = false;
    private static LostCityInternal lostCityInternal = new LostCityInternal();

    public static boolean hasLostCities() {
        return hasLostCities;
    }

    public static void registerDimension(ResourceKey<Level> key, String profile) {
        lostCityInternal.lostCities.registerDimension(key, profile);
    }

    public static String getProfile(TerrainType terrainType) {
        return switch (terrainType) {
            case CAVERN -> "cavern";
            default -> "default";
        };
    }

    private static void registerInternal() {
        if (registered) {
            return;
        }
        registered = true;
        Logging.log("RFTools Dimensions detected LostCities: enabling support");
        InterModComms.sendTo("lostcities", "getLostCities", LostCityInternal.GetLostCity::new);
    }

}
