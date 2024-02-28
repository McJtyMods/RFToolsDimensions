package mcjty.rftoolsdim.compat;

import mcjty.rftoolsbase.api.teleportation.ITeleportationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.neoforged.neoforge.fml.InterModComms;
import net.neoforged.neoforge.fml.ModList;

import java.util.function.Function;

public class RFToolsUtilityCompat {

    public static void register() {
        if (ModList.get().isLoaded("rftoolsutility")) {
            registerInternal();
        }
    }

    private static boolean registered = false;
    public static ITeleportationManager teleportationManager = null;

    private static void registerInternal() {
        if (registered) {
            return;
        }
        registered = true;
        InterModComms.sendTo("rftoolsutility", "getTeleportationManager", GetTeleportationManager::new);
    }


    public static void createTeleporter(WorldGenLevel reader, BlockPos pos, String name) {
        if (teleportationManager != null) {
            teleportationManager.createReceiver(reader.getLevel(), pos, name, -1);
        }
    }

    public static void createTeleporter(WorldGenLevel reader, BlockPos pos, String name, int power) {
        if (teleportationManager != null) {
            teleportationManager.createReceiver(reader.getLevel(), pos, name, power);
        }
    }

    public static class GetTeleportationManager implements Function<ITeleportationManager, Void> {

        @Override
        public Void apply(ITeleportationManager tm) {
            RFToolsUtilityCompat.teleportationManager = tm;
            return null;
        }
    }
}
