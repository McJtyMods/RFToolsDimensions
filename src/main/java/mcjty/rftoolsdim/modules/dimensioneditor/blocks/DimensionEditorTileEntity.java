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
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsUtilityCompat;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorConfig;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;
import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;

public class DimensionEditorTileEntity extends GenericTileEntity implements ITickableTileEntity {

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
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dimension Editor")
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


    public DimensionEditorTileEntity() {
        super(DimensionEditorModule.TYPE_DIMENSION_EDITOR.get());
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
            protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(OPERATIONTYPE);
            }
        };
    }

    private static boolean isValidInput(net.minecraft.item.ItemStack s) {
        return DimletItem.isReadyDimlet(s) ||
                s.getItem() == Items.TNT ||
                "rftoolsutility:matter_receiver".equals(s.getItem().getRegistryName().toString());
    }


    public int getEditPercentage() {
        return editPercentage;
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }

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
                    DimensionData data = PersistantDimensionManager.get(level).getData(id);

                    if (isMatterReceiver(injectableItemStack)) {
                        ServerWorld dimWorld = LevelTools.getLevel(level, LevelTools.getId(id));
                        int y = findGoodReceiverLocation(dimWorld);
                        if (y == -1) {
                            y = dimWorld.getHeight() / 2;
                        }
                        Item item = injectableItemStack.getItem();
                        if (item instanceof BlockItem) {
                            BlockItem itemBlock = (BlockItem) item;
                            BlockState state = itemBlock.getBlock().defaultBlockState();
                            BlockPos pos = new BlockPos(8, y, 8);
                            dimWorld.setBlock(pos, state, BLOCK_UPDATE);
                            Block block = dimWorld.getBlockState(pos).getBlock();
                            // @@@@@@@@@@@@@@ check if right?
                            String name = NBTTools.getInfoNBT(injectableItemStack, CompoundNBT::getString, "tpName", "");
                            long energy = NBTTools.getBlockEntityNBT(injectableItemStack, CompoundNBT::getLong, "Energy", 0L);
                            RFToolsUtilityCompat.createTeleporter(dimWorld, pos, name, (int) energy);
                            block.setPlacedBy(dimWorld, pos, state, null, injectableItemStack);
//                            block.onBlockActivated(dimWorld, pos, state, FakePlayerFactory.getMinecraft((WorldServer) dimWorld), EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
//                            block.onBlockPlacedBy(dimWorld, pos, state, null, injectableItemStack);
                            dimWorld.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), BLOCK_UPDATE);
                            dimWorld.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), BLOCK_UPDATE);

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
//        World w = DimensionManager.getWorld(id);
//        if (w != null) {
//            // Dimension is still loaded. Do nothing.
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "Dimension cannot be deleted. It is still in use!", 10);
//            return;
//        }
//        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(getWorld());
//        DimensionInformation information = dimensionManager.getDimensionInformation(id);
//        if (information.getOwner() == null) {
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "You cannot delete a dimension without an owner!", 10);
//            return;
//        }
//        if (getOwnerUUID() == null) {
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "This machine has no proper owner and cannot delete dimensions!", 10);
//            return;
//        }
//        if (!getOwnerUUID().equals(information.getOwner())) {
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "This machine's owner differs from the dimensions owner!", 10);
//            return;
//        }
//
//        RFToolsDim.teleportationManager.removeReceiverDestinations(getWorld(), id);
//
//        dimensionManager.removeDimension(id);
//        dimensionManager.reclaimId(id);
//        dimensionManager.save(getWorld());
//
//        DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(getWorld());
//        dimensionStorage.removeDimension(id);
//        dimensionStorage.save();
//
//        if (GeneralConfiguration.dimensionFolderIsDeletedWithSafeDel) {
//            File rootDirectory = DimensionManager.getCurrentSaveRootDirectory();
//            try {
//                FileUtils.deleteDirectory(new File(rootDirectory.getPath() + File.separator + "RFTOOLS" + id));
//                Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "Dimension deleted and dimension folder succesfully wiped!", 10);
//            } catch (IOException e) {
//                Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "Dimension deleted but dimension folder could not be completely wiped!", 10);
//            }
//        } else {
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "Dimension deleted. Please remove the dimension folder from disk!", 10);
//        }
//
//        dimensionTab.getTagCompound().removeTag("id");
//        int tickCost = dimensionTab.getTagCompound().getInteger("tickCost");
//        dimensionTab.getTagCompound().setInteger("ticksLeft", tickCost);
    }

    private int findGoodReceiverLocation(World dimWorld) {
        int y = findSuitableEmptySpot(dimWorld, 8, 8);
        y++;
        return y;
    }

    public static int findSuitableEmptySpot(World world, int x, int z) {
        int y = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
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
        DimletType type = key.getType();
        // @todo 1.16
//        if (itype.isInjectable(key)) {
//            return itemStack;
//        } else {
//            return ItemStack.EMPTY;
//        }
        return ItemStack.EMPTY;
    }

    private ItemStack canDeleteDimension(ItemStack itemStack) {
//        if (!GeneralConfiguration.playersCanDeleteDimensions) {
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "Players cannot delete dimensions!", 10);
//            return ItemStack.EMPTY;
//        }
//        if (!GeneralConfiguration.editorCanDeleteDimensions) {
//            Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "Dimension deletion with the editor is not enabled!", 10);
//            return ItemStack.EMPTY;
//        }
//        ItemStack dimensionStack = inventoryHelper.getStackInSlot(DimensionEditorContainer.SLOT_DIMENSIONTARGET);
//        if (dimensionStack.isEmpty()) {
//            return ItemStack.EMPTY;
//        }
//
//        NBTTagCompound tagCompound = dimensionStack.getTagCompound();
//        int id = tagCompound.getInteger("id");
//        if (id == 0) {
//            return ItemStack.EMPTY;
//        }
//        DimensionInformation information = RfToolsDimensionManager.getDimensionManager(getWorld()).getDimensionInformation(id);
//
//        if (getOwnerUUID() != null && getOwnerUUID().equals(information.getOwner())) {
//            return itemStack;
//        }
//
//        Broadcaster.broadcast(getWorld(), pos.getX(), pos.getY(), pos.getZ(), "This machine's owner differs from the dimensions owner!", 10);
//        return ItemStack.EMPTY;
        return ItemStack.EMPTY;
    }

    private boolean isMatterReceiver(ItemStack itemStack) {
        return "rftoolsutility:matter_receiver".equals(itemStack.getItem().getRegistryName().toString());
    }

    private boolean isTNT(ItemStack itemStack) {
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
            level.setBlock(worldPosition, getBlockState().setValue(OPERATIONTYPE, DimensionBuilderTileEntity.OperationType.values()[state]), Constants.BlockFlags.DEFAULT_AND_RERENDER);
            setChanged();
        }
    }


}
