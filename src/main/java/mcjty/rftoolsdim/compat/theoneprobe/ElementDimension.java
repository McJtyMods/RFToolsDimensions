package mcjty.rftoolsdim.compat.theoneprobe;

import io.netty.buffer.ByteBuf;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.theoneprobe.api.IElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ElementDimension implements IElement {

    private static final ResourceLocation iconStages = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensionbuilderstages.png");

    private final int pct;

    public ElementDimension(int pct) {
        this.pct = pct;
    }

    public ElementDimension(ByteBuf buf) {
        pct = buf.readInt();
    }

    @Override
    public void render(int x, int y) {
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(iconStages);
        int u = ((pct - 1) / 4) % 5;
        int v = ((pct - 1) / 4) / 5;
        RenderHelper.drawTexturedModalRect(x, y, u * 48, v * 48, 48, 48);
    }

    @Override
    public int getWidth() {
        return 48;
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pct);
    }

    @Override
    public int getID() {
        return TheOneProbeSupport.ELEMENT_DIMENSION;
    }
}
