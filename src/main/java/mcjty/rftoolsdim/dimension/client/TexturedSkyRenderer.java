package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ISkyRenderHandler;

public class TexturedSkyRenderer implements ISkyRenderHandler {

    private final ResourceLocation texture;

    public TexturedSkyRenderer(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void render(int ticks, float partialTicks, PoseStack matrixStack, ClientLevel world, Minecraft mc) {
        float red = FogRenderer.fogRed;
        float green = FogRenderer.fogGreen;
        float blue =FogRenderer.fogBlue;
        FogRenderer.levelFogColor();

        RenderSystem.depthMask(false);

        BlackSkyRenderer.renderColor(0f, 0f, 0f, 1.0f);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        Matrix4f pose = matrixStack.last().pose();

        float v = 800.0F;
        bufferbuilder.vertex(pose, -v, 200, -v).uv(0, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  200, -v).uv(1, 0).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, v,  200, v).uv(1, 1).color(red, green, blue, 1).endVertex();
        bufferbuilder.vertex(pose, -v, 200, v).uv(0, 1).color(red, green, blue, 1).endVertex();


        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);

        RenderSystem.depthMask(true);
    }
}