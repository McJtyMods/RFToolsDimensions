package mcjty.rftoolsdim.blocks.builder;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.tileentity.GenericEnergyStorageTileEntity;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiDimensionBuilder extends GenericGuiContainer<DimensionBuilderTileEntity> {
    public static final int BUILDER_WIDTH = 180;
    public static final int BUILDER_HEIGHT = 152;

    private EnergyBar energyBar;
    private ImageLabel stages;
    private Label percentage;
    private Label error1;
    private Label error2;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensionbuilder.png");
    private static final ResourceLocation iconStages = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensionbuilderstages.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsDim.MODID, "textures/gui/guielements.png");

    public GuiDimensionBuilder(DimensionBuilderTileEntity dimensionBuilderTileEntity, DimensionBuilderContainer container) {
        super(RFToolsDim.instance, RFToolsDimMessages.INSTANCE, dimensionBuilderTileEntity, container, GuiProxy.GUI_MANUAL_DIMENSION, "builder");
        GenericEnergyStorageTileEntity.setCurrentRF(dimensionBuilderTileEntity.getStoredPower());

        xSize = BUILDER_WIDTH;
        ySize = BUILDER_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        long maxEnergyStored = tileEntity.getCapacity();
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(10, 7, 8, 54)).setShowText(false);
        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());

        stages = new ImageLabel(mc, this).setImage(iconStages, 0, 0);
        stages.setLayoutHint(new PositionalLayout.PositionalHint(61, 9, 48, 48));

        percentage = new Label(mc, this).setText("0%").setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT);
        percentage.setLayoutHint(new PositionalLayout.PositionalHint(115, 25, 50, 16));
        error1 = new Label(mc, this).setText("").setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT).setColor(0xff0000);
        error1.setLayoutHint(new PositionalLayout.PositionalHint(115, 15, 60, 16));
        error2 = new Label(mc, this).setText("").setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT).setColor(0xff0000);
        error2.setLayoutHint(new PositionalLayout.PositionalHint(115, 28, 60, 16));

        ImageChoiceLabel redstoneMode = initRedstoneMode();

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout()).addChild(energyBar).
                addChild(stages).addChild(percentage).addChild(error1).addChild(error2).addChild(redstoneMode);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
        tileEntity.requestRfFromServer(RFToolsDim.MODID);
        tileEntity.requestBuildingPercentage();

        window.bind(RFToolsDimMessages.INSTANCE, "redstone", tileEntity, GenericTileEntity.VALUE_RSMODE.getName());
    }

    private ImageChoiceLabel initRedstoneMode() {
        ImageChoiceLabel redstoneMode = new ImageChoiceLabel(mc, this).
                setName("redstone").
                addChoice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", iconGuiElements, 0, 0).
                addChoice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", iconGuiElements, 16, 0).
                addChoice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", iconGuiElements, 32, 0);
        redstoneMode.setLayoutHint(new PositionalLayout.PositionalHint(150, 46, 16, 16));
        return redstoneMode;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        int pct = DimensionBuilderTileEntity.getBuildPercentage();

        if (pct == DimensionBuilderTileEntity.ERROR_NOOWNER) {
            error1.setText("Builder has");
            error2.setText("no owner!");
            percentage.setText("");
        } else if (pct == DimensionBuilderTileEntity.ERROR_TOOMANYDIMENSIONS) {
            error1.setText("Too many");
            error2.setText("dimensions!");
            percentage.setText("");
        } else {
            int x = ((pct - 1) / 4) % 5;
            int y = ((pct - 1) / 4) / 5;
            stages.setImage(iconStages, x * 48, y * 48);
            percentage.setText(pct + "%");
            error1.setText("");
            error2.setText("");
        }

        drawWindow();

        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());

        tileEntity.requestRfFromServer(RFToolsDim.MODID);
        tileEntity.requestBuildingPercentage();
    }
}
