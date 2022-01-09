package mcjty.rftoolsdim.dimension.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Set;

public class PacketDimensionUpdateClient {

    public static void handleUpdateDimensionsPacket(PacketDimensionUpdate packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        ResourceKey<Level> key = packet.getId();
        if (player == null || key == null)
            return;

        Set<ResourceKey<Level>> worlds = player.connection.levels();

        if (packet.getAdd()) {
            worlds.add(key);
        } else {
            worlds.remove(key);
        }
    }
}

