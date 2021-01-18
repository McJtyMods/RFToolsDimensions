package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.KnowledgeHolderTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.positional;

public class GuiHolder extends GenericGuiContainer<KnowledgeHolderTileEntity, GenericContainer> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 240;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/knowledgeholder.png");

    public GuiHolder(KnowledgeHolderTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, WorkbenchModule.HOLDER.get().getManualEntry());

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
        drawWindow(matrixStack);
    }
}
