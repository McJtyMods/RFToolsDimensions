package mcjty.rftoolsdim.compat;

import mcjty.lib.varia.Logging;
import mcjty.lostcities.api.ILostCities;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

import java.util.function.Function;

public class LostCityCompat {

    public static void register() {
        if (ModList.get().isLoaded("lostcities")) {
            registerInternal();
        }
    }

    private static boolean registered = false;
    private static ILostCities lostCities = null;

    public static boolean hasLostCities() {
        return lostCities != null;
    }

    public static void registerDimension(ResourceKey<Level> key, String profile) {
        lostCities.registerDimension(key, profile);
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
        InterModComms.sendTo("lostcities", "getLostCities", GetLostCity::new);
    }

    public static class GetLostCity implements Function<ILostCities, Void> {

        @Override
        public Void apply(ILostCities tm) {
            lostCities = tm;
            return null;
        }
    }

}
