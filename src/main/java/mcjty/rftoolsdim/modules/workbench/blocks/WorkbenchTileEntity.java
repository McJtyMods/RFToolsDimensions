package mcjty.rftoolsdim.modules.workbench.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.client.DimletClientHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.PartItem;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeKey;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.network.PacketPatternToClient;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolsdim.modules.knowledge.data.DimletPattern.PATTERN_DIM;
import static mcjty.rftoolsdim.setup.Registration.DIMENSIONAL_SHARD;

public class WorkbenchTileEntity extends GenericTileEntity {

    public static final int SLOT_EMPTY_DIMLET = 0;
    public static final int SLOT_MEMORY_PART = 1;
    public static final int SLOT_ENERGY_PART = 2;
    public static final int SLOT_ESSENCE = 3;
    public static final int SLOT_OUTPUT = 4;
    public static final int SLOT_PATTERN = 5;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(4 + PATTERN_DIM* PATTERN_DIM + 1)
            .slot(specific(DimletItem::isEmptyDimlet).in().out(), SLOT_EMPTY_DIMLET, 11, 7)
            .slot(specific(s -> s.getItem() instanceof PartItem).in().out(), SLOT_MEMORY_PART, 33, 7)
            .slot(specific(s -> s.getItem() instanceof PartItem).in().out(), SLOT_ENERGY_PART, 55, 7)
            .slot(generic().in().out(), SLOT_ESSENCE, 77, 7)
            .slot(generic().out(), SLOT_OUTPUT, 232, 158+18+18+22)
            .box(specific(WorkbenchTileEntity::isValidPatternItem).in().out(), SLOT_PATTERN, 11, 28, PATTERN_DIM, PATTERN_DIM)
            .playerSlots(11, 158));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimlet Workbench")
            .containerSupplier((windowId,player) -> new GenericContainer(WorkbenchModule.CONTAINER_WORKBENCH.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), WorkbenchTileEntity.this))
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
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/dimlet_workbench"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.ROTATION;
            }
        };
    }

    private boolean createDimlet() {
        ItemStack emptyDimlet = items.getStackInSlot(SLOT_EMPTY_DIMLET);
        ItemStack memoryPart = items.getStackInSlot(SLOT_MEMORY_PART);
        ItemStack energyPart = items.getStackInSlot(SLOT_ENERGY_PART);
        ItemStack essenceStack = items.getStackInSlot(SLOT_ESSENCE);
        DimletType type = DimletItem.getType(emptyDimlet);
        if (type == null) {
            return false;
        }
        if (memoryPart.isEmpty()) {
            return false;
        }
        if (energyPart.isEmpty()) {
            return false;
        }

        String[] pattern = new String[PATTERN_DIM];
        int slot = SLOT_PATTERN;
        for (int y = 0 ; y < PATTERN_DIM ; y++) {
            String p = "";
            for (int x = 0 ; x < PATTERN_DIM ; x++) {
                ItemStack stack = items.getStackInSlot(slot);
                char c = KnowledgeManager.getPatternChar(stack);
                p += c;
                slot++;
            }
            pattern[y] = p;
        }

        DimletKey key = DimletDictionary.get().tryCraft(level, type, memoryPart, energyPart, essenceStack, new DimletPattern(pattern));
        if (key == null) {
            return false;
        }
        ItemStack dimletStack = DimletTools.getDimletStack(key);
        if (dimletStack.isEmpty()) {
            return false;
        }

        items.decrStackSize(SLOT_EMPTY_DIMLET, 1);
        items.decrStackSize(SLOT_MEMORY_PART, 1);
        items.decrStackSize(SLOT_ENERGY_PART, 1);
        items.decrStackSize(SLOT_ESSENCE, 1);
        for (int i = SLOT_PATTERN ; i <= SLOT_PATTERN + PATTERN_DIM * PATTERN_DIM ; i++) {
            items.decrStackSize(i, 1);
        }
        items.setStackInSlot(SLOT_OUTPUT, dimletStack);
        return true;
    }

    private void cheatDimlet(PlayerEntity player, DimletKey key) {
        ItemStack dimlet = DimletTools.getDimletStack(key);
        ItemHandlerHelper.giveItemToPlayer(player, dimlet);
    }

    private void hilightPattern(PlayerEntity player, DimletKey key) {
        DimletPattern pattern = KnowledgeManager.get().getPattern(LevelTools.getOverworld(player.level).getSeed(), key);
        if (pattern != null) {
            String[] p = pattern.getPattern();
            RFToolsDimMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                    new PacketPatternToClient(p));
        }
    }

    private void suggestParts(PlayerEntity player, DimletKey key) {
        if (!key.getType().usesKnowledgeSystem()) {
            return;
        }

        tryFindAndFitItem(player, s -> s.sameItem(DimletTools.getEmptyDimletStack(key.getType())), SLOT_EMPTY_DIMLET);
        tryFindAndFitItem(player, s -> s.sameItem(DimletTools.getNeededMemoryPart(key)), SLOT_MEMORY_PART);
        tryFindAndFitItem(player, s -> s.sameItem(DimletTools.getNeededEnergyPart(key)), SLOT_ENERGY_PART);
        ItemStack essence = DimletTools.getNeededEssence(key, DimletDictionary.get().getSettings(key));
        if (!essence.isEmpty()) {
            tryFindAndFitItem(player, s -> DimletTools.isFullEssence(s, essence, key.getKey()), SLOT_ESSENCE);
        } else {
            tryFindAndFitItem(player, s -> false, SLOT_ESSENCE);
        }

        DimletPattern pattern = KnowledgeManager.get().getPattern(LevelTools.getOverworld(level).getSeed(), key);
        if (pattern != null) {
            String[] p = pattern.getPattern();
            int slotNumber = SLOT_PATTERN;
            for (String value : p) {
                for (int x = 0; x < value.length(); x++) {
                    ItemStack neededPattern = KnowledgeManager.getPatternItem(value.charAt(x));
                    if (!neededPattern.isEmpty()) {
                        tryFindAndFitItem(player, s -> s.sameItem(neededPattern), slotNumber);
                    } else {
                        tryFindAndFitItem(player, s -> false, slotNumber);
                    }
                    slotNumber++;
                }
            }
        }

        player.inventoryMenu.broadcastChanges();
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

        NonNullList<ItemStack> inventory = player.inventory.items;
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

    public static final Key<String> PARAM_TYPE = new Key<>("type", Type.STRING);
    public static final Key<String> PARAM_ID = new Key<>("id", Type.STRING);

    @ServerCommand
    public static final Command<?> CMD_SUGGESTPARTS = Command.<WorkbenchTileEntity>create("workbench.suggestParts",
        (te, player, params) -> te.suggestParts(player, new DimletKey(DimletType.byName(params.get(PARAM_TYPE)), params.get(PARAM_ID))));
    @ServerCommand
    public static final Command<?> CMD_CHEATDIMLET = Command.<WorkbenchTileEntity>create("workbench.cheatDimlet",
        (te, player, params) -> te.createDimlet());
    @ServerCommand
    public static final Command<?> CMD_HILIGHT_PATTERN = Command.<WorkbenchTileEntity>create("workbench.hilightPattern",
        (te, player, params) -> te.hilightPattern(player, new DimletKey(DimletType.byName(params.get(PARAM_TYPE)), params.get(PARAM_ID))));
    @ServerCommand
    public static final Command<?> CMD_CREATE_DIMLET = Command.<WorkbenchTileEntity>create("workbench.createDimlet",
        (te, player, params) -> te.cheatDimlet(player, new DimletKey(DimletType.byName(params.get(PARAM_TYPE)), params.get(PARAM_ID))));

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot) {
                    case SLOT_EMPTY_DIMLET:
                        return DimletItem.isEmptyDimlet(stack);
                    case SLOT_MEMORY_PART:
                        return stack.getItem() instanceof PartItem;
                    case SLOT_ENERGY_PART:
                        return stack.getItem() instanceof PartItem;
                    case SLOT_ESSENCE:
                        return true;
                    case SLOT_OUTPUT:
                        return DimletItem.isReadyDimlet(stack);
                    default:
                        return isValidPatternItem(stack);
                }
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_OUTPUT) {
                    return false;
                }
                return isItemValid(slot, stack);
            }
        };
    }

    private Set<KnowledgeKey> getSupportedKnowledgeKeys() {
        Set<KnowledgeKey> knownKeys = new HashSet<>();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            TileEntity tileEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (tileEntity instanceof KnowledgeHolderTileEntity) {
                ((KnowledgeHolderTileEntity) tileEntity).addKnownKnowledgeKeys(knownKeys);
            }
        }
        return knownKeys;
    }

    public List<DimletClientHelper.DimletWithInfo> getDimlets() {
        Set<KnowledgeKey> knownKeys = getSupportedKnowledgeKeys();
        List<DimletClientHelper.DimletWithInfo> dimlets = new ArrayList<>();
        for (DimletKey dimlet : DimletDictionary.get().getDimlets()) {
            KnowledgeKey kkey = KnowledgeManager.get().getKnowledgeKey(LevelTools.getOverworld(level).getSeed(), dimlet);
            boolean craftable = knownKeys.contains(kkey);
            dimlets.add(new DimletClientHelper.DimletWithInfo(dimlet, craftable));
        }
        return dimlets;
    }

    @ServerCommand(type = DimletClientHelper.DimletWithInfo.class, serializer = DimletClientHelper.DimletWithInfo.Serializer.class)
    public static final ListCommand<?, ?> CMD_GETDIMLETS = ListCommand.<WorkbenchTileEntity, DimletClientHelper.DimletWithInfo>create("rftoolsdim.workbench.getdimlets",
            (te, player, params) -> te.getDimlets(),
            (te, player, params, list) -> DimletClientHelper.setDimletsOnGui(list));
}
