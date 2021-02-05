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
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.enscriber.blocks.EnscriberTileEntity;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static mcjty.lib.gui.widgets.Widgets.*;
import static mcjty.rftoolsdim.modules.enscriber.blocks.EnscriberTileEntity.PARAM_NAME;

public class GuiEnscriber extends GenericGuiContainer<EnscriberTileEntity, GenericContainer> {
    public static final int ENSCRIBER_WIDTH = 256;
    public static final int ENSCRIBER_HEIGHT = 224;
    public static final String REGEX = "[a-z0-9_\\.\\-]+";

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
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            Minecraft.getInstance().player.sendStatusMessage(new StringTextComponent("Name is required!"), false);
            return;
        }
        sendServerCommandTyped(RFToolsDimMessages.INSTANCE, EnscriberTileEntity.CMD_STORE,
                TypedMap.builder()
                        .put(PARAM_NAME, name)
                        .build());
    }

    private void enableButtons() {
        Slot slot = container.inventorySlots.get(EnscriberTileEntity.SLOT_TAB);
        extractButton.enabled(false);
        storeButton.enabled(false);
        if (!slot.getStack().isEmpty()) {
            if (slot.getStack().getItem() == DimensionBuilderModule.EMPTY_DIMENSION_TAB.get()) {
                storeButton.enabled(true);
            } else if (slot.getStack().getItem() == DimensionBuilderModule.REALIZED_DIMENSION_TAB.get()) {
                extractButton.enabled(true);
            }
        }
    }

    private void validateDimlets() {
        int errorCode = tileEntity.getClientErrorCode();
        DescriptorError.Code error = DescriptorError.Code.values()[errorCode];

        List<String> tooltips = new ArrayList<>();
        if (error == DescriptorError.Code.OK) {
            tooltips.add("Everything appears to be alright");
            validateField.color(0x008800);
            validateField.text("Ok");
        } else {
            tooltips.add(error.getMessage());
            validateField.color(0xFF0000);
            validateField.text("Error");
            storeButton.enabled(false);
        }
        validateField.tooltips(tooltips.toArray(new String[tooltips.size()]));
    }

    private boolean validateName(String name) {
        if (name.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(REGEX, name);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        enableButtons();
        validateDimlets();

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            storeButton.enabled(false);
            storeButton.tooltips("A dimension name is needed!");
        } else if (!validateName(name)) {
            storeButton.enabled(false);
            storeButton.tooltips("The dimension name is invalid (only lowercase, no special characters)!");
        } else {
            storeButton.tooltips("Store dimlets in a", "empty dimension tab");
        }

        setNameFromDimensionTab();

        drawWindow(matrixStack);
    }

    private void setNameFromDimensionTab() {
        String dimensionName = tileEntity.getDimensionName();
        if (dimensionName != null) {
            nameField.text(dimensionName);
        }
    }
}
