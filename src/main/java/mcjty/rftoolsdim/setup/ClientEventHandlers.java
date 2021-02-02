package mcjty.rftoolsdim.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.event.TickEvent;

public class ClientEventHandlers {

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ClientWorld world = Minecraft.getInstance().world;

            world.func_239132_a_().setWeatherRenderHandler((ticks, partialTicks, world1, mc, lightmapIn, xIn, yIn, zIn) -> {

            });
        }
    }

}
