package mcjty.rftoolsdim.modules.dimensioneditor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimensioneditor.blocks.DimensionEditorTileEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;

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

    public GuiDimensionEditor(DimensionEditorTileEntity tileEntity, GenericContainer container, Inventory inventory) {
        super(tileEntity, container, inventory, DimensionEditorModule.DIMENSION_EDITOR.get().getManualEntry());

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

        Panel toplevel = positional().background(iconLocation).children(energyBar, arrow, percentage, destroy);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        int pct = tileEntity.getEditPercentage();
        if (pct > 0) {
            arrow.image(iconGuiElements, 144, 0);
        } else {
            arrow.image(iconGuiElements, 192, 0);
        }
        percentage.text(pct + "%");

        destroy.visible(false);
        Slot slot = this.menu.getSlot(DimensionEditorTileEntity.SLOT_INJECTINPUT);
        if (slot.hasItem()) {
            if (slot.getItem().getItem() == Items.TNT) {
                destroy.visible(true);
            }
        }

        drawWindow(matrixStack);
        updateEnergyBar(energyBar);
    }
}
