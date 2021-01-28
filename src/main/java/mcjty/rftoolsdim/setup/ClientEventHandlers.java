package mcjty.rftoolsdim.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

public class ClientEventHandlers {

    public void onSwitch() {
        ClientWorld world = Minecraft.getInstance().world;

        world.func_239132_a_().setSkyRenderHandler((ticks, partialTicks, matrixStack, world1, mc) -> {

        });

    }

}
