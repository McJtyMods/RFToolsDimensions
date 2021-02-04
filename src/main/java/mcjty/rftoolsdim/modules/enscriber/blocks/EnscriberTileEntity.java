package mcjty.rftoolsdim.modules.enscriber.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class EnscriberTileEntity extends GenericTileEntity {

    public static final String CMD_STORE = "enscriber.store";
    public static final Key<String> PARAM_NAME = new Key<>("name", Type.STRING);

    public static final String CMD_EXTRACT = "enscriber.extract";

    public static final int SLOT_DIMLETS = 0;
    public static final int SIZE_DIMLETS = 13*7;
    public static final int SLOT_TAB = SLOT_DIMLETS + SIZE_DIMLETS;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(SIZE_DIMLETS + 1)
            .box(specific(DimletItem::isReadyDimlet), CONTAINER_CONTAINER, SLOT_DIMLETS, 13, 7, 13, 7)
            .slot(specific(EnscriberTileEntity::isDimensionTab), CONTAINER_CONTAINER, SLOT_TAB, 13, 142)
            .playerSlots(85, 142));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimlet Workbench")
            .containerSupplier((windowId,player) -> new GenericContainer(EnscriberModule.CONTAINER_ENSCRIBER.get(), windowId, CONTAINER_FACTORY.get(), getPos(), EnscriberTileEntity.this))
            .itemHandler(() -> items)
            .shortListener(new IntReferenceHolder() {
                @Override
                public int get() {
                    return error.getCode().ordinal();
                }

                @Override
                public void set(int value) {
                    clientErrorCode = value;
                }
            }));

    public EnscriberTileEntity() {
        super(EnscriberModule.TYPE_ENSCRIBER.get());
    }

    private DescriptorError error = DescriptorError.OK;
    private int clientErrorCode = 0;

    private static boolean isDimensionTab(ItemStack s) {
        return s.getItem() == DimensionBuilderModule.EMPTY_DIMENSION_TAB.get() || s.getItem() == DimensionBuilderModule.REALIZED_DIMENSION_TAB.get();
    }


    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(EnscriberTileEntity::new)
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

    public String getDimensionName() {
        ItemStack stack = items.getStackInSlot(SLOT_TAB);
        if (!stack.isEmpty() && stack.getItem() == DimensionBuilderModule.REALIZED_DIMENSION_TAB.get()) {
            CompoundNBT tagCompound = stack.getTag();
            if (tagCompound != null) {
                String name = tagCompound.getString("name");
                return name;
            }
        }
        return null;
    }

    private boolean checkOwnerDimlet() {
        for (int i = 0 ; i < items.getSlots() ; i++) {
            ItemStack stack = items.getStackInSlot(i);
            DimletKey dimletKey = DimletTools.getDimletKey(stack);
            if (DimletTools.isOwnerDimlet(dimletKey)) {
                return true;
            }
        }
        return false;
    }

    private void storeDimlets(PlayerEntity player, String name) {
        if (DimensionConfig.OWNER_DIMLET_REQUIRED.get()) {
            if (checkOwnerDimlet()) {
                Logging.warn(player, "You need an owner dimlet to make a dimension!");
                return;
            }
        }

        DimensionDescriptor descriptor = convertToDimensionDescriptor(player, true);
        ItemStack realizedTab = createRealizedTab(descriptor);

        DimensionData data = PersistantDimensionManager.get(world).getData(descriptor);
        if (data != null) {
            name = data.getId().getPath();
        }

        realizedTab.getOrCreateTag().putString("name", name);

        items.setStackInSlot(SLOT_TAB, realizedTab);

        markDirty();
    }

    /**
     * Create a realized dimension tab by taking a map of ids per type and storing
     * that in the NBT of the realized dimension tab.
     */
    private ItemStack createRealizedTab(DimensionDescriptor descriptor) {
        ItemStack realizedTab = new ItemStack(DimensionBuilderModule.REALIZED_DIMENSION_TAB.get(), 1);
        CompoundNBT tagCompound = realizedTab.getOrCreateTag();
        String compact = descriptor.compact();
        tagCompound.putString("descriptor", compact);

        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        DimensionData data = mgr.getData(descriptor);

        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();

        if (data != null) {
            // The dimension was already created.
            tagCompound.putInt("ticksLeft", 0);
            tagCompound.putString("dimension", data.getId().toString());
            compiledDescriptor.compile(descriptor, data.getRandomizedDescriptor());
        } else {
            compiledDescriptor.compile(descriptor, DimensionDescriptor.EMPTY);  // Randomized part not known yet
            tagCompound.putInt("ticksLeft", compiledDescriptor.getActualTickCost());
        }

        tagCompound.putInt("tickCost", compiledDescriptor.getActualTickCost());
        tagCompound.putInt("rfCreateCost", compiledDescriptor.getCreateCostPerTick());
        tagCompound.putInt("rfMaintainCost", compiledDescriptor.getActualPowerCost());

        return realizedTab;
    }


    /**
     * Convert the dimlets in the inventory to a dimension descriptor.
     */
    private DimensionDescriptor convertToDimensionDescriptor(@Nullable PlayerEntity player, boolean forReal) {
        DimensionDescriptor descriptor = new DimensionDescriptor();
        List<DimletKey> dimlets = descriptor.getDimlets();

        long forcedSeed = 0;

        for (int i = 0 ; i < SIZE_DIMLETS ; i++) {
            ItemStack stack = items.getStackInSlot(i + SLOT_DIMLETS);
            if (!stack.isEmpty()) {
                DimletKey key = DimletTools.getDimletKey(stack);
                DimletSettings settings = DimletDictionary.get().getSettings(key);
                if (settings != null) {
                    // Make sure the dimlet is not blacklisted.
                    dimlets.add(key);
                    CompoundNBT tagCompound = stack.getTag();
                    // @todo 1.16 is this the way?
                    if (tagCompound != null && tagCompound.getLong("forcedSeed") != 0) {
                        forcedSeed = tagCompound.getLong("forcedSeed");
                    }
                    if (forReal) {
                    items.setStackInSlot(i + SLOT_DIMLETS, ItemStack.EMPTY);}
                } else {
                    if (player != null) {
                        Logging.warn(player, "Dimlet " + key.getType().name() + "." + key.getKey() + " was not included in the tab because it is blacklisted");
                    }
                }
            }
        }
        return descriptor;
    }

    private void validateDimlets() {
        DimensionDescriptor descriptor = convertToDimensionDescriptor(null, false);
        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();
        error = compiledDescriptor.compile(descriptor, DimensionDescriptor.EMPTY);  // We just need to check the descriptor. Not randomized
    }

    public int getClientErrorCode() {
        return clientErrorCode;
    }

    private void extractDimlets() {
        ItemStack realizedTab = items.getStackInSlot(SLOT_TAB);
        CompoundNBT tagCompound = realizedTab.getTag();
        if (tagCompound != null) {
            long forcedSeed = tagCompound.getLong("forcedSeed");
            String descString = tagCompound.getString("descriptor");
            DimensionDescriptor descriptor = new DimensionDescriptor();
            descriptor.read(descString);
            List<DimletKey> dimlets = descriptor.getDimlets();
            int idx = SLOT_DIMLETS;
            for (DimletKey key : dimlets) {
                boolean hasDimension = tagCompound.contains("dimension");
                if (DimensionConfig.OWNER_DIMLET_REQUIRED.get() && hasDimension) {
                    // If we need owner dimlets and the dimension is created we don't extract the owern dimlet.
                    if (DimletTools.isOwnerDimlet(key)) {
                        continue;
                    }
                }

                ItemStack dimletStack = DimletTools.getDimletStack(key);
                // @todo 1.16
//                if(key.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(key) == SpecialType.SPECIAL_SEED) {
//                    dimletStack.getTag().setLong("forcedSeed", forcedSeed);
//                }
                items.setStackInSlot(idx, dimletStack);
                idx++;
            }
        }

        items.setStackInSlot(SLOT_TAB, new ItemStack(DimensionBuilderModule.EMPTY_DIMENSION_TAB.get()));
        markDirty();
    }


    @Override
    public boolean execute(PlayerEntity player, String command, TypedMap params) {
        boolean rc = super.execute(player, command, params);
        if (rc) {
            return true;
        }
        if (CMD_STORE.equals(command)) {
            storeDimlets(player, params.get(PARAM_NAME));
            return true;
        } else if (CMD_EXTRACT.equals(command)) {
            extractDimlets();
            return true;
        }
        return false;
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_TAB) {
                    return EnscriberTileEntity.isDimensionTab(stack);
                }
                return stack.getItem() instanceof DimletItem;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_TAB) {
                    return EnscriberTileEntity.isDimensionTab(stack);
                }
                return stack.getItem() instanceof DimletItem;
            }

            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                validateDimlets();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

}
