package mcjty.rftoolsdim.blocks.builder;

import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.tileentity.GenericEnergyReceiverTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftools.blocks.builder.BuilderTileEntity;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.MachineConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;
import java.util.Random;

public class DimensionBuilderTileEntity extends GenericEnergyReceiverTileEntity implements ITickable, DefaultSidedInventory {

    public static final String CMD_GETBUILDING = "getBuilding";
    public static final Key<Integer> PARAM_BUILDING_PROGRESS = new Key<>("buildingprogress", Type.INTEGER);

    public static final String COMPONENT_NAME = "dimension_builder";

    // For usage in the gui
    private static int buildPercentage = 0;

    private int creative = -1;      // -1 is unknown
    private int state = 0;          // For front state

    public static int OK = 0;
    public static int ERROR_NOOWNER = -1;
    public static int ERROR_TOOMANYDIMENSIONS = -2;
    private int errorMode = 0;

    private InventoryHelper inventoryHelper = new InventoryHelper(this, DimensionBuilderContainer.factory, 1);

    public DimensionBuilderTileEntity() {
        super(MachineConfiguration.BUILDER_MAXENERGY, MachineConfiguration.BUILDER_RECEIVEPERTICK);
    }

    @Override
    public IValue[] getValues() {
        return new IValue[]{
                new DefaultValue<>(VALUE_RSMODE, BuilderTileEntity::getRSModeInt, BuilderTileEntity::setRSModeInt),
        };
    }



    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        int oldstate = state;
        super.onDataPacket(net, packet);
        if (oldstate != state) {
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
        }
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    private boolean isCreative() {
//        if (creative == -1) {
//            Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
//            if (DimletSetup.creativeDimensionBuilderBlock.equals(block)) {
//                creative = 1;
//            } else {
//                creative = 0;
//            }
//        }
//        return creative == 1;
        // @todo
        return false;
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        NBTTagCompound tagCompound = hasTab();
        if (tagCompound == null) {
            setState(-1);
            return;
        }

        if (!isMachineEnabled()) {
            setState(-1);
            return;
        }

        int ticksLeft = tagCompound.getInteger("ticksLeft");
        if (ticksLeft > 0) {
            ticksLeft = createDimensionTick(tagCompound, ticksLeft);
        } else {
            maintainDimensionTick(tagCompound);
        }

        setState(ticksLeft);
    }

    public NBTTagCompound hasTab() {
        ItemStack itemStack = inventoryHelper.getStackInSlot(0);
        if (itemStack.isEmpty()) {
            return null;
        }

        return itemStack.getTagCompound();
    }

    private static int counter = 20;

    private void maintainDimensionTick(NBTTagCompound tagCompound) {
        int id = tagCompound.getInteger("id");

        if (id != 0) {
            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(getWorld());
            long rf;
            if (isCheaterDimension(tagCompound)) {
                rf = MachineConfiguration.BUILDER_MAXENERGY;
            } else {
                rf = getEnergyStored();
            }
            long energy = dimensionStorage.getEnergyLevel(id);
            long maxEnergy = PowerConfiguration.MAX_DIMENSION_POWER - energy;      // Max energy the dimension can still get.
            if (rf > maxEnergy) {
                rf = maxEnergy;
            }
            if (Logging.debugMode) {
	            counter--;
	            if (counter < 0) {
	                counter = 20;
                    Logging.log("#################### id:" + id + ", rf:" + rf + ", energy:" + energy + ", max:" + maxEnergy);
                }
            }
            if (!isCheaterDimension(tagCompound)) {
                consumeEnergy(rf);
            }
            dimensionStorage.setEnergyLevel(id, energy + rf);
            dimensionStorage.save();
        }
    }

    private static Random random = new Random();

    private int createDimensionTick(NBTTagCompound tagCompound, int ticksLeft) {
        if (GeneralConfiguration.dimensionBuilderNeedsOwner) {
            if (getOwnerUUID() == null) {
                // No valid owner so we don't build the dimension.
                errorMode = ERROR_NOOWNER;
                return ticksLeft;
            }
            if (GeneralConfiguration.maxDimensionsPerPlayer >= 0) {
                int tickCost = tagCompound.getInteger("tickCost");
                if (ticksLeft == tickCost || ticksLeft < 5) {
                    // Check if we are allow to make the dimension.
                    RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(getWorld());
                    int cnt = manager.countOwnedDimensions(getOwnerUUID());
                    if (cnt >= GeneralConfiguration.maxDimensionsPerPlayer) {
                        errorMode = ERROR_TOOMANYDIMENSIONS;
                        return ticksLeft;
                    }
                }
            }
        }
        errorMode = OK;


        int createCost = tagCompound.getInteger("rfCreateCost");
        createCost = (int) (createCost * (2.0f - getInfusedFactor()) / 2.0f);

        if (isCheaterDimension(tagCompound) || (getEnergyStored() >= createCost)) {
            if (isCheaterDimension(tagCompound)) {
                ticksLeft = 0;
            } else {
                consumeEnergy(createCost);
                ticksLeft--;
                if (random.nextFloat() < getInfusedFactor()) {
                    // Randomly reduce another tick if the device is infused.
                    ticksLeft--;
                    if (ticksLeft < 0) {
                        ticksLeft = 0;
                    }
                }
            }
            tagCompound.setInteger("ticksLeft", ticksLeft);
            if (ticksLeft <= 0) {
                RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(getWorld());
                DimensionDescriptor descriptor = new DimensionDescriptor(tagCompound);
                String name = tagCompound.getString("name");
                int id = manager.createNewDimension(getWorld(), descriptor, name, getOwnerName(), getOwnerUUID());
                tagCompound.setInteger("id", id);
            }
        }
        return ticksLeft;
    }

    private boolean isCheaterDimension(NBTTagCompound tagCompound) {
        if (isCreative()) {
            return true;
        }
        String descriptionString = tagCompound.getString("descriptionString");
        return descriptionString.contains("XCheater");
    }

    public DimensionBuilderBlock.OperationType getState() {
        return DimensionBuilderBlock.OperationType.values()[state];
    }

    private void setState(int ticksLeft) {
        int oldstate = state;
        state = 0;
        if (ticksLeft == 0) {
            state = 0;
        } else if (ticksLeft == -1) {
            state = 1;
        } else if (((ticksLeft >> 2) & 1) == 0) {
            state = 2;
        } else {
            state = 3;
        }
        if (oldstate != state) {
            markDirtyClient();
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return DimensionBuilderContainer.factory.getAccessibleSlots();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return DimensionBuilderContainer.factory.isInputSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return DimensionBuilderContainer.factory.isOutputSlot(index);
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

    // Request the building percentage from the server. This has to be called on the client side.
    public void requestBuildingPercentage() {
        requestDataFromServer(RFToolsDim.MODID, CMD_GETBUILDING, TypedMap.EMPTY);
    }

    @Override
    public TypedMap executeWithResult(String command, TypedMap args) {
        TypedMap rc = super.executeWithResult(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETBUILDING.equals(command)) {
            ItemStack itemStack = inventoryHelper.getStackInSlot(0);
            if (itemStack.isEmpty()) {
                return TypedMap.builder().put(PARAM_BUILDING_PROGRESS, 0).build();
            } else {
                NBTTagCompound tagCompound = itemStack.getTagCompound();
                if (tagCompound == null) {
                    return TypedMap.builder().put(PARAM_BUILDING_PROGRESS, 0).build();
                }
                if (errorMode != OK) {
                    return TypedMap.builder().put(PARAM_BUILDING_PROGRESS, errorMode).build();
                } else {
                    int ticksLeft = tagCompound.getInteger("ticksLeft");
                    int tickCost = tagCompound.getInteger("tickCost");
                    return TypedMap.builder().put(PARAM_BUILDING_PROGRESS, (tickCost - ticksLeft) * 100 / tickCost).build();
                }
            }
        }
        return null;
    }

    @Override
    public boolean receiveDataFromServer(String command, @Nonnull TypedMap result) {
        boolean rc = super.receiveDataFromServer(command, result);
        if (rc) {
            return true;
        }
        if (CMD_GETBUILDING.equals(command)) {
            buildPercentage = result.get(PARAM_BUILDING_PROGRESS);
            return true;
        }
        return false;
    }

    public static int getBuildPercentage() {
        return buildPercentage;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        state = tagCompound.getByte("state");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setByte("state", (byte) state);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
    }
}
