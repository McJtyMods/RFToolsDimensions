package mcjty.rftoolsdim.modules.enscriber.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.enscriber.blocks.EnscriberTileEntity;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsdim.modules.enscriber.blocks.EnscriberTileEntity.PARAM_NAME;

public class GuiEnscriber extends GenericGuiContainer<EnscriberTileEntity, GenericContainer> {
    public static final int ENSCRIBER_WIDTH = 256;
    public static final int ENSCRIBER_HEIGHT = 224;

    private Button extractButton;
    private Button storeButton;
    private TextField nameField;
    private Label validateField;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsDim.MODID, "textures/gui/dimensionenscriber.png");

    public GuiEnscriber(EnscriberTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, EnscriberModule.ENSCRIBER.get().getManualEntry());

        xSize = ENSCRIBER_WIDTH;
        ySize = ENSCRIBER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        extractButton = button(12, 164, 60, 16, "Extract")
                .name("extract")
                .event(this::extractDimlets)
                .tooltips("Extract the dimlets out of", "a realized dimension tab");
        storeButton = button(13, 182, 60, 16, "Store")
                .event(this::storeDimlets)
                .tooltips("Store dimlets in a", "empty dimension tab");
        nameField = textfield(13, 200, 60, 16)
                .name("name");
        validateField = label(35, 142, 38, 16, "Val")
                .tooltips("Hover here for errors...");

        setNameFromDimensionTab();

        Panel toplevel = positional().background(iconLocation).children(extractButton, storeButton, nameField, validateField);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);
        window.bind(RFToolsDimMessages.INSTANCE, "name", tileEntity, EnscriberTileEntity.VALUE_NAME.getName());
    }

    private void extractDimlets() {
        for (int i = 0 ; i < EnscriberTileEntity.SIZE_DIMLETS ; i++) {
            ItemStack stack = container.inventorySlots.get(i + EnscriberTileEntity.SLOT_DIMLETS).getStack();
            if (!stack.isEmpty()) {
                // Cannot extract. There are still items in the way.
                Logging.warn(minecraft.player, "You cannot extract. Remove all dimlets first!");
                return;
            }
        }
        sendServerCommandTyped(RFToolsDimMessages.INSTANCE, EnscriberTileEntity.CMD_EXTRACT, TypedMap.EMPTY);
    }

    private void storeDimlets() {
        sendServerCommandTyped(RFToolsDimMessages.INSTANCE, EnscriberTileEntity.CMD_STORE,
                TypedMap.builder()
                        .put(PARAM_NAME, nameField.getText())
                        .build());
    }

    private void enableButtons() {
        Slot slot = container.inventorySlots.get(EnscriberTileEntity.SLOT_TAB);
        extractButton.enabled(false);
        storeButton.enabled(false);
        if (!slot.getStack().isEmpty()) {
            // @todo 1.16
//            if (slot.getStack().getItem() == ModItems.emptyDimensionTabItem) {
//                storeButton.setEnabled(true);
//            } else if (slot.getStack().getItem() == ModItems.realizedDimensionTabItem) {
//                extractButton.setEnabled(true);
//            }
        }
    }

    // @todo 1.16
//    private java.util.List<DimletKey> extractModifiersForType(java.util.List<DimletKey> modifiers, DimletType type) {
//        java.util.List<DimletKey> modifiersForType = new ArrayList<>();
//        int i = 0;
//        while (i < modifiers.size()) {
//            DimletKey modifier = modifiers.get(i);
//            if (type.dimletType.isModifiedBy(modifier.getType())) {
//                modifiersForType.add(modifier);
//                modifiers.remove(i);
//            } else {
//                i++;
//            }
//        }
//        return modifiersForType;
//    }

    private String shortenName(String name) {
        int idx = name.indexOf('_');
        if (idx == -1) {
            return name;
        } else {
            return name.substring(idx+1);
        }
    }

    private void validateDimlets() {
        // @todo 1.16
//        java.util.List<String> tooltips = new ArrayList<>();
//
//        TerrainType terrainType = null;
//        int cntTerrain = 0;
//        int cntBiomes = 0;
//        int cntController = 0;
//        int cntOwner = 0;
//        for (int i = DimensionEnscriberContainer.SLOT_DIMLETS ; i < DimensionEnscriberContainer.SLOT_TAB ; i++) {
//            Slot slot = inventorySlots.getSlot(i);
//            if (slot != null && !slot.getStack().isEmpty()) {
//                ItemStack stack = slot.getStack();
//                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
//                if (key.getType() == DimletType.DIMLET_TERRAIN) {
//                    cntTerrain++;
//                    terrainType = DimletObjectMapping.getTerrain(key);
//                } else if (key.getType() == DimletType.DIMLET_BIOME) {
//                    cntBiomes++;
//                } else if (key.getType() == DimletType.DIMLET_CONTROLLER) {
//                    cntController++;
//                } else if (key.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(key) == SpecialType.SPECIAL_OWNER) {
//                    cntOwner++;
//                }
//            }
//        }
//        if (cntOwner > 1) {
//            tooltips.add("Using more then one owner dimlet is not useful!");
//        }
//        if (GeneralConfiguration.ownerDimletsNeeded && cntOwner != 1) {
//            tooltips.add("You cannot make a dimension without an owner dimlet!");
//            storeButton.setEnabled(false);
//        }
//        if (cntTerrain > 1) {
//            tooltips.add("Using more then one TERRAIN is not useful!");
//            terrainType = null;
//        }
//        if (cntController > 1) {
//            tooltips.add("Using more then one CONTROLLER is not useful!");
//        }
//
//        java.util.List<DimletKey> modifiers = new ArrayList<>();
//        for (int i = DimensionEnscriberContainer.SLOT_DIMLETS ; i < DimensionEnscriberContainer.SLOT_TAB ; i++) {
//            Slot slot = inventorySlots.getSlot(i);
//            if (slot != null && !slot.getStack().isEmpty()) {
//                ItemStack stack = slot.getStack();
//                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
//                DimletType type = key.getType();
//                if (type.dimletType.isModifier()) {
//                    modifiers.add(key);
//                } else {
//                    List<DimletKey> modifiersForType = extractModifiersForType(modifiers, type);
//                    if (type == DimletType.DIMLET_TERRAIN) {
//                        if (DimletObjectMapping.getTerrain(key) == TerrainType.TERRAIN_VOID && !modifiersForType.isEmpty()) {
//                            tooltips.add("VOID terrain cannot use modifiers");
//                        }
//                    } else if (type == DimletType.DIMLET_FEATURE) {
//                        FeatureType featureType = DimletObjectMapping.getFeature(key);
//                        Counter<DimletType> modifierAmountUsed = new Counter<>();
//                        for (DimletKey modifier : modifiersForType) {
//                            modifierAmountUsed.increment(modifier.getType());
//                        }
//                        for (Map.Entry<DimletType, Integer> entry : modifierAmountUsed.entrySet()) {
//                            Integer amountSupported = featureType.getSupportedModifierAmount(entry.getKey());
//                            if (amountSupported == null) {
//                                tooltips.add(shortenName(featureType.name()) + " does not use " + shortenName(entry.getKey().name()) + " modifiers!");
//                            } else if (amountSupported == 1 && entry.getValue() > 1) {
//                                tooltips.add(shortenName(featureType.name()) + " only needs one " + shortenName(entry.getKey().name()) + " modifier!");
//                            }
//                        }
//
//                        if (terrainType == null && !featureType.supportsAllTerrains()) {
//                            tooltips.add(shortenName(featureType.name()) + " is possibly useless as it does not work on all terrains!");
//                        }
//                        if (terrainType != null && !featureType.isTerrainSupported(terrainType)) {
//                            tooltips.add(shortenName(featureType.name()) + " does not work for terrain " + shortenName(terrainType.name()) + "!");
//                        }
//                    } else if (type == DimletType.DIMLET_CONTROLLER) {
//                        ControllerType controllerType = DimletObjectMapping.getController(key);
//                        int neededBiomes = controllerType.getNeededBiomes();
//                        if (neededBiomes != -1) {
//                            if (cntBiomes > neededBiomes) {
//                                tooltips.add("Too many biomes specified for " + shortenName(controllerType.name()) + "!");
//                            } else if (cntBiomes < neededBiomes) {
//                                tooltips.add("Too few biomes specified for " + shortenName(controllerType.name()) + "!");
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (!modifiers.isEmpty()) {
//            tooltips.add("There are dangling modifiers in this descriptor");
//        }
//
//        boolean error = true;
//        if (tooltips.isEmpty()) {
//            tooltips.add("Everything appears to be alright");
//            error = false;
//        }
//        validateField.setTooltips(tooltips.toArray(new String[tooltips.size()]));
//        validateField.setColor(error ? 0xFF0000 : 0x008800);
//        validateField.setText(error ? "Warn" : "Ok");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        enableButtons();
        validateDimlets();

        if (tileEntity.hasTabSlotChangedAndClear()) {
            setNameFromDimensionTab();
        }

        drawWindow(matrixStack);
    }

    private void setNameFromDimensionTab() {
        String dimensionName = tileEntity.getDimensionName();
        if (dimensionName != null) {
            nameField.text(dimensionName);
        }
    }
}
