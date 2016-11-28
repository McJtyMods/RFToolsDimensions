package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.entity.GenericEnergyStorageTileEntity;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.network.Argument;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.tools.MinecraftTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletCraftingTools;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiDimletWorkbench extends GenericGuiContainer<DimletWorkbenchTileEntity> {
    public static final int WORKBENCH_WIDTH = 256;
    public static final int WORKBENCH_HEIGHT = 224;

    private EnergyBar energyBar;
    private ToggleButton extractButton;

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
                EntityPlayerSP player = MinecraftTools.getPlayer(Minecraft.getMinecraft());
                if (player.isCreative() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
                    cheatDimlet();
                } else {
                    suggestParts();
                }
            }
        });
        slider = new Slider(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(239, 25, 9, 108)).setDesiredWidth(11).setVertical().setScrollable(itemList);

        int maxEnergyStored = tileEntity.getMaxEnergyStored(EnumFacing.DOWN);
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(88, 9, 30, 10)).setShowText(false)
            .setHorizontal();
        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());

        extractButton = new ToggleButton(mc, this).setText("Extract")
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 7, 56, 14))
                .setCheckMarker(true)
                .addButtonEvent(parent -> setExtractMode()
        ).setTooltips("If on dimlets will be reconstructed into parts");
        extractButton.setPressed(tileEntity.isExtractMode());

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(extractButton).addChild(energyBar).addChild(itemList).addChild(slider).addChild(searchBar);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        listDirty = true;

        window = new Window(this, toplevel);
    }

    private void cheatDimlet() {
        int selected = itemList.getSelected();
        if (selected == -1) {
            return;
        }
        Widget widget = itemList.getChild(selected);
        Object userObject = widget.getUserObject();
        if (userObject instanceof DimletKey) {
            DimletKey key = (DimletKey) userObject;
            sendServerCommand(RFToolsDimMessages.INSTANCE, DimletWorkbenchTileEntity.CMD_CHEATDIMLET,
                    new Argument("type", key.getType().dimletType.getName()),
                    new Argument("id", key.getId()));
        }
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

        // First remove all dimlets with the same name and also apply the filter already
        Map<Pair<DimletType, String>, DimletKey> uniquelyNamedDimlets = new HashMap<>();
        for (DimletKey key : dimlets.keySet()) {
            String name = KnownDimletConfiguration.getDisplayName(key);
            if (name.toLowerCase().contains(filter)) {
                Pair<DimletType, String> k = Pair.of(key.getType(), name);
                if (!uniquelyNamedDimlets.containsKey(k)) {
                    uniquelyNamedDimlets.put(k, key);
                }
            }
        }

        List<DimletKey> keys = new ArrayList<>(uniquelyNamedDimlets.values());
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

    private void setExtractMode() {
        tileEntity.setExtractMode(extractButton.isPressed());
        sendServerCommand(RFToolsDimMessages.INSTANCE, DimletWorkbenchTileEntity.CMD_EXTRACTMODE,
                new Argument("mode", extractButton.isPressed()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        int extracting = tileEntity.getExtracting();
        if (extracting == 0) {
            extractButton.setText("Extract");
            extractButton.setEnabled(true);
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
                ItemStack essence = key.getType().dimletType.getDefaultEssence(key);

                if (MinecraftTools.getPlayer(Minecraft.getMinecraft()).isCreative()) {
                    if (ItemStackTools.isEmpty(essence)) {
                        widget.setTooltips(TextFormatting.RED + "Shift-Double-Click to cheat", "Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "@0@1@2", "@3@4@5",
                                TextFormatting.RED + "(currently not craftable)");
                    } else {
                        widget.setTooltips(TextFormatting.RED + "Shift-Double-Click to cheat", "Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "@0@1@2", "@3@4@5");
                    }
                } else {
                    if (ItemStackTools.isEmpty(essence)) {
                        widget.setTooltips("Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "@0@1@2", "@3@4@5",
                            TextFormatting.RED + "(currently not craftable)");
                    } else {
                        widget.setTooltips("Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "@0@1@2", "@3@4@5");
                    }
                }
                widget.setTooltipItems(base, ctrl, energy, memory, typectrl, essence);
//                itemList.setTooltips("Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "Y: @0 @1 @2 @3 @4 @5");
//                itemList.setTooltipItems(base, ctrl, energy, memory, typectrl, essence);
            }
        }
    }
}
