package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.Argument;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiDimletWorkbench extends GenericGuiContainer<DimletWorkbenchTileEntity> {
    public static final int WORKBENCH_WIDTH = 256;
    public static final int WORKBENCH_HEIGHT = 224;

    private EnergyBar energyBar;
    private Button extractButton;
    private ImageLabel progressIcon;

    private TextField searchBar;

    private WidgetList itemList;
    private Slider slider;
    private boolean listDirty = true;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimletworkbench.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsDim.MODID, "textures/gui/guielements.png");

    public GuiDimletWorkbench(DimletWorkbenchTileEntity dimletWorkbenchTileEntity, DimletWorkbenchContainer container) {
        super(RFToolsDim.instance, RFToolsDimMessages.INSTANCE, dimletWorkbenchTileEntity, container, RFToolsDim.GUI_MANUAL_DIMENSION, "create");

        xSize = WORKBENCH_WIDTH;
        ySize = WORKBENCH_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        searchBar = new TextField(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(145, 7, 112, 18)).addTextEvent((widget,string) -> { listDirty = true; });
        itemList = new WidgetList(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(145, 25, 100, 108)).setNoSelectionMode(true).setUserObject(new Integer(-1)).
                setLeftMargin(0).setRowheight(-1).addSelectionEvent(new SelectionEvent() {
            @Override
            public void select(Widget widget, int i) {
            }

            @Override
            public void doubleClick(Widget widget, int i) {
                suggestParts();
            }
        });
        slider = new Slider(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(246, 25, 11, 108)).setDesiredWidth(11).setVertical().setScrollable(itemList);

        int maxEnergyStored = tileEntity.getMaxEnergyStored(EnumFacing.DOWN);
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(8, 142, 8, 54)).setShowText(false);
        energyBar.setValue(tileEntity.getCurrentRF());

        progressIcon = new ImageLabel(mc, this).setImage(iconGuiElements, 4 * 16, 16);
        progressIcon.setLayoutHint(new PositionalLayout.PositionalHint(135, 6, 16, 16));

        extractButton = new Button(mc, this).setText("Extract").setLayoutHint(new PositionalLayout.PositionalHint(36, 7, 55, 14)).addButtonEvent(
                new ButtonEvent() {
                    @Override
                    public void buttonClicked(Widget parent) {
                        extractDimlet();
                    }
                }
        ).setTooltips("Deconstruct a dimlet into its parts");

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(extractButton).addChild(energyBar).addChild(progressIcon).addChild(itemList).addChild(slider).addChild(searchBar);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        listDirty = true;

        window = new Window(this, toplevel);
    }

    private void suggestParts() {
        int selected = itemList.getSelected();
        Widget widget = itemList.getChild(selected);
        Object userObject = widget.getUserObject();
        if (userObject instanceof DimletKey) {
            DimletKey key = (DimletKey) userObject;
            sendServerCommand(RFToolsDimMessages.INSTANCE, DimletWorkbenchTileEntity.CMD_SUGGESTPARTS,
                              new Argument("type", key.getType().dimletType.getName()),
                              new Argument("id", key.getId()));
        }
    }

    private void updateList() {
        if (!listDirty) {
            return;
        }
        listDirty = false;
        itemList.removeChildren();
        Map<DimletKey, Settings> dimlets = KnownDimletConfiguration.getKnownDimlets();
        String filter = searchBar.getText().toLowerCase();
        List<DimletKey> keys = dimlets.keySet().stream().filter(key -> key.getId().toLowerCase().contains(filter)).collect(Collectors.toList());
        keys.sort((a, b) -> {
            int rc = a.getType().compareTo(b.getType());
            if (rc == 0) {
                return a.getId().compareTo(b.getId());
            } else {
                return rc;
            }
        });
        keys.stream().forEach(key -> addItemToList(new DimletKey(DimletType.DIMLET_MATERIAL, Blocks.diamond_block.getRegistryName()), itemList));
    }

    private void addItemToList(DimletKey key, WidgetList itemList) {
        Panel panel = new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(5)).setDesiredHeight(12).setUserObject(new Integer(-1)).setDesiredHeight(16);
        panel.setUserObject(key);
        itemList.addChild(panel);
        BlockRender blockRender = new BlockRender(mc, this).setRenderItem(KnownDimletConfiguration.getDimletStack(key)).setOffsetX(-1).setOffsetY(-1);
        panel.addChild(blockRender);
        String displayName = KnownDimletConfiguration.getDisplayName(key);
        AbstractWidget label = new Label(mc, this).setText(displayName).setColor(StyleConfig.colorTextInListNormal).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setDesiredWidth(80).setUserObject(new Integer(-1));
        panel.addChild(label);
    }

    private void extractDimlet() {
        Slot slot = inventorySlots.getSlot(DimletWorkbenchContainer.SLOT_INPUT);
        if (slot.getStack() != null) {
            ItemStack itemStack = slot.getStack();
            if (ModItems.knownDimletItem.equals(itemStack.getItem())) {
                DimletKey key = KnownDimletConfiguration.getDimletKey(itemStack);
                if (!KnownDimletConfiguration.isCraftable(key)) {
//                    Achievements.trigger(Minecraft.getMinecraft().thePlayer, Achievements.smallBits);
                    sendServerCommand(RFToolsDimMessages.INSTANCE, DimletWorkbenchTileEntity.CMD_STARTEXTRACT);
                }
            }
        }
    }

    private void enableButtons() {
        boolean enabled = false;
        Slot slot = inventorySlots.getSlot(DimletWorkbenchContainer.SLOT_INPUT);
        if (slot.getStack() != null) {
            ItemStack itemStack = slot.getStack();
            if (ModItems.knownDimletItem.equals(itemStack.getItem())) {
                DimletKey key = KnownDimletConfiguration.getDimletKey(itemStack);
                if (!KnownDimletConfiguration.isCraftable(key)) {
                    enabled = true;
                }
            }
        }
        extractButton.setEnabled(enabled);
    }



    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        enableButtons();

        int extracting = tileEntity.getExtracting();
        if (extracting == 0) {
            progressIcon.setImage(iconGuiElements, 4 * 16, 16);
        } else {
            progressIcon.setImage(iconGuiElements, (extracting % 4) * 16, 16);
        }

        updateList();
        drawWindow();

        energyBar.setValue(tileEntity.getCurrentRF());

        tileEntity.requestRfFromServer(RFToolsDim.MODID);
        tileEntity.requestExtractingFromServer();
    }
}
