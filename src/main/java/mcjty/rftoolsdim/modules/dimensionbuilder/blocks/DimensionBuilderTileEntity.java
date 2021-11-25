package mcjty.rftoolsdim.modules.dimensionbuilder.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.power.PowerHandler;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class DimensionBuilderTileEntity extends GenericTileEntity implements ITickableTileEntity {

    @GuiValue
    private short errorMode = 0;

    public static final int SLOT_DIMENSION_TAB = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(DimensionBuilderTileEntity::isRealizedTab).in().out(),
                    SLOT_DIMENSION_TAB, 28, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, DimensionBuilderConfig.BUILDER_MAXENERGY.get(), DimensionBuilderConfig.BUILDER_RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimension Builder")
            .containerSupplier((windowId,player) -> new GenericContainer(DimensionBuilderModule.CONTAINER_DIMENSION_BUILDER, windowId, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .integerListener(Sync.integer(this::getBuildPercentage, v -> clientBuildPercentage = v))
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(DimensionBuilderTileEntity.this));

    // For usage in the gui
    private int clientBuildPercentage = 0;

    private int state = 0;          // For front state

    public static final short OK = 0;
    public static final short ERROR_NOOWNER = -1;
    public static final short ERROR_TOOMANYDIMENSIONS = -2;
    public static final short ERROR_COLLISION = -3;


    public DimensionBuilderTileEntity() {
        super(DimensionBuilderModule.TYPE_DIMENSION_BUILDER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(DimensionBuilderTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimensions/dimension_builder"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.ROTATION;
            }
        };
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        this.writeClientDataToNBT(nbtTag);
        nbtTag.putInt("errorMode", errorMode);
        return new SUpdateTileEntityPacket(worldPosition, 1, nbtTag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        updateTag.putInt("errorMode", errorMode);
        return updateTag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        int oldstate = state;
        int oldError = errorMode;
        super.onDataPacket(net, packet);
        errorMode = (short) packet.getTag().getInt("errorMode");
        if (oldstate != state || oldError != this.errorMode) {
            getLevel().setBlocksDirty(worldPosition, getBlockState(), getBlockState());
        }
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            CompoundNBT tagCompound = hasTab();
            if (tagCompound == null) {
                setState(-1);
                return;
            }

            if (!isMachineEnabled()) {
                setState(-1);
                return;
            }

            int ticksLeft = tagCompound.getInt("ticksLeft");
            if (ticksLeft > 0) {
                ticksLeft = createDimensionTick(tagCompound, ticksLeft);
            } else {
                maintainDimensionTick(tagCompound);
            }

            setState(ticksLeft);

        }
    }

    private void maintainDimensionTick(CompoundNBT tagCompound) {
        if (tagCompound.contains("dimension")) {
            String dimension = tagCompound.getString("dimension");
            ResourceLocation id = new ResourceLocation(dimension);
            DimensionData data = PersistantDimensionManager.get(level).getData(id);
            if (data == null) {
                return;
            }
            long rf;
            // @todo 1.16
//            if (isCheaterDimension(tagCompound)) {
//                rf = MachineConfiguration.BUILDER_MAXENERGY;
//            } else {
                rf = energyStorage.getEnergy();
//            }

            long energy = data.getEnergy();
            long maxEnergy = PowerHandler.calculateMaxDimensionPower(id, level) - energy;
            if (rf > maxEnergy) {
                rf = maxEnergy;
            }
//            if (!isCheaterDimension(tagCompound)) {
                energyStorage.consumeEnergy(rf);
//            }
            data.setEnergy(level, energy + rf);
            PersistantDimensionManager.get(level).save();
        }
    }

    private static final Random random = new Random();

    private int createDimensionTick(CompoundNBT tagCompound, int ticksLeft) {

        // @todo 1.16
//        if (GeneralConfiguration.dimensionBuilderNeedsOwner) {
//            if (getOwnerUUID() == null) {
//                // No valid owner so we don't build the dimension.
//                errorMode = ERROR_NOOWNER;
//                return ticksLeft;
//            }
//            if (GeneralConfiguration.maxDimensionsPerPlayer >= 0) {
//                int tickCost = tagCompound.getInt("tickCost");
//                if (ticksLeft == tickCost || ticksLeft < 5) {
//                    // Check if we are allow to make the dimension.
//                    RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(getWorld());
//                    int cnt = manager.countOwnedDimensions(getOwnerUUID());
//                    if (cnt >= GeneralConfiguration.maxDimensionsPerPlayer) {
//                        errorMode = ERROR_TOOMANYDIMENSIONS;
//                        return ticksLeft;
//                    }
//                }
//            }
//        }
        errorMode = OK;

        // If we are creating a dimension we should reserve the name
        String name = tagCompound.getString("name");
        DimensionManager.get().markReservedName(level, worldPosition, name);

        int createCost = tagCompound.getInt("rfCreateCost");
        Float inf = infusableHandler.map(IInfusable::getInfusedFactor).orElse(0.0f);
        createCost = (int) (createCost * (2.0f - inf) / 2.0f);

        if (isCheaterDimension(tagCompound) || (energyStorage.getEnergyStored() >= createCost)) {
            if (!DimensionManager.get().isNameAvailable(level, worldPosition, name)) {
                // The name is not available. Stop building!
                errorMode = ERROR_COLLISION;
                setChanged();
                return ticksLeft;
            }

            if (isCheaterDimension(tagCompound)) {
                ticksLeft = 0;
            } else {
                energyStorage.consumeEnergy(createCost);
                ticksLeft--;
                if (random.nextFloat() < inf) {
                    // Randomly reduce another tick if the device is infused.
                    ticksLeft--;
                    if (ticksLeft < 0) {
                        ticksLeft = 0;
                    }
                }
            }
            tagCompound.putInt("ticksLeft", ticksLeft);
            if (ticksLeft <= 0) {
                String descriptorString = tagCompound.getString("descriptor");
                DimensionDescriptor descriptor = new DimensionDescriptor();
                descriptor.read(descriptorString);

                DimensionDescriptor randomizedDescriptor = descriptor.createRandomizedDescriptor(random);

                if (!DimensionManager.get().isNameAvailable(level, worldPosition, name)) {
                    // Error!
                    errorMode = ERROR_COLLISION;
                    setChanged();
                    return 0;
                }
                if (!DimensionManager.get().isDescriptorAvailable(level, descriptor)) {
                    // Error!
                    errorMode = ERROR_COLLISION;
                    setChanged();
                    return 0;
                }

                long seed = random.nextLong();
                ServerWorld newworld = DimensionManager.get().createWorld(this.level, name, seed, descriptor, randomizedDescriptor);
                ResourceLocation id = new ResourceLocation(RFToolsDim.MODID, name);
                tagCompound.putString("dimension", id.toString());
                CompiledDescriptor compiledDescriptor = DimensionManager.get().getCompiledDescriptor(newworld);
                tagCompound.putInt("rfMaintainCost", compiledDescriptor.getActualPowerCost());
                setChanged();

                placeMatterReceiver(newworld, name);
            }
        }
        return ticksLeft;
    }

    private void placeMatterReceiver(ServerWorld newworld, String name) {
        int y = 250;
        while (y >= 1) {
            if (newworld.getBlockState(new BlockPos(8, y, 8)).getBlock() == Blocks.COMMAND_BLOCK) {
                RFToolsUtilityCompat.createTeleporter(newworld, new BlockPos(8, y, 8), name);
                return;
            }
            y--;
        }
        // It failed, the commandblock may have been overwritten. Luckily we recorded the height
        // of the platform somewhere
        int platformHeight = DimensionManager.get().getPlatformHeight(newworld.dimension().location());
        RFToolsUtilityCompat.createTeleporter(newworld, new BlockPos(8, platformHeight, 8), name);
        newworld.setBlockAndUpdate(new BlockPos(8, platformHeight+1, 8), Blocks.AIR.defaultBlockState());
        newworld.setBlockAndUpdate(new BlockPos(8, platformHeight+2, 8), Blocks.AIR.defaultBlockState());
    }

    private boolean isCheaterDimension(CompoundNBT tag) {
        // @todo 1.16
        return false;
    }

    public OperationType getState() {
        return OperationType.values()[state];
    }

    private void setState(int ticksLeft) {
        int oldstate = state;
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
            setChanged();
        }
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        readItemHandlerCap(tagCompound);
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        writeItemHandlerCap(tagCompound);
    }

    public CompoundNBT hasTab() {
        ItemStack itemStack = items.getStackInSlot(SLOT_DIMENSION_TAB);
        if (itemStack.isEmpty()) {
            return null;
        }

        return itemStack.getTag();
    }

    public int getBuildPercentage() {
        if (level.isClientSide) {
            return clientBuildPercentage;
        } else {
            CompoundNBT tag = hasTab();
            if (tag != null) {
                int ticksLeft = tag.getInt("ticksLeft");
                int tickCost = tag.getInt("tickCost");
                if (tickCost == 0) {
                    return 0;
                }
                return (tickCost - ticksLeft) * 100 / tickCost;
            } else {
                return 0;
            }
        }
    }

    public int getErrorMode() {
        return errorMode;
    }

    public static boolean isRealizedTab(ItemStack stack) {
        return stack.getItem() == DimensionBuilderModule.REALIZED_DIMENSION_TAB.get();
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isRealizedTab(stack);
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }

    public enum OperationType implements IStringSerializable {
        CHARGING("charging"),
        EMPTY("empty"),
        BUILDING1("building1"),
        BUILDING2("building2");

        private final String name;

        OperationType(String name) {
            this.name = name;
        }


        @Override
        @Nonnull
        public String getSerializedName() {
            return name;
        }
    }

}
