package mcjty.rftoolsdim.items.modules;

import mcjty.rftools.api.screens.*;
import mcjty.rftools.api.screens.data.IModuleDataContents;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimensionClientScreenModule implements IClientScreenModule<IModuleDataContents> {
    private String line = "";
    private int color = 0xffffff;
    private int rfcolor = 0xffffff;
    private int rfcolorNeg = 0xffffff;
    private boolean hidebar = false;
    private boolean hidetext = false;
    private boolean showdiff = false;
    private boolean showpct = false;
    private FormatStyle format = FormatStyle.MODE_FULL;
    private TextAlign textAlign = TextAlign.ALIGN_LEFT;

    private ITextRenderHelper labelCache = null;
    private ILevelRenderHelper levelCache = null;

    @Override
    public TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void render(IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, IModuleDataContents screenData, ModuleRenderInfo renderInfo) {
        if (labelCache == null) {
            labelCache = renderHelper.createTextRenderHelper()
                    .align(textAlign);
            levelCache = renderHelper.createLevelRenderHelper()
                    .label("RF")
                    .settings(hidebar, hidetext, showpct, showdiff)
                    .color(rfcolor, rfcolorNeg)
                    .gradient(0xffff0000, 0xff333300)
                    .format(format);
        }

        GlStateManager.disableLighting();
        int xoffset;
        if (!line.isEmpty()) {
            labelCache.setup(line, 160, renderInfo);
            labelCache.renderText(0, currenty, color, renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        levelCache.render(xoffset, currenty, screenData, renderInfo);
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:").text("text", "Label text").color("color", "Color for the label").nl()
                .label("RF+:").color("rfcolor", "Color for the RF text").label("RF-:").color("rfcolor_neg", "Color for the negative", "RF/tick ratio").nl()
                .toggleNegative("hidebar", "Bar", "Toggle visibility of the", "energy bar").mode("RF").format("format").nl()
                .label("Dimension:").integer("dim", "The id of the dimension", "to monitor").nl()
                .choices("align", "Label alignment", "Left", "Center", "Right").nl();

    }

    @Override
    public void setupFromNBT(NBTTagCompound tagCompound, int dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.hasKey("color")) {
                color = tagCompound.getInteger("color");
            } else {
                color = 0xffffff;
            }
            if (tagCompound.hasKey("rfcolor")) {
                rfcolor = tagCompound.getInteger("rfcolor");
            } else {
                rfcolor = 0xffffff;
            }
            if (tagCompound.hasKey("rfcolor_neg")) {
                rfcolorNeg = tagCompound.getInteger("rfcolor_neg");
            } else {
                rfcolorNeg = 0xffffff;
            }
            if (tagCompound.hasKey("align")) {
                String alignment = tagCompound.getString("align");
                textAlign = TextAlign.get(alignment);
            } else {
                textAlign = TextAlign.ALIGN_LEFT;
            }

            hidebar = tagCompound.getBoolean("hidebar");
            hidetext = tagCompound.getBoolean("hidetext");
            showdiff = tagCompound.getBoolean("showdiff");
            showpct = tagCompound.getBoolean("showpct");

            format = FormatStyle.values()[tagCompound.getInteger("format")];
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
