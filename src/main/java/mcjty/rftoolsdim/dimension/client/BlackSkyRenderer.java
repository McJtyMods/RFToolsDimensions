package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.ISkyRenderHandler;

public class BlackSkyRenderer implements ISkyRenderHandler {

    @Override
    public void render(int ticks, float partialTicks, PoseStack matrixStack, ClientLevel world, Minecraft mc) {
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        renderColor(0f, 0f, 0f, 1.0f);

        RenderSystem.depthMask(true);
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

        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
}
