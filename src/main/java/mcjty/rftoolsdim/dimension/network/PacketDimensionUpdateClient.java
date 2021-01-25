package mcjty.rftoolsdim.dimension.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import java.util.Set;

public class PacketDimensionUpdateClient {

    public static void handleUpdateDimensionsPacket(PacketDimensionUpdate packet) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        RegistryKey<World> key = packet.getId();
        if (player == null || key == null)
            return;

        Set<RegistryKey<World>> worlds = player.connection.func_239164_m_();
        if (worlds == null)
            return;

        if (packet.getAdd()) {
            worlds.add(key);
        } else {
            worlds.remove(key);
        }
    }
}

