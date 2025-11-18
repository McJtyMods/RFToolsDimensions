package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
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

    public boolean renderSky(Matrix4f pose) {
        // Use neutral white tint to avoid relying on FogRenderer internals changed in 1.21.1
        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;

        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture2);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float v = 200.0F;
        buffer.addVertex(pose, -v, 200, -v).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  200, -v).setUv(1, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  200, v).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -v, 200, v).setUv(0, 1).setColor(red, green, blue, 1);

        buffer.addVertex(pose, -v, -200, v).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  -200, v).setUv(1, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  -200, -v).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -v, -200, -v).setUv(0, 1).setColor(red, green, blue, 1);

        RenderSystem.setShaderTexture(0, texture1);

        buffer.addVertex(pose, -v, -v, -200).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  -v, -200).setUv(1, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,   v, -200).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -v,  v, -200).setUv(0, 1).setColor(red, green, blue, 1);

        buffer.addVertex(pose, -v,  v, 200).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,   v, 200).setUv(0, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, v,  -v, 200).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -v, -v, 200).setUv(1, 0).setColor(red, green, blue, 1);

        buffer.addVertex(pose, 200, v,  -v).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, 200, -v, -v).setUv(1, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, 200, -v,  v).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, 200, v,   v).setUv(0, 1).setColor(red, green, blue, 1);

        buffer.addVertex(pose, -200, v,   v).setUv(1, 1).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -200, -v,  v).setUv(1, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -200, -v, -v).setUv(0, 0).setColor(red, green, blue, 1);
        buffer.addVertex(pose, -200, v,  -v).setUv(0, 1).setColor(red, green, blue, 1);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        return true;
    }
}
