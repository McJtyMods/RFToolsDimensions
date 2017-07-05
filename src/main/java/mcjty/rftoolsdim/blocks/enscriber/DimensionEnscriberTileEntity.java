package mcjty.rftoolsdim.blocks.enscriber;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.dimensions.types.SpecialType;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DimensionEnscriberTileEntity extends GenericTileEntity implements DefaultSidedInventory {

    public static final String CMD_STORE = "store";
    public static final String CMD_EXTRACT = "extract";
    public static final String CMD_SETNAME = "setName";

    private boolean tabSlotHasChanged = false;

    private InventoryHelper inventoryHelper = new InventoryHelper(this, DimensionEnscriberContainer.factory, DimensionEnscriberContainer.SIZE_DIMLETS+1);

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return DimensionEnscriberContainer.factory.getAccessibleSlots();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return DimensionEnscriberContainer.factory.isInputSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return DimensionEnscriberContainer.factory.isOutputSlot(index);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
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
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
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
    }

    private void storeDimlets(EntityPlayerMP player) {
        if (GeneralConfiguration.ownerDimletsNeeded) {
            if (checkOwnerDimlet()) {
                Logging.warn(player, "You need an owner dimlet to make a dimension!");
                return;
            }
        }
        DimensionDescriptor descriptor = convertToDimensionDescriptor();
        ItemStack realizedTab = createRealizedTab(descriptor, getWorld());
        inventoryHelper.setStackInSlot(DimensionEnscriberContainer.SLOT_TAB, realizedTab);

        markDirty();
    }

    private boolean checkOwnerDimlet() {
        boolean owner = false;
        for (int i = 0 ; i < DimensionEnscriberContainer.SIZE_DIMLETS ; i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i + DimensionEnscriberContainer.SLOT_DIMLETS);
            if (!stack.isEmpty()) {
                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
                if (key.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(key) == SpecialType.SPECIAL_OWNER) {
                    owner = true;
                    break;
                }
            }
        }
        return !owner;
    }

    /**
     * Create a realized dimension tab by taking a map of ids per type and storing
     * that in the NBT of the realized dimension tab.
     */
    public static ItemStack createRealizedTab(DimensionDescriptor descriptor, World world) {
        ItemStack realizedTab = new ItemStack(ModItems.realizedDimensionTabItem, 1, 0);
        NBTTagCompound tagCompound = new NBTTagCompound();
        descriptor.writeToNBT(tagCompound);

        // Check if the dimension already exists and if so set the progress to 100%.
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(world);
        Integer id = manager.getDimensionID(descriptor);
        if (id != null) {
            // The dimension was already created.
            tagCompound.setInteger("ticksLeft", 0);
            tagCompound.setInteger("id", id);
        }

        realizedTab.setTagCompound(tagCompound);
        return realizedTab;
    }

    /**
     * Convert the dimlets in the inventory to a dimension descriptor.
     */
    private DimensionDescriptor convertToDimensionDescriptor() {
        List<DimletKey> descriptors = new ArrayList<>();

        long forcedSeed = 0;

        for (int i = 0 ; i < DimensionEnscriberContainer.SIZE_DIMLETS ; i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i + DimensionEnscriberContainer.SLOT_DIMLETS);
            if (!stack.isEmpty()) {
                DimletKey key = KnownDimletConfiguration.getDimletKey(stack);
                Settings settings = KnownDimletConfiguration.getSettings(key);
                if (settings != null) {
                    // Make sure the dimlet is not blacklisted.
                    descriptors.add(key);
                    NBTTagCompound tagCompound = stack.getTagCompound();
                    if (tagCompound != null && tagCompound.getLong("forcedSeed") != 0) {
                        forcedSeed = tagCompound.getLong("forcedSeed");
                    }
                }
            }
            inventoryHelper.setStackInSlot(i + DimensionEnscriberContainer.SLOT_DIMLETS, ItemStack.EMPTY);
        }
        return new DimensionDescriptor(descriptors, forcedSeed);
    }

    private void extractDimlets() {
        ItemStack realizedTab = inventoryHelper.getStackInSlot(DimensionEnscriberContainer.SLOT_TAB);
        NBTTagCompound tagCompound = realizedTab.getTagCompound();
        if (tagCompound != null) {
            int idx = DimensionEnscriberContainer.SLOT_DIMLETS;
            String descriptionString = tagCompound.getString("descriptionString");
            for (DimletKey descriptor : DimensionDescriptor.parseDescriptionString(descriptionString)) {
                int id = tagCompound.getInteger("id");
                if (GeneralConfiguration.ownerDimletsNeeded && id != 0) {
                    // If we need owner dimlets and the dimension is created we don't extract the owern dimlet.
                    if (descriptor.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(descriptor) == SpecialType.SPECIAL_OWNER) {
                        continue;
                    }
                }

                inventoryHelper.setStackInSlot(idx++, KnownDimletConfiguration.getDimletStack(descriptor));
            }
        }

        inventoryHelper.setStackInSlot(DimensionEnscriberContainer.SLOT_TAB, new ItemStack(ModItems.emptyDimensionTabItem));
        markDirty();
    }

    private void setName(String name) {
        ItemStack realizedTab = inventoryHelper.getStackInSlot(DimensionEnscriberContainer.SLOT_TAB);
        if (!realizedTab.isEmpty()) {
            NBTTagCompound tagCompound = realizedTab.getTagCompound();
            if (tagCompound == null) {
                tagCompound = new NBTTagCompound();
                realizedTab.setTagCompound(tagCompound);
            }
            tagCompound.setString("name", name);
            if (tagCompound.hasKey("id")) {
                Integer id = tagCompound.getInteger("id");
                RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(getWorld());
                DimensionInformation information = dimensionManager.getDimensionInformation(id);
                if (information != null) {
                    information.setName(name);
                    dimensionManager.save(getWorld());
                }
            }
            markDirty();
        }
    }

    @Override
    public void onSlotChanged(int index, ItemStack stack) {
        if (getWorld().isRemote && index == DimensionEnscriberContainer.SLOT_TAB) {
            tabSlotHasChanged = true;
        }
    }

    public boolean hasTabSlotChangedAndClear() {
        boolean rc = tabSlotHasChanged;
        tabSlotHasChanged = false;
        return rc;
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_STORE.equals(command)) {
            storeDimlets(playerMP);
            setName(args.get("name").getString());
            return true;
        } else if (CMD_EXTRACT.equals(command)) {
            extractDimlets();
            return true;
        } else if (CMD_SETNAME.equals(command)) {
            setName(args.get("name").getString());
            return true;
        }
        return false;
    }
}
