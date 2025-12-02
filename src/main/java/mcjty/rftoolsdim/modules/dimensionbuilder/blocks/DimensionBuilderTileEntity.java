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
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.data.DimensionCreator;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.power.PowerHandler;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderConfig;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.data.RealizedTabData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import mcjty.lib.setup.Registration;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class DimensionBuilderTileEntity extends TickingTileEntity {

    @GuiValue
    private int errorMode = 0;

    public static final int SLOT_DIMENSION_TAB = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(DimensionBuilderTileEntity::isRealizedTab).in().out(),
                    SLOT_DIMENSION_TAB, 28, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> isRealizedTab(stack))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<DimensionBuilderTileEntity, GenericItemHandler> ITEM_CAP = be -> be.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, DimensionBuilderConfig.BUILDER_MAXENERGY.get(), DimensionBuilderConfig.BUILDER_RECEIVEPERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<DimensionBuilderTileEntity, GenericEnergyStorage> ENERGY_CAP = be -> be.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<DimensionBuilderTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Dimension Builder")
            .containerSupplier(container(DimensionBuilderModule.CONTAINER_DIMENSION_BUILDER, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .integerListener(Sync.integer(be::getBuildPercentage, v -> be.clientBuildPercentage = v))
            .setupSync(be);

    private final DefaultInfusable infusableHandler = new DefaultInfusable(DimensionBuilderTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<DimensionBuilderTileEntity, IInfusable> INFUSABLE_CAP = be -> be.infusableHandler;

    // For usage in the gui
    private int clientBuildPercentage = 0;

    private int state = 0;          // For front state

    public static final short OK = 0;
    public static final short ERROR_NOOWNER = -1;
    public static final short ERROR_TOOMANYDIMENSIONS = -2;
    public static final short ERROR_COLLISION = -3;


    public DimensionBuilderTileEntity(BlockPos pos, BlockState state) {
        super(DimensionBuilderModule.TYPE_DIMENSION_BUILDER.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(DimensionBuilderTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsbase:dimensions/dimension_builder"))
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
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTag = new CompoundTag();
        this.saveClientDataToNBT(nbtTag, level.registryAccess());
        nbtTag.putInt("errorMode", errorMode);
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag updateTag = super.getUpdateTag(provider);
        updateTag.putInt("errorMode", errorMode);
        return updateTag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        int oldstate = state;
        int oldError = errorMode;
        super.onDataPacket(net, packet, provider);
        errorMode = (short) packet.getTag().getInt("errorMode");
        if (oldstate != state || oldError != this.errorMode) {
            getLevel().setBlocksDirty(worldPosition, getBlockState(), getBlockState());
        }
    }

    @Override
    public void tickServer() {
        RealizedTabData tab = hasTab();
        if (tab == null || tab.tickCost() == 0) {
            setState(-1);
            return;
        }

        if (!isMachineEnabled()) {
            setState(-1);
            return;
        }

        int ticksLeft = tab.ticksLeft();
        if (ticksLeft > 0) {
            ItemStack itemStack = items.getStackInSlot(SLOT_DIMENSION_TAB);
            ticksLeft = createDimensionTick(itemStack, tab);
        } else {
            maintainDimensionTick(tab);
        }

        setState(ticksLeft);
    }

    private void maintainDimensionTick(RealizedTabData tab) {
        if (tab.dimension().isPresent()) {
            ResourceLocation id = tab.dimension().get();
            DimensionData data = PersistantDimensionManager.get(level).getData(id);
            if (data == null) {
                return;
            }
            long rf = energyStorage.getEnergy();

            long energy = data.getEnergy();
            long maxEnergy = PowerHandler.calculateMaxDimensionPower(id, level) - energy;
            if (rf > maxEnergy) {
                rf = maxEnergy;
            }
            energyStorage.consumeEnergy(rf);
            data.setEnergy(level, energy + rf);
            PersistantDimensionManager.get(level).save();
        }
    }

    private static final Random random = new Random();

    private int createDimensionTick(ItemStack stack, RealizedTabData tab) {

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
        String name = tab.name().orElse("");
        DimensionCreator.get().markReservedName(level, worldPosition, name);

        int createCost = tab.rfCreateCost();
        float inf = infusableHandler.getInfusedFactor();
        createCost = (int) (createCost * (2.0f - inf) / 2.0f);

        if (isCheaterDimension(tab) || (energyStorage.getEnergyStored() >= createCost)) {
            if (!DimensionCreator.get().isNameAvailable(level, worldPosition, name)) {
                // The name is not available. Stop building!
                errorMode = ERROR_COLLISION;
                setChanged();
                return tab.ticksLeft();
            }

            int ticksLeft = tab.ticksLeft();
            if (isCheaterDimension(tab)) {
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
            tab = tab.withTicksLeft(ticksLeft);
            stack.set(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA, tab);
            if (ticksLeft <= 0) {
                String descriptorString = tab.descriptor();
                DimensionDescriptor descriptor = new DimensionDescriptor();
                descriptor.read(descriptorString);

                DimensionDescriptor randomizedDescriptor = descriptor.createRandomizedDescriptor(random);

                if (!DimensionCreator.get().isNameAvailable(level, worldPosition, name)) {
                    // Error!
                    errorMode = ERROR_COLLISION;
                    setChanged();
                    return 0;
                }
                if (!DimensionCreator.get().isDescriptorAvailable(level, descriptor)) {
                    // Error!
                    errorMode = ERROR_COLLISION;
                    setChanged();
                    return 0;
                }

                long seed = random.nextLong();
                ServerLevel newworld = DimensionCreator.get().createWorld((ServerLevel) this.level, name, seed, descriptor, randomizedDescriptor, getOwnerUUID());
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(RFToolsDim.MODID, name);
                tab = tab.withDimension(id);
                CompiledDescriptor compiledDescriptor = DimensionCreator.get().getCompiledDescriptor(newworld);
                tab = tab.withRfMaintainCost(compiledDescriptor.getActualPowerCost());
                stack.set(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA, tab);
                setChanged();

                placeMatterReceiver(newworld, name);
            }
        }
        return tab.ticksLeft();
    }

    private void placeMatterReceiver(ServerLevel newworld, String name) {
        int y = newworld.getMaxBuildHeight() - 10;
        while (y >= newworld.getMinBuildHeight() + 1) {
            if (newworld.getBlockState(new BlockPos(8, y, 8)).getBlock() == Blocks.COMMAND_BLOCK) {
                RFToolsUtilityCompat.createTeleporter(newworld, new BlockPos(8, y, 8), name);
                return;
            }
            y--;
        }
        // It failed, the commandblock may have been overwritten. Luckily we recorded the height
        // of the platform somewhere
        int platformHeight = DimensionCreator.get().getPlatformHeight(newworld.dimension().location());
        RFToolsUtilityCompat.createTeleporter(newworld, new BlockPos(8, platformHeight, 8), name);
        newworld.setBlockAndUpdate(new BlockPos(8, platformHeight+1, 8), Blocks.AIR.defaultBlockState());
        newworld.setBlockAndUpdate(new BlockPos(8, platformHeight+2, 8), Blocks.AIR.defaultBlockState());
    }

    private boolean isCheaterDimension(RealizedTabData tab) {
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
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        items.load(tag, "items", provider);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        items.save(tag, "items", provider);
    }

    public RealizedTabData hasTab() {
        ItemStack itemStack = items.getStackInSlot(SLOT_DIMENSION_TAB);
        if (itemStack.isEmpty()) {
            return null;
        }

        return itemStack.get(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA);
    }

    public int getBuildPercentage() {
        if (level.isClientSide) {
            return clientBuildPercentage;
        } else {
            RealizedTabData tab = hasTab();
            if (tab != null) {
                int ticksLeft = tab.ticksLeft();
                int tickCost = tab.tickCost();
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

    public enum OperationType implements StringRepresentable {
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        energyStorage.save(tag, "energy", provider);
        items.save(tag, "items", provider);
        infusableHandler.save(tag, "infusable");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energyStorage.load(tag, "energy", provider);
        items.load(tag, "items", provider);
        infusableHandler.load(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        energyStorage.applyImplicitComponents(input.get(mcjty.lib.setup.Registration.ITEM_ENERGY));
        items.applyImplicitComponents(input.get(mcjty.lib.setup.Registration.ITEM_INVENTORY));
        infusableHandler.applyImplicitComponents(input.get(mcjty.lib.setup.Registration.ITEM_INFUSABLE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        energyStorage.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
        infusableHandler.collectImplicitComponents(builder);
    }
}
