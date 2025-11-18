package mcjty.rftoolsdim.dimension.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class BlackSkyRenderer {

    public boolean renderSky() {
        RenderSystem.depthMask(false);
        renderColor(0f, 0f, 0f, 1.0f);

        RenderSystem.depthMask(true);
        return true;
    }

    public static void renderColor(float red, float green, float blue, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(red, green, blue, alpha);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float v = 200.0F;
        buffer.addVertex(-v, -v, -100f).setColor(red, green, blue, alpha);
        buffer.addVertex(v, -v, -100f).setColor(red, green, blue, alpha);
        buffer.addVertex(v, v, -100f).setColor(red, green, blue, alpha);
        buffer.addVertex(-v, v, -100f).setColor(red, green, blue, alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
}
