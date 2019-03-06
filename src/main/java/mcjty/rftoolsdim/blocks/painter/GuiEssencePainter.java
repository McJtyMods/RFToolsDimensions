package mcjty.rftoolsdim.blocks.painter;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.varia.Counter;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.ControllerType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.types.SpecialType;
import mcjty.rftoolsdim.dimensions.types.TerrainType;
import mcjty.rftoolsdim.gui.GuiProxy;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiEssencePainter extends GenericGuiContainer<EssencePainterTileEntity> {
    public static final int PAINTER_WIDTH = 256;
    public static final int PAINTER_HEIGHT = 224;

    private Button extractButton;
    private Button storeButton;
    private TextField nameField;
    private Label validateField;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/essencepainter.png");

    public GuiEssencePainter(EssencePainterTileEntity tileEntity, EssencePainterContainer container) {
        super(RFToolsDim.instance, RFToolsDimMessages.INSTANCE, tileEntity, container, GuiProxy.GUI_MANUAL_DIMENSION, "enscriber");

        xSize = PAINTER_WIDTH;
        ySize = PAINTER_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        extractButton = new Button(mc, this).setText("Extract").setLayoutHint(new PositionalLayout.PositionalHint(13, 164, 60, 16)).addButtonEvent(
            parent -> {
                extractDimlets();
            }
        ).setTooltips("Extract the dimlets out of", "a realized dimension tab");
        storeButton = new Button(mc, this).setText("Store").setLayoutHint(new PositionalLayout.PositionalHint(13, 182, 60, 16)).addButtonEvent(
            parent -> {
                storeDimlets();
            }
        ).setTooltips("Store dimlets in a", "empty dimension tab");
        nameField = new TextField(mc, this).addTextEvent(
            (parent, newText) -> {
                storeName(newText);
            }
        ).setLayoutHint(new PositionalLayout.PositionalHint(13, 200, 60, 16));
        validateField = new Label(mc, this).setText("Val");
        validateField.setTooltips("Hover here for errors...");
        validateField.setLayoutHint(new PositionalLayout.PositionalHint(35, 142, 38, 16));

        setNameFromDimensionTab();

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout()).addChild(extractButton).addChild(storeButton).
                addChild(nameField).addChild(validateField);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void storeName(String name) {
//        sendServerCommand(RFToolsDimMessages.INSTANCE, EssencePainterTileEntity.CMD_SETNAME, new Argument("name", name));
    }

    private void extractDimlets() {
//        for (int i = 0; i < EssencePainterContainer.SIZE_DIMLETS ; i++) {
//            ItemStack stack = inventorySlots.getSlot(i + EssencePainterContainer.SLOT_DIMLETS).getStack();
//            if (!stack.isEmpty()) {
//                // Cannot extract. There are still items in the way.
//                Logging.warn(mc.player, "You cannot extract. Remove all dimlets first!");
//                return;
//            }
//        }
//        sendServerCommand(RFToolsDimMessages.INSTANCE, EssencePainterTileEntity.CMD_EXTRACT);
    }

    private void storeDimlets() {
//        sendServerCommand(RFToolsDimMessages.INSTANCE, EssencePainterTileEntity.CMD_STORE, new Argument("name", nameField.getText()));
    }

    private void enableButtons() {
        Slot slot = inventorySlots.getSlot(EssencePainterContainer.SLOT_TAB);
        extractButton.setEnabled(false);
        storeButton.setEnabled(false);
        if (!slot.getStack().isEmpty()) {
            if (slot.getStack().getItem() == ModItems.emptyDimensionTabItem) {
                storeButton.setEnabled(true);
            } else if (slot.getStack().getItem() == ModItems.realizedDimensionTabItem) {
                extractButton.setEnabled(true);
            }
        }
    }

    private List<DimletKey> extractModifiersForType(List<DimletKey> modifiers, DimletType type) {
        List<DimletKey> modifiersForType = new ArrayList<>();
        int i = 0;
        while (i < modifiers.size()) {
            DimletKey modifier = modifiers.get(i);
            if (type.dimletType.isModifiedBy(modifier.getType())) {
                modifiersForType.add(modifier);
                modifiers.remove(i);
            } else {
                i++;
            }
        }
        return modifiersForType;
    }

    private String shortenName(String name) {
        int idx = name.indexOf('_');
        if (idx == -1) {
            return name;
        } else {
            return name.substring(idx+1);
        }
    }

    private void validateDimlets() {
        List<String> tooltips = new ArrayList<>();

        TerrainType terrainType = null;
        int cntTerrain = 0;
        int cntBiomes = 0;
        int cntController = 0;
        int cntOwner = 0;
        for (int i = EssencePainterContainer.SLOT_DIMLETS; i < EssencePainterContainer.SLOT_TAB ; i++) {
            Slot slot = inventorySlots.getSlot(i);
            if (slot != null && !slot.getStack().isEmpty()) {
                ItemStack stack = slot.getStack();
                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
                if (key.getType() == DimletType.DIMLET_TERRAIN) {
                    cntTerrain++;
                    terrainType = DimletObjectMapping.getTerrain(key);
                } else if (key.getType() == DimletType.DIMLET_BIOME) {
                    cntBiomes++;
                } else if (key.getType() == DimletType.DIMLET_CONTROLLER) {
                    cntController++;
                } else if (key.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(key) == SpecialType.SPECIAL_OWNER) {
                    cntOwner++;
                }
            }
        }
        if (cntOwner > 1) {
            tooltips.add("Using more then one owner dimlet is not useful!");
        }
        if (GeneralConfiguration.ownerDimletsNeeded && cntOwner != 1) {
            tooltips.add("You cannot make a dimension without an owner dimlet!");
            storeButton.setEnabled(false);
        }
        if (cntTerrain > 1) {
            tooltips.add("Using more then one TERRAIN is not useful!");
            terrainType = null;
        }
        if (cntController > 1) {
            tooltips.add("Using more then one CONTROLLER is not useful!");
        }

        List<DimletKey> modifiers = new ArrayList<>();
        for (int i = EssencePainterContainer.SLOT_DIMLETS; i < EssencePainterContainer.SLOT_TAB ; i++) {
            Slot slot = inventorySlots.getSlot(i);
            if (slot != null && !slot.getStack().isEmpty()) {
                ItemStack stack = slot.getStack();
                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
                DimletType type = key.getType();
                if (type.dimletType.isModifier()) {
                    modifiers.add(key);
                } else {
                    List<DimletKey> modifiersForType = extractModifiersForType(modifiers, type);
                    if (type == DimletType.DIMLET_TERRAIN) {
                        if (DimletObjectMapping.getTerrain(key) == TerrainType.TERRAIN_VOID && !modifiersForType.isEmpty()) {
                            tooltips.add("VOID terrain cannot use modifiers");
                        }
                    } else if (type == DimletType.DIMLET_FEATURE) {
                        FeatureType featureType = DimletObjectMapping.getFeature(key);
                        Counter<DimletType> modifierAmountUsed = new Counter<>();
                        for (DimletKey modifier : modifiersForType) {
                            modifierAmountUsed.increment(modifier.getType());
                        }
                        for (Map.Entry<DimletType, Integer> entry : modifierAmountUsed.entrySet()) {
                            Integer amountSupported = featureType.getSupportedModifierAmount(entry.getKey());
                            if (amountSupported == null) {
                                tooltips.add(shortenName(featureType.name()) + " does not use " + shortenName(entry.getKey().name()) + " modifiers!");
                            } else if (amountSupported == 1 && entry.getValue() > 1) {
                                tooltips.add(shortenName(featureType.name()) + " only needs one " + shortenName(entry.getKey().name()) + " modifier!");
                            }
                        }

                        if (terrainType == null && !featureType.supportsAllTerrains()) {
                            tooltips.add(shortenName(featureType.name()) + " is possibly useless as it does not work on all terrains!");
                        }
                        if (terrainType != null && !featureType.isTerrainSupported(terrainType)) {
                            tooltips.add(shortenName(featureType.name()) + " does not work for terrain " + shortenName(terrainType.name()) + "!");
                        }
                    } else if (type == DimletType.DIMLET_CONTROLLER) {
                        ControllerType controllerType = DimletObjectMapping.getController(key);
                        int neededBiomes = controllerType.getNeededBiomes();
                        if (neededBiomes != -1) {
                            if (cntBiomes > neededBiomes) {
                                tooltips.add("Too many biomes specified for " + shortenName(controllerType.name()) + "!");
                            } else if (cntBiomes < neededBiomes) {
                                tooltips.add("Too few biomes specified for " + shortenName(controllerType.name()) + "!");
                            }
                        }
                    }
                }
            }
        }

        if (!modifiers.isEmpty()) {
            tooltips.add("There are dangling modifiers in this descriptor");
        }

        boolean error = true;
        if (tooltips.isEmpty()) {
            tooltips.add("Everything appears to be alright");
            error = false;
        }
        validateField.setTooltips(tooltips.toArray(new String[tooltips.size()]));
        validateField.setColor(error ? 0xFF0000 : 0x008800);
        validateField.setText(error ? "Warn" : "Ok");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        enableButtons();
        validateDimlets();

        if (tileEntity.hasTabSlotChangedAndClear()) {
            setNameFromDimensionTab();
        }

        drawWindow();
    }

    private void setNameFromDimensionTab() {
        Slot slot = inventorySlots.getSlot(EssencePainterContainer.SLOT_TAB);
        if (!slot.getStack().isEmpty() && slot.getStack().getItem() == ModItems.realizedDimensionTabItem) {
            NBTTagCompound tagCompound = slot.getStack().getTagCompound();
            if (tagCompound != null) {
                String name = tagCompound.getString("name");
                if (name != null) {
                    nameField.setText(name);
                }
            }
        }
    }
}
