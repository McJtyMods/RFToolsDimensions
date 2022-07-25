package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;

public class BlackSkyRenderer {

    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        renderColor(0f, 0f, 0f, 1.0f);

        RenderSystem.depthMask(true);
        return true;
    }

    public static void renderColor(float red, float green, float blue, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(red, green, blue, alpha);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float v = 200.0F;
        bufferbuilder.vertex(-v, -v, -100f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(v, -v, -100f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(v, v, -100f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(-v, v, -100f).color(red, green, blue, alpha).endVertex();

        BufferUploader.draw(bufferbuilder.end());
    }
}
