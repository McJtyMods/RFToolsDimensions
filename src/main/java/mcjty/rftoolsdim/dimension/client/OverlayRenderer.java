package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class OverlayRenderer {

    public static void render(RenderGuiLayerEvent.Post event) {
        if (event.getName().equals(VanillaGuiLayers.TITLE)) {
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

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float v = 900.0F;
        buffer.addVertex(-v, v, -100f).setColor(red, green, blue, alpha);
        buffer.addVertex(v, v, -100f).setColor(red, green, blue, alpha);
        buffer.addVertex(v, -v, -100f).setColor(red, green, blue, alpha);
        buffer.addVertex(-v, -v, -100f).setColor(red, green, blue, alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

}
