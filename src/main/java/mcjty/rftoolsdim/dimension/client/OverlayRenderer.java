package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public class OverlayRenderer {

    public static void render(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.SLEEP_FADE.type()) {
            ClientDimensionData.ClientData clientData = ClientDimensionData.get().getClientData(Minecraft.getInstance().level.dimension().location());
            if (clientData.power() >= 0) {
                // Don't do anything outside an RFTools Dimension
                float factor = (float) clientData.power() / clientData.max();
                if (factor < .1) {
                    float alpha = (.1f-factor) * 9;
                    if (alpha > .9f) {
                        alpha = .9f;
                    }
                    RenderSystem.depthMask(false);
                    renderBlack(0.2f, 0, 0, alpha);
                    RenderSystem.depthMask(true);
                }
            }
        }
    }

    public static void renderBlack(float red, float green, float blue, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(red, green, blue, alpha);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float v = 900.0F;
        bufferbuilder.vertex(-v, v, -100f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(v, v, -100f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(v, -v, -100f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(-v, -v, -100f).color(red, green, blue, alpha).endVertex();

        BufferUploader.draw(bufferbuilder.end());
    }

}
