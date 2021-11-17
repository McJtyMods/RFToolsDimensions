package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.ResearcherTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.label;
import static mcjty.lib.gui.widgets.Widgets.positional;

public class GuiResearcher extends GenericGuiContainer<ResearcherTileEntity, GenericContainer> {

    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    private EnergyBar energyBar;
    private Label progress;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/researcher.png");

    public GuiResearcher(ResearcherTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, WorkbenchModule.HOLDER.get().getManualEntry());

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().name("energybar").vertical().hint(10, 7, 8, 54).showText(false);
        progress = label(64, 44, 70, 18, "");

        Panel toplevel = positional().background(iconLocation).children(energyBar, progress); //.addChild(arrow);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);

        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        updateEnergyBar(energyBar);
        progress.text(tileEntity.getProgressPercentage() + "%");
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateFields();
        drawWindow(matrixStack);
    }
}
