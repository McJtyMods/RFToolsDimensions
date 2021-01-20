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
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.PartItem;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolsdim.setup.Registration.DIMENSIONAL_SHARD;

public class WorkbenchTileEntity extends GenericTileEntity {

    public static final String CMD_SUGGESTPARTS = "workbench.suggestParts";
    public static final String CMD_CHEATDIMLET = "workbench.cheatDimlet";
    public static final Key<String> PARAM_TYPE = new Key<>("type", Type.STRING);
    public static final Key<String> PARAM_ID = new Key<>("id", Type.STRING);

    public static final int SLOT_EMPTY_DIMLET = 0;
    public static final int SLOT_MEMORY_PART = 1;
    public static final int SLOT_ENERGY_PART = 2;
    public static final int SLOT_ESSENCE = 3;
    public static final int SLOT_OUTPUT = 4;
    public static final int SLOT_PATTERN = 5;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(4 + DimletPattern.PATTERN_DIM*DimletPattern.PATTERN_DIM + 1)
            .slot(specific(DimletItem::isEmptyDimlet), CONTAINER_CONTAINER, SLOT_EMPTY_DIMLET, 11, 7)
            .slot(specific(s -> s.getItem() instanceof PartItem), CONTAINER_CONTAINER, SLOT_MEMORY_PART, 33, 7)
            .slot(specific(s -> s.getItem() instanceof PartItem), CONTAINER_CONTAINER, SLOT_ENERGY_PART, 55, 7)
            .slot(generic(), CONTAINER_CONTAINER, SLOT_ESSENCE, 77, 7)
            .slot(generic(), CONTAINER_CONTAINER, SLOT_OUTPUT, 230, 158+18+18+20)
            .box(specific(WorkbenchTileEntity::isValidPatternItem), CONTAINER_CONTAINER, SLOT_PATTERN, 11, 28, DimletPattern.PATTERN_DIM, DimletPattern.PATTERN_DIM)
            .playerSlots(11, 158));

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

    private void cheatDimlet(PlayerEntity player, DimletKey key) {
        ItemStack dimlet = DimletTools.getDimletStack(key);
        ItemHandlerHelper.giveItemToPlayer(player, dimlet);
    }

    private void suggestParts(PlayerEntity player, DimletKey key) {
        tryFindAndFitItem(player, s -> s.isItemEqual(DimletTools.getEmptyDimletStack(key.getType())), SLOT_EMPTY_DIMLET);
        tryFindAndFitItem(player, s -> s.isItemEqual(DimletTools.getNeededMemoryPart(key)), SLOT_MEMORY_PART);
        tryFindAndFitItem(player, s -> s.isItemEqual(DimletTools.getNeededEnergyPart(key)), SLOT_ENERGY_PART);
        ItemStack essence = DimletTools.getNeededEssence(key);
        if (!essence.isEmpty()) {
            tryFindAndFitItem(player, s -> isFullEssence(s, essence, key.getKey()), SLOT_ESSENCE);
        } else {
            tryFindAndFitItem(player, s -> false, SLOT_ESSENCE);
        }

        DimletPattern pattern = KnowledgeManager.get().getPattern(world, key);
        if (pattern != null) { // Can in principle not happen but ...
            String[] p = pattern.getPattern();
            int slotNumber = SLOT_PATTERN;
            for (String value : p) {
                for (int x = 0; x < value.length(); x++) {
                    ItemStack neededPattern = KnowledgeManager.getPatternItem(value.charAt(x));
                    if (!neededPattern.isEmpty()) {
                        tryFindAndFitItem(player, s -> s.isItemEqual(neededPattern), slotNumber);
                    } else {
                        tryFindAndFitItem(player, s -> false, slotNumber);
                    }
                    slotNumber++;
                }
            }
        }

        player.container.detectAndSendChanges();
    }

    private void tryFindAndFitItem(PlayerEntity player, Predicate<ItemStack> desired, int slotNumber) {
        if (desired.test(items.getStackInSlot(slotNumber))) {
            // Already ok
            return;
        }

        if (!items.getStackInSlot(slotNumber).isEmpty()) {
            // We need to move this item away first
            ItemHandlerHelper.giveItemToPlayer(player, items.getStackInSlot(slotNumber));
            items.setStackInSlot(slotNumber, ItemStack.EMPTY);
        }

        NonNullList<ItemStack> inventory = player.inventory.mainInventory;
        for (ItemStack itemStack : inventory) {
            if (desired.test(itemStack)) {
                ItemStack copy = itemStack.copy();
                copy.setCount(1);
                items.setStackInSlot(slotNumber, copy);
                itemStack.shrink(1);
                return;
            }
        }
    }

    private boolean isFullEssence(ItemStack stack, ItemStack desired, String desiredKey) {
        if (stack.isItemEqual(desired)) {
            if (stack.getItem() == EssencesModule.BIOME_ABSORBER_ITEM.get()) {
                String biome = BiomeAbsorberTileEntity.getBiome(stack);
                if (Objects.equals(desiredKey, biome)) {
                    return BiomeAbsorberTileEntity.getProgress(stack) >= 100;
                }
            } else if (stack.getItem() == EssencesModule.BLOCK_ABSORBER_ITEM.get()) {
                String block = BlockAbsorberTileEntity.getBlock(stack);
                if (Objects.equals(desiredKey, block)) {
                    return BlockAbsorberTileEntity.getProgress(stack) >= 100;
                }
            }
        }
        return false;
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SUGGESTPARTS.equals(command)) {
            String type = params.get(PARAM_TYPE);
            String id = params.get(PARAM_ID);
            suggestParts(playerMP, new DimletKey(DimletType.byName(type), id));
            return true;
        } else if (CMD_CHEATDIMLET.equals(command)) {
            String type = params.get(PARAM_TYPE);
            String id = params.get(PARAM_ID);
            cheatDimlet(playerMP, new DimletKey(DimletType.byName(type), id));
            return true;
        }
        return false;
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
