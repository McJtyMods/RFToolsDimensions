package mcjty.rftoolsdim.modules.dimensionbuilder.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Broadcaster;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class DimensionBuilderTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT_DIMENSION_TAB = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(DimensionBuilderTileEntity::isRealizedTab).in().out(),
                    CONTAINER_CONTAINER, SLOT_DIMENSION_TAB, 28, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, DimensionBuilderConfig.BUILDER_MAXENERGY.get(), DimensionBuilderConfig.BUILDER_RECEIVEPERTICK.get());
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimension Builder")
            .containerSupplier((windowId,player) -> new GenericContainer(DimensionBuilderModule.CONTAINER_DIMENSION_BUILDER.get(), windowId, CONTAINER_FACTORY.get(), getPos(), DimensionBuilderTileEntity.this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .shortListener(new IntReferenceHolder() {
                @Override
                public int get() {
                    return errorMode;
                }

                @Override
                public void set(int value) {
                    clientErrorMode = value;
                }
            })
            .integerListener(new IntReferenceHolder() {
                @Override
                public int get() {
                    return getBuildPercentage();
                }

                @Override
                public void set(int value) {
                    clientBuildPercentage = value;
                }
            }));
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(DimensionBuilderTileEntity.this));

    // For usage in the gui
    private int clientBuildPercentage = 0;

    private int creative = -1;      // -1 is unknown
    private int state = 0;          // For front state

    public static int OK = 0;
    public static int ERROR_NOOWNER = -1;
    public static int ERROR_TOOMANYDIMENSIONS = -2;
    private int errorMode = 0;
    private int clientErrorMode = 0;


    public DimensionBuilderTileEntity() {
        super(DimensionBuilderModule.TYPE_DIMENSION_BUILDER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(DimensionBuilderTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimensionbuilder"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.ROTATION;
            }
        };
    }

    @Override
    public IValue<?>[] getValues() {
        return new IValue[]{
                new DefaultValue<>(VALUE_RSMODE, this::getRSModeInt, this::setRSModeInt),
        };
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        int oldstate = state;
        super.onDataPacket(net, packet);
        if (oldstate != state) {
            getWorld().markBlockRangeForRenderUpdate(pos, getBlockState(), getBlockState());
        }
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
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

    private static int counter = 20;

    private void maintainDimensionTick(CompoundNBT tagCompound) {
        if (tagCompound.contains("dimension")) {
            String dimension = tagCompound.getString("dimension");
            DimensionData data = PersistantDimensionManager.get(world).getData(new ResourceLocation(dimension));
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
            long maxEnergy = DimensionConfig.MAX_DIMENSION_POWER.get() - energy;      // Max energy the dimension can still get.
            if (rf > maxEnergy) {
                rf = maxEnergy;
            }
//            if (Logging.debugMode) {
//                counter--;
//                if (counter < 0) {
//                    counter = 20;
//                    Logging.log("#################### id:" + id + ", rf:" + rf + ", energy:" + energy + ", max:" + maxEnergy);
//                }
//            }
//            if (!isCheaterDimension(tagCompound)) {
                energyStorage.consumeEnergy(rf);
//            }
            data.setEnergy(world, energy + rf);
            PersistantDimensionManager.get(world).save();
        }
    }

    private static Random random = new Random();

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


        int createCost = tagCompound.getInt("rfCreateCost");
        Float inf = infusableHandler.map(IInfusable::getInfusedFactor).orElse(0.0f);
        createCost = (int) (createCost * (2.0f - inf) / 2.0f);

        if (isCheaterDimension(tagCompound) || (energyStorage.getEnergyStored() >= createCost)) {
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
                String name = tagCompound.getString("name");
                String descriptorString = tagCompound.getString("descriptor");
                DimensionDescriptor descriptor = new DimensionDescriptor();
                descriptor.read(descriptorString);

                DimensionDescriptor randomizedDescriptor = descriptor.createRandomizedDescriptor(random);

                long seed = random.nextLong();
                ServerWorld newworld = DimensionManager.get().createWorld(this.world, name, seed, descriptor, randomizedDescriptor);
                ResourceLocation id = new ResourceLocation(RFToolsDim.MODID, name);
                tagCompound.putString("dimension", id.toString());
                CompiledDescriptor compiledDescriptor = DimensionManager.get().getCompiledDescriptor(newworld);
                tagCompound.putInt("rfMaintainCost", compiledDescriptor.getActualPowerCost());
                markDirty();

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
        int platformHeight = DimensionManager.get().getPlatformHeight(newworld.getDimensionKey().getLocation());
        RFToolsUtilityCompat.createTeleporter(newworld, new BlockPos(8, platformHeight, 8), name);
        newworld.setBlockState(new BlockPos(8, platformHeight+1, 8), Blocks.AIR.getDefaultState());
        newworld.setBlockState(new BlockPos(8, platformHeight+2, 8), Blocks.AIR.getDefaultState());
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


    public CompoundNBT hasTab() {
        ItemStack itemStack = items.getStackInSlot(SLOT_DIMENSION_TAB);
        if (itemStack.isEmpty()) {
            return null;
        }

        return itemStack.getTag();
    }

    public int getBuildPercentage() {
        if (world.isRemote) {
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
        if (world.isRemote) {
            return clientErrorMode;
        } else {
            return errorMode;
        }
    }

    private static boolean isRealizedTab(ItemStack stack) {
        return stack.getItem() == DimensionBuilderModule.REALIZED_DIMENSION_TAB.get();
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        if (cap == CapabilityInfusable.INFUSABLE_CAPABILITY) {
            return infusableHandler.cast();
        }
        return super.getCapability(cap, facing);
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
        public String getString() {
            return name;
        }
    }

}
