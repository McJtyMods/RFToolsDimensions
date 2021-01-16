package mcjty.rftoolsdim.modules.workbench.blocks;

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
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.PartItem;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolsdim.setup.Registration.DIMENSIONAL_SHARD;

public class WorkbenchTileEntity extends GenericTileEntity {

    public static final int SLOT_EMPTY_DIMLET = 0;
    public static final int SLOT_MEMORY_PART = 1;
    public static final int SLOT_ENERGY_PART = 2;
    public static final int SLOT_ESSENCE = 3;
    public static final int SLOT_OUTPUT = 4;
    public static final int SLOT_PATTERN = 5;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(4 + 7*7 + 1)
            .slot(specific(DimletItem::isEmptyDimlet), CONTAINER_CONTAINER, SLOT_EMPTY_DIMLET, 11, 7)
            .slot(specific(s -> s.getItem() instanceof PartItem), CONTAINER_CONTAINER, SLOT_MEMORY_PART, 33, 7)
            .slot(specific(s -> s.getItem() instanceof PartItem), CONTAINER_CONTAINER, SLOT_ENERGY_PART, 55, 7)
            .slot(generic(), CONTAINER_CONTAINER, SLOT_ESSENCE, 77, 7)
            .slot(generic(), CONTAINER_CONTAINER, SLOT_OUTPUT, 157, 7)
            .box(specific(WorkbenchTileEntity::isValidPatternItem), CONTAINER_CONTAINER, SLOT_PATTERN, 11, 28, 7, 7)
            .playerSlots(11, 162));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimlet Workbench")
            .containerSupplier((windowId,player) -> new GenericContainer(WorkbenchModule.CONTAINER_WORKBENCH.get(), windowId, CONTAINER_FACTORY.get(), getPos(), WorkbenchTileEntity.this))
            .itemHandler(() -> items));

    public WorkbenchTileEntity() {
        super(WorkbenchModule.TYPE_WORKBENCH.get());
    }

    private static boolean isValidPatternItem(ItemStack stack) {
        Item item = stack.getItem();
        return item == DIMENSIONAL_SHARD || item == DimletModule.COMMON_ESSENCE.get() ||
                item == DimletModule.RARE_ESSENCE.get() ||
                item == DimletModule.LEGENDARY_ESSENCE.get();
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(WorkbenchTileEntity::new)
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

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                // @todo 1.15
                return true;
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
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

}
