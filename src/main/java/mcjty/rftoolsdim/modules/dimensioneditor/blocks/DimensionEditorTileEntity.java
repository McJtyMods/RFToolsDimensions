package mcjty.rftoolsdim.modules.dimensioneditor.blocks;

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
import mcjty.lib.varia.Broadcaster;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.tools.DynamicDimensionManager;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorConfig;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class DimensionEditorTileEntity extends TickingTileEntity {

    public static final EnumProperty<DimensionBuilderTileEntity.OperationType> OPERATIONTYPE = EnumProperty.create("operationtype", DimensionBuilderTileEntity.OperationType.class);

    public static final int SLOT_INJECTINPUT = 0;
    public static final int SLOT_DIMENSIONTARGET = 1;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(specific(DimensionEditorTileEntity::isValidInput).in().out(), SLOT_INJECTINPUT, 64, 24)
            .slot(specific(DimensionBuilderTileEntity::isRealizedTab).in().out(), SLOT_DIMENSIONTARGET, 118, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> {
                if (slot == SLOT_DIMENSIONTARGET) {
                    return DimensionBuilderTileEntity.isRealizedTab(stack);
                } else {
                    return isValidInput(stack);
                }
            })
            .build();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, DimensionEditorConfig.EDITOR_MAXENERGY.get(), DimensionEditorConfig.EDITOR_RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimension Editor")
            .containerSupplier(container(DimensionEditorModule.CONTAINER_DIMENSION_EDITOR, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(DimensionEditorTileEntity.this);

    @GuiValue
    private int editPercentage = 0;

    private int ticksLeft = -1;
    private int ticksCost = -1;
    private int rfPerTick = -1;
    private int state = 0;          // For front state

    public DimensionEditorTileEntity(BlockPos pos, BlockState state) {
        super(DimensionEditorModule.TYPE_DIMENSION_EDITOR.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(DimensionEditorTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimensions/dimension_editor"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.ROTATION;
            }

            @Override
            protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(OPERATIONTYPE);
            }
        };
    }

    private static boolean isValidInput(ItemStack s) {
        if (isMatterReceiver(s)) {
            return true;
        }
        if (DimensionEditorConfig.TNT_CAN_DESTROY_DIMENSION.get() && isTNT(s)) {
            return true;
        }
        // @todo 1.16 the other items are currently not implemented yet
        return false;
//        return DimletItem.isReadyDimlet(s) ||
//                s.getItem() == Items.TNT;
    }


    public int getEditPercentage() {
        return editPercentage;
    }

    @Override
    public void tickServer() {
        if (ticksLeft == -1) {
            editPercentage = 0;
        } else {
            editPercentage = (ticksCost - ticksLeft) * 100 / ticksCost;
        }

        ItemStack injectableItemStack = validateInjectableItemStack();
        if (injectableItemStack.isEmpty()) {
            return;
        }

        ItemStack dimensionItemStack = validateDimensionItemStack();
        if (dimensionItemStack.isEmpty()) {
            return;
        }

        if (ticksLeft == -1) {
            // We were not injecting. Start now.
            String dimension = dimensionItemStack.getOrCreateTag().getString("dimension");
            ResourceLocation id = new ResourceLocation(dimension);
            DimensionData data = PersistantDimensionManager.get(level).getData(id);

            if (false) { // @todo 1.16 dimensionManager.getDimensionInformation(id).isCheater()) {
                ticksCost = 1;
                rfPerTick = 0;
            } else if (isMatterReceiver(injectableItemStack)) {
                ticksCost = 1000; // @todo 1.16 DimletCosts.baseDimensionTickCost + 1000;
                rfPerTick = 200; // @todo 1.16 DimletCosts.baseDimensionCreationCost + 200;
            } else if (isTNT(injectableItemStack)) {
                ticksCost = 600;
                rfPerTick = 10;
            } else {
                DimletKey key = DimletTools.getDimletKey(injectableItemStack);
                DimletSettings settings = DimletDictionary.get().getSettings(key);
                if (false) { // @todo 1.16 DimletObjectMapping.getSpecial(key) == SpecialType.SPECIAL_CHEATER) {
                    ticksCost = 1;
                    rfPerTick = 0;
                } else {
                    ticksCost = 1000 + settings.getTickCost(); // @todo 1.16 DimletCosts.baseDimensionTickCost + settings.getTickCost();
                    rfPerTick = 200 + settings.getCreateCost(); // @todo 1.16 DimletCosts.baseDimensionCreationCost + settings.getCreateCost();
                }
            }
            ticksLeft = ticksCost;
        } else {
            long rf = energyStorage.getEnergyStored();
            int rfpt = rfPerTick;
            rfpt = (int) (rfpt * (2.0f - infusable.getInfusedFactor()) / 2.0f);

            if (rf >= rfpt) {
                // Enough energy.
                energyStorage.consumeEnergy(rfpt);

                ticksLeft--;
                if (ticksLeft <= 0) {
                    String dimension = dimensionItemStack.getOrCreateTag().getString("dimension");
                    ResourceLocation id = new ResourceLocation(dimension);

                    if (isMatterReceiver(injectableItemStack)) {
                        ServerLevel dimWorld = LevelTools.getLevel(level, LevelTools.getId(id));
                        int y = findGoodReceiverLocation(dimWorld);
                        if (y == -1) {
                            y = dimWorld.getHeight() / 2;
                        }
                        Item item = injectableItemStack.getItem();
                        if (item instanceof BlockItem) {
                            BlockItem itemBlock = (BlockItem) item;
                            BlockState state = itemBlock.getBlock().defaultBlockState();
                            BlockPos pos = new BlockPos(8, y, 8);
                            dimWorld.setBlock(pos, state, Block.UPDATE_NEIGHBORS);
                            Block block = dimWorld.getBlockState(pos).getBlock();
                            // @@@@@@@@@@@@@@ check if right?
                            String name = NBTTools.getInfoNBT(injectableItemStack, CompoundTag::getString, "tpName", "");
                            long energy = NBTTools.getBlockEntityNBT(injectableItemStack, CompoundTag::getLong, "Energy", 0L);
                            RFToolsUtilityCompat.createTeleporter(dimWorld, pos, name, (int) energy);
                            block.setPlacedBy(dimWorld, pos, state, null, injectableItemStack);
//                            block.onBlockActivated(dimWorld, pos, state, FakePlayerFactory.getMinecraft((WorldServer) dimWorld), EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
//                            block.onBlockPlacedBy(dimWorld, pos, state, null, injectableItemStack);
                            dimWorld.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_NEIGHBORS);
                            dimWorld.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_NEIGHBORS);

                        }
                    } else if (isTNT(injectableItemStack)) {
                        safeDeleteDimension(id, dimensionItemStack);
                    } else {
                        DimletKey key = DimletTools.getDimletKey(injectableItemStack);
                        // @todo 1.16
//
//                        DimensionInformation information = dimensionManager.getDimensionInformation(id);
//                        information.injectDimlet(key);
//                        dimensionManager.save(getWorld());
                    }

                    items.decrStackSize(SLOT_INJECTINPUT, 1);

                    stopInjecting();
                }
            }
        }
        setChanged();

        setState();

    }

    private void safeDeleteDimension(ResourceLocation id, ItemStack dimensionTab) {
        DimensionData data = PersistantDimensionManager.get(level).getData(id);
        if (data == null) {
            return;
        }
        ServerLevel dimension = LevelTools.getLevel(this.level, id);
        int chunks = dimension.getChunkSource().chunkMap.size();
        if (chunks > 0) {
            // Dimension is still loaded. Do nothing.
            Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "Dimension cannot be deleted. It is still in use!", 10);
            return;
        }
        if (data.getOwner() != null) {
            if (getOwnerUUID() == null) {
                Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "This machine has no proper owner and cannot delete dimensions!", 10);
                return;
            }
            if (!getOwnerUUID().equals(data.getOwner())) {
                Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "This machine's owner differs from the dimensions owner!", 10);
                return;
            }
            RFToolsUtilityCompat.teleportationManager.removeReceiverDestinations(level, LevelTools.getId(id));
            PersistantDimensionManager mgr = PersistantDimensionManager.get(level);
            mgr.forget(id);
            DynamicDimensionManager.markDimensionForUnregistration(level.getServer(), LevelTools.getId(id));
            Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "Removed dimension '" + id.toString() + "'!", 10);

            dimensionTab.getTag().remove("dimension");
            int tickCost = dimensionTab.getTag().getInt("tickCost");
            dimensionTab.getTag().putInt("ticksLeft", tickCost);
        }
    }

    private int findGoodReceiverLocation(Level dimWorld) {
        int y = findSuitableEmptySpot(dimWorld, 8, 8);
        y++;
        return y;
    }

    public static int findSuitableEmptySpot(Level world, int x, int z) {
        int y = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        if (y == -1) {
            return -1;
        }
        if (y == 0) {
            y = 65;
        }

        y--;            // y should now be at a solid or liquid block.

        if (y > world.getHeight() - 5) {
            y = world.getHeight() / 2;
        }


        BlockState state = world.getBlockState(new BlockPos(x, y + 1, z));
        while (state.getMaterial().isLiquid()) {
            y++;
            if (y > world.getHeight() - 10) {
                return -1;
            }
            state = world.getBlockState(new BlockPos(x, y + 1, z));
        }

        return y;
    }

    private ItemStack validateInjectableItemStack() {
        ItemStack itemStack = items.getStackInSlot(SLOT_INJECTINPUT);
        if (itemStack.isEmpty()) {
            stopInjecting();
            return ItemStack.EMPTY;
        }

        if (isMatterReceiver(itemStack)) {
            return itemStack;
        }
        if (isTNT(itemStack)) {
            return canDeleteDimension(itemStack);
        }

        DimletKey key = DimletTools.getDimletKey(itemStack);
        DimletType type = key.type();
        // @todo 1.16
//        if (itype.isInjectable(key)) {
//            return itemStack;
//        } else {
//            return ItemStack.EMPTY;
//        }
        return ItemStack.EMPTY;
    }

    private ItemStack canDeleteDimension(ItemStack itemStack) {
        if (!DimensionEditorConfig.TNT_CAN_DESTROY_DIMENSION.get()) {
            Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "Players cannot delete dimensions!", 10);
            return ItemStack.EMPTY;
        }
        ItemStack dimensionStack = items.getStackInSlot(SLOT_DIMENSIONTARGET);
        if (dimensionStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        String dimension = dimensionStack.getOrCreateTag().getString("dimension");
        ResourceLocation id = new ResourceLocation(dimension);
        DimensionData data = PersistantDimensionManager.get(level).getData(id);

        if (data == null) {
            Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "Not a valid dimension!", 10);
            return ItemStack.EMPTY;
        }
        if (getOwnerUUID() != null && getOwnerUUID().equals(data.getOwner())) {
            return itemStack;
        }

        Broadcaster.broadcast(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "This machine's owner differs from the dimensions owner!", 10);
        return ItemStack.EMPTY;
    }

    private static boolean isMatterReceiver(ItemStack itemStack) {
        return "rftoolsutility:matter_receiver".equals(Tools.getId(itemStack).toString());
    }

    private static boolean isTNT(ItemStack itemStack) {
        return itemStack.getItem() == Items.TNT;
    }

    private ItemStack validateDimensionItemStack() {
        ItemStack itemStack = items.getStackInSlot(SLOT_DIMENSIONTARGET);
        if (itemStack.isEmpty()) {
            stopInjecting();
            return ItemStack.EMPTY;
        }

        String dimension = itemStack.getOrCreateTag().getString("dimension");
        ResourceLocation id = new ResourceLocation(dimension);
        DimensionData data = PersistantDimensionManager.get(level).getData(id);
        if (data == null) {
            // Not a valid dimension.
            stopInjecting();
            return ItemStack.EMPTY;
        }

        return itemStack;
    }

    private void stopInjecting() {
        setState();
        ticksLeft = -1;
        ticksCost = -1;
        rfPerTick = -1;
        setChanged();
    }

//    public DimensionEditorBlock.OperationType getState() {
//        return DimensionEditorBlock.OperationType.values()[state];
//    }

    private void setState() {
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
            level.setBlock(worldPosition, getBlockState().setValue(OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.values()[state]), Block.UPDATE_ALL);
            setChanged();
        }
    }


}
