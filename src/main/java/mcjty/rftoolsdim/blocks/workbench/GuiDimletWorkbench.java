package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.entity.GenericEnergyStorageTileEntity;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.network.Argument;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletCraftingTools;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiDimletWorkbench extends GenericGuiContainer<DimletWorkbenchTileEntity> {
    public static final int WORKBENCH_WIDTH = 256;
    public static final int WORKBENCH_HEIGHT = 224;

    private EnergyBar energyBar;
    private Button extractButton;

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

        searchBar = new TextField(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(120, 7, 128, 16)).addTextEvent((widget,string) -> { itemList.setSelected(-1); listDirty = true; });
        itemList = new WidgetList(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(120, 25, 118, 108)).
                setLeftMargin(0).setRowheight(-1).addSelectionEvent(new SelectionEvent() {
            @Override
            public void select(Widget widget, int i) {
            }

            @Override
            public void doubleClick(Widget widget, int i) {
                suggestParts();
            }
        });
        slider = new Slider(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(239, 25, 9, 108)).setDesiredWidth(11).setVertical().setScrollable(itemList);

        int maxEnergyStored = tileEntity.getMaxEnergyStored(EnumFacing.DOWN);
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(80, 9, 38, 10)).setShowText(false)
            .setHorizontal();
        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());

        extractButton = new Button(mc, this).setText("Extract").setLayoutHint(new PositionalLayout.PositionalHint(30, 7, 48, 14)).addButtonEvent(
                parent -> extractDimlet()
        ).setTooltips("Deconstruct a dimlet into its parts");

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(extractButton).addChild(energyBar).addChild(itemList).addChild(slider).addChild(searchBar);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        listDirty = true;

        window = new Window(this, toplevel);
    }

    private void suggestParts() {
        int selected = itemList.getSelected();
        if (selected == -1) {
            return;
        }
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
        List<DimletKey> keys = dimlets.keySet().stream().filter(key -> KnownDimletConfiguration.getDisplayName(key).toLowerCase().contains(filter)).collect(Collectors.toList());
        keys.sort((a, b) -> {
            int rc = a.getType().compareTo(b.getType());
            if (rc == 0) {
                return a.getId().compareTo(b.getId());
            } else {
                return rc;
            }
        });
        keys.stream().forEach(key -> addItemToList(key, itemList));
        if (itemList.getFirstSelected() >= itemList.getChildCount()) {
            itemList.setFirstSelected(0);
        }
    }

    private void addItemToList(DimletKey key, WidgetList itemList) {
        Panel panel = new Panel(mc, this).setLayout(new PositionalLayout()).setDesiredWidth(116).setDesiredHeight(16);
        panel.setUserObject(key);
        itemList.addChild(panel);
        BlockRender blockRender = new BlockRender(mc, this).setRenderItem(KnownDimletConfiguration.getDimletStack(key)).setLayoutHint(new PositionalLayout.PositionalHint(1, 0, 16, 16))
                .setUserObject(key);
        panel.addChild(blockRender);
        String displayName = KnownDimletConfiguration.getDisplayName(key);
        AbstractWidget label = new Label(mc, this).setText(displayName).setColor(StyleConfig.colorTextInListNormal).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT)
                .setLayoutHint(new PositionalLayout.PositionalHint(20, 0, 95, 16))
                .setUserObject(key);
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
            extractButton.setText("Extract");
        } else {
            switch (extracting % 4) {
                case 0: extractButton.setText("."); break;
                case 1: extractButton.setText(".."); break;
                case 2: extractButton.setText("..."); break;
                case 3: extractButton.setText("...."); break;
            }
            extractButton.setEnabled(false);
        }

        updateList();
        setDimletTooltip();
        drawWindow();

        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());

        tileEntity.requestRfFromServer(RFToolsDim.MODID);
        tileEntity.requestExtractingFromServer();
    }

    private void setDimletTooltip() {
        itemList.setTooltips("All known dimlets");
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        Widget widget = window.getToplevel().getWidgetAtPosition(x, y);
        if (widget != null) {
            Object userObject = widget.getUserObject();
            if (userObject instanceof DimletKey) {
                DimletKey key = (DimletKey) userObject;
                Settings settings = KnownDimletConfiguration.getSettings(key);
                int rarity = settings.getRarity();
                int level = DimletCraftingTools.calculateItemLevelFromRarity(rarity);
                ItemStack base = new ItemStack(ModItems.dimletBaseItem, 1);
                ItemStack ctrl = new ItemStack(ModItems.dimletControlCircuitItem, 1, rarity);
                ItemStack energy = new ItemStack(ModItems.dimletEnergyModuleItem, 1, level);
                ItemStack memory = new ItemStack(ModItems.dimletMemoryUnitItem, 1, level);
                ItemStack typectrl = new ItemStack(ModItems.dimletTypeControllerItem, 1, key.getType().ordinal());
                ItemStack essence = key.getType().dimletType.getDefaultEssence();

                widget.setTooltips("Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "@0@1@2", "@3@4@5");
                widget.setTooltipItems(base, ctrl, energy, memory, typectrl, essence);
//                itemList.setTooltips("Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "Y: @0 @1 @2 @3 @4 @5");
//                itemList.setTooltipItems(base, ctrl, energy, memory, typectrl, essence);
            }
        }
    }
}
