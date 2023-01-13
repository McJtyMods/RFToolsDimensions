package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SkyboxRenderer {

    private final ResourceLocation texture1;
    private final ResourceLocation texture2;

    public SkyboxRenderer(ResourceLocation texture1, ResourceLocation texture2) {
        this.texture1 = texture1;
        this.texture2 = texture2;
    }

    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float red = FogRenderer.fogRed;
        float green = FogRenderer.fogGreen;
        float blue =FogRenderer.fogBlue;
        FogRenderer.levelFogColor();

        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableTexture();
//        RenderSystem.setShaderColor(red, green, blue, 1.0F);
        RenderSystem.setShaderTexture(0, texture2);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        Matrix4f pose = poseStack.last().pose();

        float v = 200.0F;
        bufferbuilder.vertex(pose, -v, 200, -v).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  200, -v).uv(1, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  200, v).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -v, 200, v).uv(0, 1).color(red, green, blue, 1).endVertex();

        bufferbuilder.vertex(pose, -v, -200, v).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  -200, v).uv(1, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  -200, -v).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -v, -200, -v).uv(0, 1).color(red, green, blue, 1).endVertex();

        RenderSystem.setShaderTexture(0, texture1);

        bufferbuilder.vertex(pose, -v, -v, -200).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  -v, -200).uv(1, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,   v, -200).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -v,  v, -200).uv(0, 1).color(red, green, blue, 1).endVertex();

        bufferbuilder.vertex(pose, -v,  v, 200).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,   v, 200).uv(0, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  -v, 200).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -v, -v, 200).uv(1, 0).color(red, green, blue, 1).endVertex();

        bufferbuilder.vertex(pose, 200, v,  -v).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, 200, -v, -v).uv(1, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, 200, -v,  v).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, 200, v,   v).uv(0, 1).color(red, green, blue, 1).endVertex();

        bufferbuilder.vertex(pose, -200, v,   v).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -200, -v,  v).uv(1, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -200, -v, -v).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -200, v,  -v).uv(0, 1).color(red, green, blue, 1).endVertex();

        BufferUploader.draw(bufferbuilder.end());

        RenderSystem.depthMask(true);
        return true;
    }
}
