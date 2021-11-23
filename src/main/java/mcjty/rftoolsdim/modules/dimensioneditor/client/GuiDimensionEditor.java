package mcjty.rftoolsdim.modules.dimensioneditor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiDimensionEditor extends GenericGuiContainer<DimensionEditorTileEntity, GenericContainer> {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    private EnergyBar energyBar;
    private ImageLabel arrow;
    private Label percentage;
    private Label destroy;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensioneditor.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsDim.MODID, "textures/gui/guielements.png");

    public GuiDimensionEditor(DimensionEditorTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, DimensionBuilderModule.DIMENSION_BUILDER.get().getManualEntry());

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);

        arrow = new ImageLabel().image(iconGuiElements, 192, 0).hint(90, 26, 16, 16);

        destroy = label(30, 53, 150, 16, "Destroying dimension!").color(0xff0000);
        destroy.visible(false);

        percentage = label(80, 43, 40, 16, "0%");

        Panel toplevel = horizontal().background(iconLocation).children(energyBar, arrow, percentage, destroy);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);
//        tileEntity.requestBuildingPercentage();
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
//        int pct = DimensionEditorTileEntity.getEditPercentage();
//        if (pct > 0) {
//            arrow.setImage(iconGuiElements, 144, 0);
//        } else {
//            arrow.setImage(iconGuiElements, 192, 0);
//        }
//        percentage.setText(pct + "%");
//
//        drawWindow();
//
//        destroy.setVisible(false);
//        Slot slot = this.inventorySlots.getSlot(DimensionEditorContainer.SLOT_INJECTINPUT);
//        if (slot.getHasStack()) {
//            Block block = BlockTools.getBlock(slot.getStack());
//            if (block == Blocks.TNT) {
//                destroy.setVisible(true);
//            }
//        }

        drawWindow(matrixStack);
        updateEnergyBar(energyBar);
    }
}
