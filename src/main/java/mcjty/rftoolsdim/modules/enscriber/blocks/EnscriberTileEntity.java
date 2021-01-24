package mcjty.rftoolsdim.modules.enscriber.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
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
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class EnscriberTileEntity extends GenericTileEntity {

    public static final String CMD_STORE = "enscriber.store";
    public static final Key<String> PARAM_NAME = new Key<>("name", Type.STRING);
    public static final Key<String> VALUE_NAME = new Key<>("name", Type.STRING);

    public static final String CMD_EXTRACT = "enscriber.extract";

    private boolean tabSlotHasChanged = false;

    public static final int SLOT_DIMLETS = 0;
    public static final int SIZE_DIMLETS = 13*7;
    public static final int SLOT_TAB = SLOT_DIMLETS + SIZE_DIMLETS;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(SIZE_DIMLETS + 1)
            .box(specific(DimletItem::isReadyDimlet), CONTAINER_CONTAINER, SLOT_DIMLETS, 13, 7, 13, 7)
            .slot(specific(s -> true /* @todo */), CONTAINER_CONTAINER, SLOT_TAB, 13, 142)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimlet Workbench")
            .containerSupplier((windowId,player) -> new GenericContainer(EnscriberModule.CONTAINER_ENSCRIBER.get(), windowId, CONTAINER_FACTORY.get(), getPos(), EnscriberTileEntity.this))
            .itemHandler(() -> items));

    public EnscriberTileEntity() {
        super(EnscriberModule.TYPE_ENSCRIBER.get());
    }


    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_NAME, this::getDimensionName, this::setDimensionName),
        };
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
        // @todo 1.16
//        if (!stack.isEmpty() && stack.getItem() == ModItems.realizedDimensionTabItem) {
//            CompoundNBT tagCompound = stack.getTagCompound();
//            if (tagCompound != null) {
//                String name = tagCompound.getString("name");
//                if (name != null) {
//                    return name;
//                }
//            }
//        }
        return null;
    }

    private void setDimensionName(String name) {
        // @todo 1.16
//        ItemStack realizedTab = inventoryHelper.getStackInSlot(SLOT_TAB);
//        if (!realizedTab.isEmpty()) {
//            CompoundNBT tagCompound = realizedTab.getTagCompound();
//            if (tagCompound == null) {
//                tagCompound = new CompoundNBT();
//                realizedTab.setTagCompound(tagCompound);
//            }
//            tagCompound.setString("name", name);
//            if (tagCompound.hasKey("id")) {
//                Integer id = tagCompound.getInteger("id");
//                RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(getWorld());
//                DimensionInformation information = dimensionManager.getDimensionInformation(id);
//                if (information != null) {
//                    information.setName(name);
//                    dimensionManager.save(getWorld());
//                }
//            }
//            markDirty();
//        }
    }

    private void storeDimlets(PlayerEntity player) {
        // @todo 1.16
//        if (GeneralConfiguration.ownerDimletsNeeded) {
//            if (checkOwnerDimlet()) {
//                Logging.warn(player, "You need an owner dimlet to make a dimension!");
//                return;
//            }
//        }

        // @todo 1.16
//        DimensionDescriptor descriptor = convertToDimensionDescriptor(player);
//        ItemStack realizedTab = createRealizedTab(descriptor, getWorld());
//        inventoryHelper.setStackInSlot(SLOT_TAB, realizedTab);

        markDirty();
    }

    private void extractDimlets() {
        ItemStack realizedTab = items.getStackInSlot(SLOT_TAB);
        CompoundNBT tagCompound = realizedTab.getTag();
        if (tagCompound != null) {
            // @todo 1.16
//            long forcedSeed = tagCompound.getLong("forcedSeed");
//            List<DimletKey> descriptors = DimensionDescriptor.parseDescriptionString(tagCompound.getString("descriptionString"));
//            int idx, skip;
//            if(SIZE_DIMLETS >= 2 * descriptors.size()) {
//                idx = SLOT_DIMLETS + 1;
//                skip = 2;
//            } else {
//                idx = SLOT_DIMLETS;
//                skip = 1;
//            }
//            for (DimletKey descriptor : descriptors) {
//                int id = tagCompound.getInteger("id");
//                if (GeneralConfiguration.ownerDimletsNeeded && id != 0) {
//                    // If we need owner dimlets and the dimension is created we don't extract the owern dimlet.
//                    if (descriptor.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(descriptor) == SpecialType.SPECIAL_OWNER) {
//                        continue;
//                    }
//                }
//
//                ItemStack dimletStack = KnownDimletConfiguration.getDimletStack(descriptor);
//                if(descriptor.getType() == DimletType.DIMLET_SPECIAL && DimletObjectMapping.getSpecial(descriptor) == SpecialType.SPECIAL_SEED) {
//                    dimletStack.getTagCompound().setLong("forcedSeed", forcedSeed);
//                }
//                inventoryHelper.setStackInSlot(idx, dimletStack);
//                idx += skip;
//            }
        }

        // @todo 1.16
//        items.setStackInSlot(SLOT_TAB, new ItemStack(ModItems.emptyDimensionTabItem));
        markDirty();
    }

    public boolean hasTabSlotChangedAndClear() {
        boolean rc = tabSlotHasChanged;
        tabSlotHasChanged = false;
        return rc;
    }



    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_STORE.equals(command)) {
            storeDimlets(playerMP);
            setDimensionName(params.get(PARAM_NAME));
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
                // @todo 1.16
                return true;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }

            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                if (index == SLOT_TAB) {
                    tabSlotHasChanged = true;
                }
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
