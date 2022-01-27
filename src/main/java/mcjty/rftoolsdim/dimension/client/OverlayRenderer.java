package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class OverlayRenderer {

    public static void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ClientDimensionData.ClientData clientData = ClientDimensionData.get().getClientData(Minecraft.getInstance().level.dimension().location());
            if (clientData.power() >= 0) {
                // Don't do anything outside an RFTools Dimension
                float factor = (float) clientData.power() / clientData.max();
                if (factor < .1) {
                    float alpha = (.1f-factor) * 3;
                    RenderSystem.depthMask(false);
                    RenderSystem.disableDepthTest();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
                    BlackSkyRenderer.renderColor(0, 0, 0, alpha);
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();
                    RenderSystem.depthMask(true);
                }
            }
        }
    }
}
