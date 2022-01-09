package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;

public class ClientEventHandlers {

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ClientLevel world = Minecraft.getInstance().level;

            world.effects().setSkyRenderHandler((ticks, partialTicks, matrixStack, world1, mc) -> {

            });
        }
    }

    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientDimensionData.get().clear();
    }

}
