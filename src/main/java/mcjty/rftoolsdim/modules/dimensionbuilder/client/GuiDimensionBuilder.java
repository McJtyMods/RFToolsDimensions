package mcjty.rftoolsdim.modules.dimensionbuilder.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiDimensionBuilder extends GenericGuiContainer<DimensionBuilderTileEntity, GenericContainer> {
    public static final int BUILDER_WIDTH = 180;
    public static final int BUILDER_HEIGHT = 152;

    private EnergyBar energyBar;
    private ImageLabel stages;
    private Label percentage;
    private Label error1;
    private Label error2;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensionbuilder.png");
    private static final ResourceLocation iconStages = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensionbuilderstages.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    public GuiDimensionBuilder(DimensionBuilderTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, DimensionBuilderModule.DIMENSION_BUILDER.get().getManualEntry());

        imageWidth = BUILDER_WIDTH;
        imageHeight = BUILDER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);
        stages = new ImageLabel().image(iconStages, 0, 0).hint(61, 9, 48, 48);

        percentage = label(115, 25, 50, 16, "0%").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        error1 = label(115, 15, 60, 16, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).color(0xff0000);
        error2 = label(115, 28, 60, 16, "").horizontalAlignment(HorizontalAlignment.ALIGN_LEFT).color(0xff0000);

        ImageChoiceLabel redstoneMode = initRedstoneMode();

        Panel toplevel = positional().background(iconLocation).children(energyBar, stages, percentage, error1, error2, redstoneMode);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);
        window.bind(RFToolsDimMessages.INSTANCE, "redstone", tileEntity, "rsmode");
    }

    private ImageChoiceLabel initRedstoneMode() {
        return imageChoice(150, 46, 16, 16).
                name("redstone").
                choice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", iconGuiElements, 0, 0).
                choice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", iconGuiElements, 16, 0).
                choice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", iconGuiElements, 32, 0);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        int pct = tileEntity.getBuildPercentage();
        int error = tileEntity.getErrorMode();

        if (error == DimensionBuilderTileEntity.ERROR_NOOWNER) {
            error1.text("Builder has");
            error2.text("no owner!");
            percentage.text("");
        } else if (error == DimensionBuilderTileEntity.ERROR_TOOMANYDIMENSIONS) {
            error1.text("Too many");
            error2.text("dimensions!");
            percentage.text("");
        } else if (error == DimensionBuilderTileEntity.ERROR_COLLISION) {
            error1.text("Duplicate");
            error2.text("name!");
            percentage.text("");
        } else {
            int px = ((pct - 1) / 4) % 5;
            int py = ((pct - 1) / 4) / 5;
            stages.image(iconStages, px * 48, py * 48);
            percentage.text(pct + "%");
            error1.text("");
            error2.text("");
        }

        drawWindow(matrixStack);
        updateEnergyBar(energyBar);
    }
}
