package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.positional;

public class GuiWorkbench extends GenericGuiContainer<WorkbenchTileEntity, GenericContainer> {

    public static final int WIDTH = 180;
    public static final int HEIGHT = 244;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimletworkbench.png");

    public GuiWorkbench(WorkbenchTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, WorkbenchModule.WORKBENCH.get().getManualEntry());

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = positional().background(iconLocation);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
//        int pct = DimensionBuilderTileEntity.getBuildPercentage();
//
//        if (pct == DimensionBuilderTileEntity.ERROR_NOOWNER) {
//            error1.setText("Builder has");
//            error2.setText("no owner!");
//            percentage.setText("");
//        } else if (pct == DimensionBuilderTileEntity.ERROR_TOOMANYDIMENSIONS) {
//            error1.setText("Too many");
//            error2.setText("dimensions!");
//            percentage.setText("");
//        } else {
//            int x = ((pct - 1) / 4) % 5;
//            int y = ((pct - 1) / 4) / 5;
//            stages.setImage(iconStages, x * 48, y * 48);
//            percentage.setText(pct + "%");
//            error1.setText("");
//            error2.setText("");
//        }

        drawWindow(matrixStack);

//        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());
//
//        tileEntity.requestRfFromServer(RFToolsDim.MODID);
//        tileEntity.requestBuildingPercentage();
    }
}
