package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.network.PacketRequestDimlets;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity.PARAM_ID;
import static mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity.PARAM_TYPE;

public class GuiWorkbench extends GenericGuiContainer<WorkbenchTileEntity, GenericContainer> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 240;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimletworkbench.png");

    private TextField searchBar;
    private WidgetList itemList;
    private Slider slider;
    private long dimletListAge = -1;

    public GuiWorkbench(WorkbenchTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, WorkbenchModule.WORKBENCH.get().getManualEntry());

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        searchBar = textfield(122, 6, 123, 14).event(this::search);
        itemList = list(122, 22, 120, 132).name("widgets").event(new SelectionEvent() {
            @Override
            public void select(int index) {
            }

            @Override
            public void doubleClick(int index) {
                if (Minecraft.getInstance().player.abilities.isCreativeMode && hasShiftDown()) {
                    cheatDimlet();
                } else {
                    suggestParts();
                }
            }
        });
        slider = slider(243, 22, 8, 132).scrollableName("widgets");

        Panel toplevel = positional().background(iconLocation).children(searchBar, itemList, slider);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);
        dimletListAge = -1;
        RFToolsDimMessages.INSTANCE.sendToServer(new PacketRequestDimlets());
    }

    private void cheatDimlet() {
        int selected = itemList.getSelected();
        if (selected == -1) {
            return;
        }
        Panel widget = itemList.getChild(selected);
        Object userObject = widget.getUserObject();
        if (userObject instanceof DimletKey) {
            DimletKey key = (DimletKey) userObject;
            sendServerCommandTyped(RFToolsDimMessages.INSTANCE, WorkbenchTileEntity.CMD_CHEATDIMLET,
                    TypedMap.builder()
                            .put(PARAM_TYPE, key.getType().name())
                            .put(PARAM_ID, key.getKey())
                            .build());
        }
    }

    private void suggestParts() {
        int selected = itemList.getSelected();
        if (selected == -1) {
            return;
        }
        Panel widget = itemList.getChild(selected);
        Object userObject = widget.getUserObject();
        if (userObject instanceof DimletKey) {
            DimletKey key = (DimletKey) userObject;
            sendServerCommandTyped(RFToolsDimMessages.INSTANCE, WorkbenchTileEntity.CMD_SUGGESTPARTS,
                    TypedMap.builder()
                            .put(PARAM_TYPE, key.getType().name())
                            .put(PARAM_ID, key.getKey())
                            .build());
        }
    }


    private void search(String filter) {
        dimletListAge = -1;
    }

    private void updateList() {
        if (dimletListAge == DimletClientHelper.dimletListAge) {
            return;
        }
        dimletListAge = DimletClientHelper.dimletListAge;

        itemList.removeChildren();

        String filter = searchBar.getText().toLowerCase();
        DimletClientHelper.dimlets.stream()
                .filter(key -> dimletMatches(filter, key))
                .sorted()
                .forEachOrdered(this::addItemToList);

        if (itemList.getFirstSelected() >= itemList.getChildCount()) {
            itemList.setFirstSelected(0);
        }
    }

    private boolean dimletMatches(String filter, DimletKey key) {
        String readableName = DimletTools.getReadableName(key);
        return readableName.toLowerCase().contains(filter)
                || key.getType().name().toLowerCase().contains(filter);
    }

    private void addItemToList(DimletKey key) {
        Panel panel = positional().desiredWidth(113).desiredHeight(16).userObject(key);
        itemList.children(panel);
        BlockRender blockRender = new BlockRender().renderItem(DimletTools.getDimletStack(key)).hint(1, 0, 16, 16)
                .userObject(key);
        panel.children(blockRender);
        String displayName = DimletTools.getReadableName(key);
        AbstractWidget label = label(displayName).color(StyleConfig.colorTextInListNormal).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                .hint(20, 0, 100, 16).userObject(key);
        panel.children(label);
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

        updateList();
        drawWindow(matrixStack);

//        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());
//
//        tileEntity.requestRfFromServer(RFToolsDim.MODID);
//        tileEntity.requestBuildingPercentage();
    }
}
