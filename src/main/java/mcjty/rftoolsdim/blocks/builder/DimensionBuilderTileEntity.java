package mcjty.rftoolsdim.blocks.builder;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.network.PacketRequestIntegerFromServer;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.MachineConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.network.RFToolsDimMessages;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import java.util.Map;
import java.util.Random;

public class DimensionBuilderTileEntity extends GenericEnergyReceiverTileEntity implements ITickable, DefaultSidedInventory {

    public static final String CMD_GETBUILDING = "getBuilding";
    public static final String CLIENTCMD_GETBUILDING = "getBuilding";
    public static final String CMD_RSMODE = "rsMode";

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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        int oldstate = state;
        super.onDataPacket(net, packet);
        if (oldstate != state) {
            worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
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
        if (!worldObj.isRemote) {
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
        if (itemStack == null || itemStack.stackSize == 0) {
            return null;
        }

        return itemStack.getTagCompound();
    }

    private static int counter = 20;

    private void maintainDimensionTick(NBTTagCompound tagCompound) {
        int id = tagCompound.getInteger("id");

        if (id != 0) {
            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(worldObj);
            int rf;
            if (isCheaterDimension(tagCompound)) {
                rf = MachineConfiguration.BUILDER_MAXENERGY;
            } else {
                rf = getEnergyStored(EnumFacing.DOWN);
            }
            int energy = dimensionStorage.getEnergyLevel(id);
            int maxEnergy = PowerConfiguration.MAX_DIMENSION_POWER - energy;      // Max energy the dimension can still get.
            if (rf > maxEnergy) {
                rf = maxEnergy;
            }
            counter--;
            if (counter < 0) {
                counter = 20;
                if (Logging.debugMode) {
                    Logging.log("#################### id:" + id + ", rf:" + rf + ", energy:" + energy + ", max:" + maxEnergy);
                }
            }
            if (!isCheaterDimension(tagCompound)) {
                consumeEnergy(rf);
            }
            dimensionStorage.setEnergyLevel(id, energy + rf);
            dimensionStorage.save(worldObj);
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
                    RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(worldObj);
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

        if (isCheaterDimension(tagCompound) || (getEnergyStored(EnumFacing.DOWN) >= createCost)) {
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
                RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(worldObj);
                DimensionDescriptor descriptor = new DimensionDescriptor(tagCompound);
                String name = tagCompound.getString("name");
                int id = manager.createNewDimension(worldObj, descriptor, name, getOwnerName(), getOwnerUUID());
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    // Request the building percentage from the server. This has to be called on the client side.
    public void requestBuildingPercentage() {
        RFToolsDimMessages.INSTANCE.sendToServer(new PacketRequestIntegerFromServer(RFToolsDim.MODID, getPos(),
                CMD_GETBUILDING,
                CLIENTCMD_GETBUILDING));
    }

    @Override
    public Integer executeWithResultInteger(String command, Map<String, Argument> args) {
        Integer rc = super.executeWithResultInteger(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETBUILDING.equals(command)) {
            ItemStack itemStack = inventoryHelper.getStackInSlot(0);
            if (itemStack == null || itemStack.stackSize == 0) {
                return 0;
            } else {
                NBTTagCompound tagCompound = itemStack.getTagCompound();
                if (tagCompound == null) {
                    return 0;
                }
                if (errorMode != OK) {
                    return errorMode;
                } else {
                    int ticksLeft = tagCompound.getInteger("ticksLeft");
                    int tickCost = tagCompound.getInteger("tickCost");
                    return (tickCost - ticksLeft) * 100 / tickCost;
                }
            }
        }
        return null;
    }

    @Override
    public boolean execute(String command, Integer result) {
        boolean rc = super.execute(command, result);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETBUILDING.equals(command)) {
            buildPercentage = result;
            return true;
        }
        return false;
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_RSMODE.equals(command)) {
            String m = args.get("rs").getString();
            setRSMode(RedstoneMode.getMode(m));
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
