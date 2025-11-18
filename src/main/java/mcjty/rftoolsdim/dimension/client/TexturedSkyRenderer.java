package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TexturedSkyRenderer {

    private final ResourceLocation texture;

    public TexturedSkyRenderer(ResourceLocation texture) {
        this.texture = texture;
    }

    public boolean renderSky(Matrix4f pose) {
        // Use neutral white tint to avoid relying on internal FogRenderer fields that changed in 1.21.1
        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;

        RenderSystem.depthMask(false);

        BlackSkyRenderer.renderColor(0f, 0f, 0f, 1.0f);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float v = 800.0F;
        buffer.addVertex(pose, -v, 200, -v).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  200, -v).setUv(1, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  200, v).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -v, 200, v).setUv(0, 1).setColor(red, green, blue, 1);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        return true;
    }
}
