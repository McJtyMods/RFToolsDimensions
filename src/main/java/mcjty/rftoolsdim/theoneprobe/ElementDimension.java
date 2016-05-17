package mcjty.rftoolsdim.theoneprobe;

import io.netty.buffer.ByteBuf;
import mcjty.lib.gui.RenderHelper;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.theoneprobe.api.Cursor;
import mcjty.theoneprobe.api.IElement;
import net.minecraft.client.Minecraft;
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
    public void render(Cursor cursor) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(iconStages);
        int x = ((pct - 1) / 4) % 5;
        int y = ((pct - 1) / 4) / 5;
        RenderHelper.drawTexturedModalRect(cursor.getX(), cursor.getY(), x * 48, y * 48, 48, 48);
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
