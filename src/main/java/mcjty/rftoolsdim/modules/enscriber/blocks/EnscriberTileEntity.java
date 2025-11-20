package mcjty.rftoolsdim.modules.enscriber.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.DimensionCreator;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.data.RealizedTabData;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class EnscriberTileEntity extends GenericTileEntity {

    public static final int SLOT_DIMLETS = 0;
    public static final int SIZE_DIMLETS = 13 * 7;
    public static final int SLOT_TAB = SLOT_DIMLETS + SIZE_DIMLETS;

    private DescriptorError error = DescriptorError.OK;
    private int clientErrorCode = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(SIZE_DIMLETS + 1)
            .box(specific(DimletItem::isReadyDimlet), SLOT_DIMLETS, 13, 7, 13, 7)
            .slot(specific(EnscriberTileEntity::isDimensionTab), SLOT_TAB, 13, 142)
            .playerSlots(85, 142));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .slotLimit(1)
            .itemValid((slot, stack) -> {
                if (slot == SLOT_TAB) {
                    return EnscriberTileEntity.isDimensionTab(stack);
                }
                return stack.getItem() instanceof DimletItem;
            })
            .onUpdate((slot, stack) -> validateDimlets())
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<EnscriberTileEntity, GenericItemHandler> ITEM_CAP = be -> be.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<EnscriberTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Enscriber")
            .containerSupplier(container(EnscriberModule.CONTAINER_ENSCRIBER, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .shortListener(Sync.integer(() -> be.error.getCode().ordinal(), v -> be.clientErrorCode = v))
            .setupSync(be);

    public EnscriberTileEntity(BlockPos pos, BlockState state) {
        super(EnscriberModule.TYPE_ENSCRIBER.get(), pos, state);
    }

    private static boolean isDimensionTab(ItemStack s) {
        return s.getItem() == DimensionBuilderModule.EMPTY_DIMENSION_TAB.get() || s.getItem() == DimensionBuilderModule.REALIZED_DIMENSION_TAB.get();
    }


    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(EnscriberTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimensions/enscriber"))
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
            RealizedTabData tab = stack.get(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA);
            if (tab != null) {
                return tab.name().orElse(null);
            }
        }
        return null;
    }

    private boolean checkOwnerDimlet() {
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stack = items.getStackInSlot(i);
            DimletKey dimletKey = DimletTools.getDimletKey(stack);
            if (DimletTools.isOwnerDimlet(dimletKey)) {
                return true;
            }
        }
        return false;
    }

    private void storeDimlets(Player player, String name) {
        if (DimensionConfig.OWNER_DIMLET_REQUIRED.get()) {
            if (checkOwnerDimlet()) {
                Logging.warn(player, "You need an owner dimlet to make a dimension!");
                return;
            }
        }

        DimensionDescriptor descriptor = convertToDimensionDescriptor(player);
        ItemStack realizedTab = createRealizedTab(descriptor);

        DimensionData data = PersistantDimensionManager.get(level).getData(descriptor);
        if (data != null) {
            name = data.getId().getPath();
            player.displayClientMessage(ComponentFactory.literal("This dimension already existed! If this is what you wanted then that's fine. Otherwise you need digit dimlets to make new unique dimensions. The dimlet sequence uniquely identifies a dimension. Names can't be changed")
                    .withStyle(ChatFormatting.YELLOW), false);
        } else {
            if (!DimensionCreator.get().isNameAvailable(level, null, name)) {
                player.displayClientMessage(ComponentFactory.literal("This name is already used by another dimension!")
                        .withStyle(ChatFormatting.YELLOW), false);
                return;
            }
        }

        // Clear out the dimlets
        for (int i = 0; i < SIZE_DIMLETS; i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }

        RealizedTabData tab = realizedTab.getOrDefault(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA,  RealizedTabData.DEFAULT);
        realizedTab.set(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA, tab.withName(name));
        items.setStackInSlot(SLOT_TAB, realizedTab);

        setChanged();
    }

    /**
     * Create a realized dimension tab by taking a map of ids per type and storing
     * that in the NBT of the realized dimension tab.
     */
    private ItemStack createRealizedTab(DimensionDescriptor descriptor) {
        ItemStack realizedTab = new ItemStack(DimensionBuilderModule.REALIZED_DIMENSION_TAB.get(), 1);
        RealizedTabData tab = realizedTab.getOrDefault(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA,  RealizedTabData.DEFAULT);
        String compact = descriptor.compact();
        tab = tab.withDescriptor(compact);

        PersistantDimensionManager mgr = PersistantDimensionManager.get(level);
        DimensionData data = mgr.getData(descriptor);

        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();

        if (data != null) {
            // The dimension was already created.
            tab = tab.withTicksLeft(0);
            tab = tab.withDimension(data.getId());
            try {
                compiledDescriptor.compile(descriptor, data.getRandomizedDescriptor());
            } catch (DescriptorError ignore) {
            }
        } else {
            try {
                compiledDescriptor.compile(descriptor, DimensionDescriptor.EMPTY);  // Randomized part not known yet
            } catch (DescriptorError ignore) {
            }
            tab = tab.withTicksLeft(compiledDescriptor.getActualTickCost());
        }

        tab = tab.withTickCost(compiledDescriptor.getActualTickCost());
        tab = tab.withRfCreateCost(compiledDescriptor.getCreateCostPerTick());
        tab = tab.withRfMaintainCost(compiledDescriptor.getActualPowerCost());
        realizedTab.set(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA, tab);

        return realizedTab;
    }


    /**
     * Convert the dimlets in the inventory to a dimension descriptor.
     */
    private DimensionDescriptor convertToDimensionDescriptor(@Nullable Player player) {
        DimensionDescriptor descriptor = new DimensionDescriptor();
        List<DimletKey> dimlets = descriptor.getDimlets();

        long forcedSeed = 0;

        for (int i = 0; i < SIZE_DIMLETS; i++) {
            ItemStack stack = items.getStackInSlot(i + SLOT_DIMLETS);
            if (!stack.isEmpty()) {
                DimletKey key = DimletTools.getDimletKey(stack);
                DimletSettings settings = DimletDictionary.get().getSettings(key);
                if (settings != null) {
                    // Make sure the dimlet is not blacklisted.
                    dimlets.add(key);
                    CompoundTag tagCompound = null; // @todo 1.21 data stack.getTag();
                    // @todo 1.16 is this the way?
                    if (tagCompound != null && tagCompound.getLong("forcedSeed") != 0) {
                        forcedSeed = tagCompound.getLong("forcedSeed");
                    }
                } else {
                    if (player != null) {
                        Logging.warn(player, "Dimlet " + key.type().name() + "." + key.key() + " was not included in the tab because it is blacklisted");
                    }
                }
            }
        }
        return descriptor;
    }

    private void validateDimlets() {
        DimensionDescriptor descriptor = convertToDimensionDescriptor(null);
        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();
        try {
            compiledDescriptor.compile(descriptor, DimensionDescriptor.EMPTY);  // We just need to check the descriptor. Not randomized
            error = DescriptorError.OK;
        } catch (DescriptorError e) {
            error = e;
        }
    }

    public int getClientErrorCode() {
        return clientErrorCode;
    }

    private void extractDimlets() {
        ItemStack realizedTab = items.getStackInSlot(SLOT_TAB);
        RealizedTabData tab = realizedTab.get(DimensionBuilderModule.ITEM_REALIZED_TAB_DATA);
        if (tab != null) {
//            long forcedSeed = tab.forcedSeed();
            String descString = tab.descriptor();
            DimensionDescriptor descriptor = new DimensionDescriptor();
            descriptor.read(descString);
            List<DimletKey> dimlets = descriptor.getDimlets();
            int idx = SLOT_DIMLETS;
            for (DimletKey key : dimlets) {
                boolean hasDimension = tab.dimension().isPresent();
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
        setChanged();
    }

    public static final Key<String> PARAM_NAME = new Key<>("name", Type.STRING);
    @ServerCommand
    public static final Command<?> CMD_STORE = Command.<EnscriberTileEntity>create("enscriber.store",
            (te, player, params) -> te.storeDimlets(player, params.get(PARAM_NAME)));

    @ServerCommand
    public static final Command<?> CMD_EXTRACT = Command.<EnscriberTileEntity>create("enscriber.extract",
            (te, player, params) -> te.extractDimlets());


}
