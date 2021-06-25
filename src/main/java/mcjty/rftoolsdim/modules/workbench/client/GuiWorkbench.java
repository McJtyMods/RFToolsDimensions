package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.network.PacketRequestDimlets;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity.PARAM_ID;
import static mcjty.rftoolsdim.modules.workbench.blocks.WorkbenchTileEntity.PARAM_TYPE;

public class GuiWorkbench extends GenericGuiContainer<WorkbenchTileEntity, GenericContainer> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 240;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimletworkbench.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    private TextField searchBar;
    private WidgetList itemList;
    private ToggleButton allFilter;
    private long dimletListAge = -1;

    private static String[] pattern = null;

    public GuiWorkbench(WorkbenchTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, WorkbenchModule.WORKBENCH.get().getManualEntry());

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        pattern = null;
    }

    @Override
    public void init() {
        super.init();

        searchBar = textfield(122, 6, 123, 14).event(this::search);
        itemList = list(122, 22, 120, 132).name("widgets").event(new SelectionEvent() {
            @Override
            public void select(int index) {
                hilightPattern();
            }

            @Override
            public void doubleClick(int index) {
                if (Minecraft.getInstance().player.abilities.instabuild && hasShiftDown()) {
                    cheatDimlet();
                } else {
                    suggestParts();
                }
            }
        });
        Slider slider = slider(243, 22, 8, 132).scrollableName("widgets");

        Button createButton = button(210, 178, 40, 18, "Create").event(this::createDimlet);
        allFilter = new ToggleButton().hint(210, 158, 40, 18).text("All").event(this::toggleAll);

        Panel toplevel = positional().background(iconLocation).children(searchBar, itemList, slider, createButton, allFilter);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);
        dimletListAge = -1;

        RFToolsDimMessages.INSTANCE.sendToServer(new PacketRequestDimlets(tileEntity.getBlockPos()));
    }

    private void createDimlet() {
        sendServerCommandTyped(RFToolsDimMessages.INSTANCE, WorkbenchTileEntity.CMD_CREATE_DIMLET,
                TypedMap.builder().build());
    }

    private void hilightPattern() {
        int selected = itemList.getSelected();
        if (selected == -1) {
            return;
        }
        Panel widget = itemList.getChild(selected);
        Object userObject = widget.getUserObject();
        if (userObject instanceof DimletClientHelper.DimletWithInfo) {
            DimletClientHelper.DimletWithInfo key = (DimletClientHelper.DimletWithInfo) userObject;
            if (key.isCraftable()) {
                DimletKey dimlet = key.getDimlet();
                sendServerCommandTyped(RFToolsDimMessages.INSTANCE, WorkbenchTileEntity.CMD_HILIGHT_PATTERN,
                        TypedMap.builder()
                                .put(PARAM_TYPE, dimlet.getType().name())
                                .put(PARAM_ID, dimlet.getKey())
                                .build());
            }
        }
    }

    public static void setPattern(String[] pattern) {
        GuiWorkbench.pattern = pattern;
    }

    private void renderHilightedPattern(MatrixStack matrixStack) {
        if (pattern != null) {
            net.minecraft.client.renderer.RenderHelper.setupFor3DItems();
            matrixStack.pushPose();
            matrixStack.translate(leftPos, topPos, 0.0F);
            RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.enableRescaleNormal();

            itemRenderer.blitOffset = 100.0F;
            GlStateManager._enableDepthTest();
            GlStateManager._disableBlend();
            RenderSystem.enableLighting();

            for (int y = 0 ; y < pattern.length ; y++) {
                String p = pattern[y];
                for (int x = 0 ; x < p.length() ; x++) {
                    ItemStack stack = KnowledgeManager.getPatternItem(p.charAt(x));
                    if (!stack.isEmpty()) {
                        int slotIdx = WorkbenchTileEntity.SLOT_PATTERN + y * pattern.length + x;
                        Slot slot = menu.getSlot(slotIdx);
                        if (!slot.hasItem()) {
                            itemRenderer.renderAndDecorateItem(stack, leftPos + slot.x, topPos + slot.y);

                            RenderSystem.disableLighting();
                            GlStateManager._enableBlend();
                            GlStateManager._disableDepthTest();
                            this.minecraft.getTextureManager().bind(iconGuiElements);
                            RenderHelper.drawTexturedModalRect(matrixStack.last().pose(), slot.x, slot.y, 14 * 16, 3 * 16, 16, 16);
                            GlStateManager._enableDepthTest();
                            GlStateManager._disableBlend();
                            RenderSystem.enableLighting();
                        }
                    }
                }
            }
            itemRenderer.blitOffset = 0.0F;

            matrixStack.popPose();
            net.minecraft.client.renderer.RenderHelper.turnOff();
        }
    }

    private void cheatDimlet() {
        int selected = itemList.getSelected();
        if (selected == -1) {
            return;
        }
        Panel widget = itemList.getChild(selected);
        Object userObject = widget.getUserObject();
        if (userObject instanceof DimletClientHelper.DimletWithInfo) {
            DimletClientHelper.DimletWithInfo key = (DimletClientHelper.DimletWithInfo) userObject;
            DimletKey dimlet = key.getDimlet();
            sendServerCommandTyped(RFToolsDimMessages.INSTANCE, WorkbenchTileEntity.CMD_CHEATDIMLET,
                    TypedMap.builder()
                            .put(PARAM_TYPE, dimlet.getType().name())
                            .put(PARAM_ID, dimlet.getKey())
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
        if (userObject instanceof DimletClientHelper.DimletWithInfo) {
            DimletClientHelper.DimletWithInfo key = (DimletClientHelper.DimletWithInfo) userObject;
            if (key.isCraftable()) {
                DimletKey dimlet = key.getDimlet();
                sendServerCommandTyped(RFToolsDimMessages.INSTANCE, WorkbenchTileEntity.CMD_SUGGESTPARTS,
                        TypedMap.builder()
                                .put(PARAM_TYPE, dimlet.getType().name())
                                .put(PARAM_ID, dimlet.getKey())
                                .build());
            }
        }
    }

    private void toggleAll() {
        dimletListAge = -1;
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

    private boolean dimletMatches(String filter, DimletClientHelper.DimletWithInfo key) {
        if (allFilter.isPressed() || key.isCraftable()) {
            DimletKey dimlet = key.getDimlet();
            String readableName = DimletTools.getReadableName(dimlet);
            return readableName.toLowerCase().contains(filter)
                    || dimlet.getType().name().toLowerCase().contains(filter);
        }
        return false;
    }

    private void addItemToList(DimletClientHelper.DimletWithInfo key) {
        Panel panel = positional().desiredWidth(113).desiredHeight(16).userObject(key);
        itemList.children(panel);
        BlockRender blockRender = new BlockRender().renderItem(DimletTools.getDimletStack(key.getDimlet())).hint(1, 0, 16, 16)
                .userObject(key);
        panel.children(blockRender);
        String displayName = DimletTools.getReadableName(key.getDimlet());
        AbstractWidget label = label(displayName).color(key.isCraftable() ? StyleConfig.colorTextInListNormal : StyleConfig.colorTextDisabled).horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                .hint(20, 0, 95, 16).userObject(key);
        panel.children(label);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        updateList();
        drawWindow(matrixStack);
        renderHilightedPattern(matrixStack);
    }
}
