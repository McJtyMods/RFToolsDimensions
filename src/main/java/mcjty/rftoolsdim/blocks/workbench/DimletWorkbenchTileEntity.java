package mcjty.rftoolsdim.blocks.workbench;

import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.tileentity.GenericEnergyReceiverTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.MachineConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletCraftingTools;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.dimlets.types.IDimletType;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;

public class DimletWorkbenchTileEntity extends GenericEnergyReceiverTileEntity implements ITickable, DefaultSidedInventory {

    public static final String CMD_SUGGESTPARTS = "workbench.suggestParts";
    public static final String CMD_CHEATDIMLET = "workbench.cheatDimlet";
    public static final Key<String> PARAM_TYPE = new Key<>("type", Type.STRING);
    public static final Key<String> PARAM_ID = new Key<>("id", Type.STRING);


    public static final String CMD_GETEXTRACTING = "getExtracting";
    public static final String CLIENTCMD_GETEXTRACTING = "getExtracting";
    public static final Key<Integer> PARAM_PROGRESS = new Key<>("progress", Type.INTEGER);

    private InventoryHelper inventoryHelper = new InventoryHelper(this, DimletWorkbenchContainer.factory, DimletWorkbenchContainer.SIZE_BUFFER + 9);

    private int extracting = 0;
    private DimletKey idToExtract = null;
    private int inhibitCrafting = 0;
    private boolean extractMode = false;

    public static final Key<Boolean> VALUE_EXTRACT = new Key<>("extract", Type.BOOLEAN);

    @Override
    public IValue[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_EXTRACT, DimletWorkbenchTileEntity::isExtractMode, DimletWorkbenchTileEntity::setExtractMode)
        };
    }

    public int getExtracting() {
        return extracting;
    }

    public DimletWorkbenchTileEntity() {
        super(MachineConfiguration.WORKBENCH_MAXENERGY, MachineConfiguration.WORKBENCH_RECEIVEPERTICK);
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] { DimletWorkbenchContainer.SLOT_INPUT, DimletWorkbenchContainer.SLOT_OUTPUT };
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == DimletWorkbenchContainer.SLOT_OUTPUT;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == DimletWorkbenchContainer.SLOT_INPUT;
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    private void checkCrafting() {
        if (inhibitCrafting == 0) {
            if (!checkDimletCrafting()) {
                if (!inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_OUTPUT).isEmpty()) {
                    inventoryHelper.setInventorySlotContents(0, DimletWorkbenchContainer.SLOT_OUTPUT, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        if (extracting > 0) {
            extracting--;
            if (extracting == 0) {
                if (!doExtract()) {
                    // We failed due to not enough power. Try again later.
                    extracting = 10;
                }
            }
            markDirty();
        } else if (extractMode) {
            startExtracting();
        }
    }

    private boolean checkDimletCrafting() {
        ItemStack stackBase = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_BASE);
        if (stackBase.isEmpty()) {
            return false;
        }
        ItemStack stackController = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_CONTROLLER);
        if (stackController.isEmpty()) {
            return false;
        }
        ItemStack stackTypeController = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_TYPE_CONTROLLER);
        if (stackTypeController.isEmpty()) {
            return false;
        }
        ItemStack stackMemory = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_MEMORY);
        if (stackMemory.isEmpty()) {
            return false;
        }
        ItemStack stackEnergy = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_ENERGY);
        if (stackEnergy.isEmpty()) {
            return false;
        }
        ItemStack stackEssence = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_ESSENCE);
        if (stackEssence.isEmpty()) {
            return false;
        }

        DimletType type = DimletType.values()[stackTypeController.getItemDamage()];
        IDimletType itype = type.dimletType;
        DimletKey key = itype.attemptDimletCrafting(stackController, stackMemory, stackEnergy, stackEssence);
        if (key != null) {
            inventoryHelper.setInventorySlotContents(1, DimletWorkbenchContainer.SLOT_OUTPUT, KnownDimletConfiguration.getDimletStack(key));
            return true;
        }
        return false;
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        ItemStack s = inventoryHelper.decrStackSize(index, amount);
        checkCrafting();
        return s;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventoryHelper.setInventorySlotContents(getInventoryStackLimit(), index, stack);
        if (index < DimletWorkbenchContainer.SLOT_BASE || index > DimletWorkbenchContainer.SLOT_ESSENCE) {
            return;
        }

        checkCrafting();
    }

    public void craftDimlet() {
        inhibitCrafting++;
        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_BASE, 1);
        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_CONTROLLER, 1);
        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_TYPE_CONTROLLER, 1);
        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_ENERGY, 1);
        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_MEMORY, 1);
        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_ESSENCE, 1);
        inhibitCrafting--;
        checkCrafting();
    }

    public boolean isExtractMode() {
        return extractMode;
    }

    public void setExtractMode(boolean extractMode) {
        this.extractMode = extractMode;
        markDirtyClient();
    }

    private void startExtracting() {
        if (extracting > 0) {
            // Already extracting
            return;
        }
        ItemStack stack = inventoryHelper.getStackInSlot(DimletWorkbenchContainer.SLOT_INPUT);
        if (!stack.isEmpty()) {
            if (ModItems.knownDimletItem.equals(stack.getItem())) {
                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
                Settings settings = KnownDimletConfiguration.getSettings(key);
                if (settings != null && settings.isDimlet()) {
                    if (!KnownDimletConfiguration.isCraftable(key)) {
                        extracting = 64;
                        idToExtract = key;
                        inventoryHelper.decrStackSize(DimletWorkbenchContainer.SLOT_INPUT, 1);
                        markDirty();
                    }
                }
            }
        }
    }

    private boolean doExtract() {
        int rf = MachineConfiguration.workbenchRfPerDimlet;
        if (getEnergyStored() < rf) {
            // Not enough energy.
            return false;
        }
        consumeEnergy(rf);

        float factor = getInfusedFactor();

        Settings entry = KnownDimletConfiguration.getSettings(idToExtract);

        if (extractSuccess(factor)) {
            mergeItemOrThrowInWorld(new ItemStack(ModItems.dimletBaseItem));
        }

        int rarity = entry.getRarity();

        if (extractSuccess(factor)) {
            mergeItemOrThrowInWorld(new ItemStack(ModItems.dimletTypeControllerItem, 1, idToExtract.getType().ordinal()));
        }

        int level = DimletCraftingTools.calculateItemLevelFromRarity(rarity);
        if (extractSuccess(factor)) {
            mergeItemOrThrowInWorld(new ItemStack(ModItems.dimletMemoryUnitItem, 1, level));
        } else {
            factor += 0.1f;     // If this failed we increase our chances a bit
        }

        if (extractSuccess(factor)) {
            mergeItemOrThrowInWorld(new ItemStack(ModItems.dimletEnergyModuleItem, 1, level));
        } else {
            factor += 0.1f;     // If this failed we increase our chances a bit
        }

        if (extractSuccess(factor)) {
            mergeItemOrThrowInWorld(new ItemStack(ModItems.dimletControlCircuitItem, 1, rarity));
        }

        idToExtract = null;
        markDirty();

        return true;
    }

    private boolean extractSuccess(float factor) {
        return getWorld().rand.nextFloat() <= (0.61f + factor * 0.4f);
    }

    private void cheatDimlet(EntityPlayerMP player, DimletKey key) {
        ItemStack dimlet = KnownDimletConfiguration.getDimletStack(key);
        if (!player.inventory.addItemStackToInventory(dimlet)) {
            player.entityDropItem(dimlet, 1.05f);
        }
    }

    private void suggestParts(EntityPlayerMP playerMP, DimletKey key) {
        // First try to remove all items currently in the slots
        setAsideIfPossible(playerMP, DimletWorkbenchContainer.SLOT_BASE);
        setAsideIfPossible(playerMP, DimletWorkbenchContainer.SLOT_CONTROLLER);
        setAsideIfPossible(playerMP, DimletWorkbenchContainer.SLOT_ENERGY);
        setAsideIfPossible(playerMP, DimletWorkbenchContainer.SLOT_MEMORY);
        setAsideIfPossible(playerMP, DimletWorkbenchContainer.SLOT_TYPE_CONTROLLER);
        setAsideIfPossible(playerMP, DimletWorkbenchContainer.SLOT_ESSENCE);
        // Place the correct ones back;
        Settings entry = KnownDimletConfiguration.getSettings(key);
        int rarity = entry.getRarity();
        int level = DimletCraftingTools.calculateItemLevelFromRarity(rarity);
        tryPlaceIfPossible(playerMP, DimletWorkbenchContainer.SLOT_BASE, new ItemStack(ModItems.dimletBaseItem, 1));
        tryPlaceIfPossible(playerMP, DimletWorkbenchContainer.SLOT_TYPE_CONTROLLER, new ItemStack(ModItems.dimletTypeControllerItem, 1, key.getType().ordinal()));
        tryPlaceIfPossible(playerMP, DimletWorkbenchContainer.SLOT_MEMORY, new ItemStack(ModItems.dimletMemoryUnitItem, 1, level));
        tryPlaceIfPossible(playerMP, DimletWorkbenchContainer.SLOT_ENERGY, new ItemStack(ModItems.dimletEnergyModuleItem, 1, level));
        tryPlaceIfPossible(playerMP, DimletWorkbenchContainer.SLOT_CONTROLLER, new ItemStack(ModItems.dimletControlCircuitItem, 1, rarity));
        tryPlaceEssenceIfPossible(playerMP, DimletWorkbenchContainer.SLOT_ESSENCE, key, key.getType().dimletType);
        markDirtyClient();
        checkCrafting();
    }

    private void setAsideIfPossible(EntityPlayerMP playerMP, int slot) {
        ItemStack stack = inventoryHelper.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            int result = InventoryHelper.mergeItemStack(this, false, stack, DimletWorkbenchContainer.SLOT_BUFFER, DimletWorkbenchContainer.SLOT_BUFFER + DimletWorkbenchContainer.SIZE_BUFFER, null);
            if (result > 0) {
                if (result <= 0) {
                    stack.setCount(0);
                } else {
                    stack.setCount(result);
                }
                if (playerMP.inventory.addItemStackToInventory(stack)) {
                    inventoryHelper.setInventorySlotContents(64, slot, ItemStack.EMPTY);
                }
            } else {
                inventoryHelper.setInventorySlotContents(64, slot, ItemStack.EMPTY);
            }
        }
    }

    private void tryPlaceIfPossible(EntityPlayerMP playerMP, int slot, ItemStack part) {
        if (inventoryHelper.containsItem(slot)) {
            return;
        }
        for (int i = DimletWorkbenchContainer.SLOT_BUFFER ; i < DimletWorkbenchContainer.SLOT_BUFFER + DimletWorkbenchContainer.SIZE_BUFFER ; i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i);
            if (!stack.isEmpty() && stack.isItemEqual(part)) {
                ItemStack partStack = inventoryHelper.decrStackSize(i, 1);
                if (!partStack.isEmpty()) {
                    inventoryHelper.setInventorySlotContents(64, slot, partStack);
                    return;
                }
            }
        }
        for (int i = 0 ; i < playerMP.inventory.getSizeInventory() ; i++) {
            ItemStack stack = playerMP.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.isItemEqual(part)) {
                ItemStack partStack = playerMP.inventory.decrStackSize(i, 1);
                if (!partStack.isEmpty()) {
                    inventoryHelper.setInventorySlotContents(64, slot, partStack);
                    playerMP.openContainer.detectAndSendChanges();
                    return;
                }
            }
        }
    }

    private void tryPlaceEssenceIfPossible(EntityPlayerMP playerMP, int slot, DimletKey key, IDimletType type) {
        if (inventoryHelper.containsItem(slot)) {
            return;
        }
        for (int i = DimletWorkbenchContainer.SLOT_BUFFER ; i < DimletWorkbenchContainer.SLOT_BUFFER + DimletWorkbenchContainer.SIZE_BUFFER ; i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i);
            if (!stack.isEmpty() && key.equals(type.isValidEssence(stack))) {
                ItemStack partStack = inventoryHelper.decrStackSize(i, 1);
                if (!partStack.isEmpty()) {
                    inventoryHelper.setInventorySlotContents(64, slot, partStack);
                    return;
                }
            }
        }
        for (int i = 0 ; i < playerMP.inventory.getSizeInventory() ; i++) {
            ItemStack stack = playerMP.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && key.equals(type.isValidEssence(stack))) {
                ItemStack partStack = playerMP.inventory.decrStackSize(i, 1);
                if (!partStack.isEmpty()) {
                    inventoryHelper.setInventorySlotContents(64, slot, partStack);
                    playerMP.openContainer.detectAndSendChanges();
                    return;
                }
            }
        }
    }

    private void mergeItemOrThrowInWorld(ItemStack stack) {
        int notInserted = InventoryHelper.mergeItemStack(this, false, stack, DimletWorkbenchContainer.SLOT_BUFFER, DimletWorkbenchContainer.SLOT_BUFFER + DimletWorkbenchContainer.SIZE_BUFFER, null);
        if (notInserted > 0) {
            BlockTools.spawnItemStack(getWorld(), getPos().getX(), getPos().getY(), getPos().getZ(), stack);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
        extracting = tagCompound.getInteger("extracting");
        extractMode = tagCompound.getBoolean("extractMode");
        idToExtract = null;
        if (tagCompound.hasKey("extKtype")) {
            DimletType type = DimletType.getTypeByOpcode(tagCompound.getString("extKtype"));
            idToExtract = new DimletKey(type, tagCompound.getString("extDkey"));
        } else {
            idToExtract = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
        tagCompound.setInteger("extracting", extracting);
        tagCompound.setBoolean("extractMode", extractMode);
        if (idToExtract != null) {
            tagCompound.setString("extKtype", idToExtract.getType().dimletType.getOpcode());
            tagCompound.setString("extDkey", idToExtract.getId());
        }
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SUGGESTPARTS.equals(command)) {
            String type = params.get(PARAM_TYPE);
            String id = params.get(PARAM_ID);
            suggestParts(playerMP, new DimletKey(DimletType.getTypeByName(type), id));
            return true;
        } else if (CMD_CHEATDIMLET.equals(command)) {
            String type = params.get(PARAM_TYPE);
            String id = params.get(PARAM_ID);
            cheatDimlet(playerMP, new DimletKey(DimletType.getTypeByName(type), id));
            return true;
        }
        return false;
    }

    // Request the extracting amount from the server. This has to be called on the client side.
    public void requestExtractingFromServer() {
        requestDataFromServer(RFToolsDim.MODID,
                CMD_GETEXTRACTING,
                CLIENTCMD_GETEXTRACTING, TypedMap.EMPTY);
    }

    @Override
    public TypedMap executeWithResult(String command, TypedMap args) {
        TypedMap rc = super.executeWithResult(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETEXTRACTING.equals(command)) {
            return TypedMap.builder().put(PARAM_PROGRESS, extracting).build();
        }
        return null;
    }

    @Override
    public boolean receiveDataFromServer(String command, @Nonnull TypedMap result) {
        boolean rc = super.receiveDataFromServer(command, result);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETEXTRACTING.equals(command)) {
            extracting = result.get(PARAM_PROGRESS);
            return true;
        }
        return false;
    }
}
