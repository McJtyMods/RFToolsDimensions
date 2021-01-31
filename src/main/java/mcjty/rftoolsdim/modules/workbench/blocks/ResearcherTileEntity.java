package mcjty.rftoolsdim.modules.workbench.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeKey;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import mcjty.rftoolsdim.modules.workbench.WorkbenchConfig;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;
import static mcjty.lib.container.SlotDefinition.specific;

public class ResearcherTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static int SLOT_IN = 0;
    public static int SLOT_OUT = 1;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(specific(ResearcherTileEntity::isResearchable), CONTAINER_CONTAINER, SLOT_IN, 64, 24)
            .slot(generic(), CONTAINER_CONTAINER, SLOT_OUT, 118, 24)
            .playerSlots(10, 70));

    private final IInfusable infusable = new DefaultInfusable(ResearcherTileEntity.this);
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> infusable);

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, WorkbenchConfig.RESEARCHER_MAXENERGY.get(),
            WorkbenchConfig.RESEARCHER_ENERGY_INPUT_PERTICK.get());
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Knowledge Holder")
            .containerSupplier((windowId,player) -> new GenericContainer(WorkbenchModule.CONTAINER_RESEARCHER.get(), windowId, CONTAINER_FACTORY.get(), getPos(), ResearcherTileEntity.this))
            .integerListener(new IntReferenceHolder() {
                @Override
                public int get() {
                    return progress;
                }

                @Override
                public void set(int value) {
                    progress = value;
                }
            })
            .energyHandler(() -> energyStorage)
            .itemHandler(() -> items));

    public static VoxelShape SLAB = VoxelShapes.create(0f, 0f, 0f, 1f, 0.5f, 1f);

    private int progress;

    public ResearcherTileEntity() {
        super(WorkbenchModule.TYPE_RESEARCHER.get());
    }

    private static boolean isResearchable(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof LostKnowledgeItem) {
            KnowledgeKey key = LostKnowledgeItem.getKnowledgeKey(stack);
            return key == null;
        } else if (item == EssencesModule.BLOCK_ABSORBER_ITEM.get()) {
            return true;
        } else if (item == EssencesModule.BIOME_ABSORBER_ITEM.get()) {
            return true;
        } else if (DimletItem.isReadyDimlet(stack)) {
            return true;
        }
        return false;
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(ResearcherTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimensionbuilder"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
                return SLAB;
            }
        };
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (!items.getStackInSlot(SLOT_OUT).isEmpty()) {
                return; // Can't do anything
            }
            ItemStack stack = items.getStackInSlot(SLOT_IN);
            if (!stack.isEmpty()) {
                progress--;
                if (progress <= 0) {
                    progress = 0;
                    research();
                    markDirtyClient();
                }
                energyStorage.consumeEnergy((long) (WorkbenchConfig.RESEARCHER_USE_PER_TICK.get() / (1 + infusable.getInfusedFactor() / 3.0f)));
                markDirtyQuick();
            }
        }
    }

    public void research() {
        ItemStack stack = items.getStackInSlot(SLOT_IN);
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof LostKnowledgeItem) {
                researchKnowledge((LostKnowledgeItem) item);
            } else if (item == EssencesModule.BLOCK_ABSORBER_ITEM.get()) {
                researchBlockAbsorber(stack);
            } else if (item == EssencesModule.BIOME_ABSORBER_ITEM.get()) {
                researchBiomeAbsorber(stack);
            } else if (DimletItem.isReadyDimlet(stack)) {
                researchDimlet(stack);
            }
        }
    }

    private void researchDimlet(ItemStack stack) {
        DimletKey key = DimletTools.getDimletKey(stack);
        if (key != null) {
            ItemStack researched = LostKnowledgeItem.createLostKnowledge(world, key);
            items.setStackInSlot(SLOT_OUT, researched);
        }
        items.decrStackSize(SLOT_IN, 1);
    }

    private void researchBiomeAbsorber(ItemStack stack) {
        String biomeId = BiomeAbsorberTileEntity.getBiome(stack);
        if (biomeId != null && !biomeId.isEmpty()) {
            DimletKey key = DimletDictionary.get().getBiomeDimlet(biomeId);
            if (key != null) {
                int absorberProgress = BiomeAbsorberTileEntity.getProgress(stack);
                if (world.getRandom().nextInt(100) < absorberProgress) {
                    ItemStack researched = LostKnowledgeItem.createLostKnowledge(world, key);
                    items.setStackInSlot(SLOT_OUT, researched);
                }
            }
        }
        items.decrStackSize(SLOT_IN, 1);
    }

    private void researchBlockAbsorber(ItemStack stack) {
        String blockId = BlockAbsorberTileEntity.getBlock(stack);
        if (blockId != null && !blockId.isEmpty()) {
            DimletKey key = DimletDictionary.get().getBlockDimlet(blockId);
            if (key != null) {
                int absorberProgress = BlockAbsorberTileEntity.getProgress(stack);
                if (world.getRandom().nextInt(100) < absorberProgress) {
                    ItemStack researched = LostKnowledgeItem.createLostKnowledge(world, key);
                    items.setStackInSlot(SLOT_OUT, researched);
                }
            }
        }
        items.decrStackSize(SLOT_IN, 1);
    }

    private void researchKnowledge(LostKnowledgeItem item) {
        DimletRarity rarity = item.getRarity();
        ItemStack researched = LostKnowledgeItem.createRandomLostKnowledge(world, rarity, world.getRandom());
        items.setStackInSlot(SLOT_OUT, researched);
        if (!items.getStackInSlot(SLOT_IN).isEmpty()) {
            progress = getMaxProgress();
        }
        items.decrStackSize(SLOT_IN, 1);
    }

    public int getProgress() {
        return progress;
    }

    public int getProgressPercentage() {
        int max = getMaxProgress();
        return (max-progress) * 100 / max;
    }

    private int getMaxProgress() {
        int p = WorkbenchConfig.RESEARCH_TIME.get();
        return (int) (p / (1 + infusable.getInfusedFactor()));
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        progress = tagCompound.getInt("progress");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("progress", progress);
        return super.write(tagCompound);
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isResearchable(stack);
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_OUT) {
                    return false;
                }
                return isItemValid(slot, stack);
            }

            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                if (index == SLOT_IN) {
                    progress = getMaxProgress();
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

}
