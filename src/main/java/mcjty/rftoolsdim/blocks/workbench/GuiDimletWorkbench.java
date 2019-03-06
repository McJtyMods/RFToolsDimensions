package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.tileentity.GenericEnergyStorageTileEntity;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletCraftingTools;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static mcjty.rftoolsdim.blocks.workbench.DimletWorkbenchTileEntity.PARAM_ID;
import static mcjty.rftoolsdim.blocks.workbench.DimletWorkbenchTileEntity.PARAM_TYPE;

public class GuiDimletWorkbench extends GenericGuiContainer<DimletWorkbenchTileEntity> {
    public static final int WORKBENCH_WIDTH = 256;
    public static final int WORKBENCH_HEIGHT = 244;

    private EnergyBar energyBar;
    private ToggleButton extractButton;

    private TextField searchBar;

    private WidgetList itemList;
    private Slider slider;
    private boolean listDirty = true;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimletworkbench.png");

    public GuiDimletWorkbench(DimletWorkbenchTileEntity dimletWorkbenchTileEntity, DimletWorkbenchContainer container) {
        super(RFToolsDim.instance, RFToolsDimMessages.INSTANCE, dimletWorkbenchTileEntity, container, GuiProxy.GUI_MANUAL_DIMENSION, "create");

        xSize = WORKBENCH_WIDTH;
        ySize = WORKBENCH_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        searchBar = new TextField(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(120, 7, 128, 16)).addTextEvent((widget,string) -> { itemList.setSelected(-1); listDirty = true; });
        itemList = new WidgetList(mc, this)
                .setName("items")
                .setLayoutHint(new PositionalLayout.PositionalHint(120, 25, 118, 133))
                .setLeftMargin(0).setRowheight(-1).addSelectionEvent(new SelectionEvent() {
            @Override
            public void select(Widget widget, int i) {
            }

            @Override
            public void doubleClick(Widget widget, int i) {
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                if (player.isCreative() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
                    cheatDimlet();
                } else {
                    suggestParts();
                }
            }
        });
        slider = new Slider(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(239, 25, 9, 133)).setDesiredWidth(11).setVertical()
                .setScrollableName("items");

        long maxEnergyStored = tileEntity.getCapacity();
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(88, 9, 30, 10)).setShowText(false)
            .setHorizontal();
        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());

        extractButton = new ToggleButton(mc, this)
                .setName("extract")
                .setText("Extract")
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 7, 56, 14))
                .setCheckMarker(true)
                .setTooltips("If on dimlets will be reconstructed into parts");

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(extractButton).addChild(energyBar).addChild(itemList).addChild(slider).addChild(searchBar);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        listDirty = true;

        window = new Window(this, toplevel);

        window.bind(RFToolsDimMessages.INSTANCE, "extract", tileEntity, DimletWorkbenchTileEntity.VALUE_EXTRACT.getName());
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
                    TypedMap.builder()
                            .put(PARAM_TYPE, key.getType().dimletType.getName())
                            .put(PARAM_ID, key.getId())
                            .build());
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
                    TypedMap.builder()
                            .put(PARAM_TYPE, key.getType().dimletType.getName())
                            .put(PARAM_ID, key.getId())
                            .build());
        }
    }

    private void updateList() {
        if (!listDirty) {
            return;
        }
        listDirty = false;
        itemList.removeChildren();

        String filter = searchBar.getText().toLowerCase();

        KnownDimletConfiguration.getKnownDimlets().keySet().stream()
                .filter(key -> dimletMatches(filter, key))
                .sorted()
                .forEachOrdered(this::addItemToList);

        if (itemList.getFirstSelected() >= itemList.getChildCount()) {
            itemList.setFirstSelected(0);
        }
    }

    private boolean dimletMatches(String filter, DimletKey key) {
        return KnownDimletConfiguration.getDisplayName(key).toLowerCase().contains(filter)
                || key.getType().dimletType.getName().toLowerCase().contains(filter);
    }

    private void addItemToList(DimletKey key) {
        Panel panel = new Panel(mc, this).setLayout(new PositionalLayout()).setDesiredWidth(116).setDesiredHeight(16);
        panel.setUserObject(key);
        itemList.addChild(panel);
        BlockRender blockRender = new BlockRender(mc, this).setRenderItem(KnownDimletConfiguration.getDimletStack(key)).setLayoutHint(new PositionalLayout.PositionalHint(1, 0, 16, 16))
                .setUserObject(key);
        panel.addChild(blockRender);
        String displayName = KnownDimletConfiguration.getDisplayName(key);
        AbstractWidget label = new Label(mc, this).setText(displayName).setColor(StyleConfig.colorTextInListNormal).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                .setLayoutHint(new PositionalLayout.PositionalHint(20, 0, 95, 16))
                .setUserObject(key);
        panel.addChild(label);
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
        if (widget != null && !(widget instanceof BlockRender)) {
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

                List<String> tooltips = new ArrayList<>();
                if (Minecraft.getMinecraft().player.isCreative()) {
                    tooltips.add(TextFormatting.RED + "Shift-Double-Click to cheat");
                }
                tooltips.add("Type: " + key.getType().dimletType.getName());
                tooltips.add("Name: " + escapeString(KnownDimletConfiguration.getDisplayName(key)));
                tooltips.add("Key: " + escapeString(key.getId()));
                tooltips.add("Rarity: " + settings.getRarity());
                tooltips.add("@0@1@2");
                tooltips.add("@3@4@5");
                if (essence.isEmpty()) {
                    tooltips.add(TextFormatting.RED + "(currently not craftable)");
                }
                widget.setTooltips(tooltips.toArray(new String[tooltips.size()]));

                widget.setTooltipItems(base, ctrl, energy, memory, typectrl, essence);
//                itemList.setTooltips("Type: " + key.getType().dimletType.getName(), "Rarity: " + settings.getRarity(), "Y: @0 @1 @2 @3 @4 @5");
//                itemList.setTooltipItems(base, ctrl, energy, memory, typectrl, essence);
            }
        }
    }
}
