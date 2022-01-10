package mcjty.rftoolsdim.modules.workbench.blocks;

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
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeKey;
import mcjty.rftoolsdim.modules.knowledge.items.LostKnowledgeItem;
import mcjty.rftoolsdim.modules.workbench.WorkbenchConfig;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.GenericItemHandler.slot;
import static mcjty.lib.container.SlotDefinition.generic;
import static mcjty.lib.container.SlotDefinition.specific;

public class ResearcherTileEntity extends TickingTileEntity {

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    @GuiValue
    private int progress;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(specific(ResearcherTileEntity::isResearchable).in(), SLOT_IN, 64, 24)
            .slot(generic().out(), SLOT_OUT, 118, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(ResearcherTileEntity.this);

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, WorkbenchConfig.RESEARCHER_MAXENERGY.get(),
            WorkbenchConfig.RESEARCHER_ENERGY_INPUT_PERTICK.get());

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> isResearchable(stack))
            .insertable(slot(SLOT_IN))
            .onUpdate((slot, stack) -> {
                if (slot == SLOT_IN) {
                    progress = getMaxProgress();
                }
            })
            .build();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Knowledge Holder")
            .containerSupplier(container(WorkbenchModule.CONTAINER_RESEARCHER, CONTAINER_FACTORY, this))
            .energyHandler(() -> energyStorage)
            .itemHandler(() -> items)
            .setupSync(this));

    public static final VoxelShape SLAB = Shapes.box(0f, 0f, 0f, 1f, 0.5f, 1f);

    public ResearcherTileEntity(BlockPos pos, BlockState state) {
        super(WorkbenchModule.TYPE_RESEARCHER.get(), pos, state);
    }

    private static boolean isResearchable(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof LostKnowledgeItem) {
            KnowledgeKey key = LostKnowledgeItem.getKnowledgeKey(stack);
            return key == null;
        } else if (item == EssencesModule.BLOCK_ABSORBER_ITEM.get()) {
            return true;
        } else if (item == EssencesModule.FLUID_ABSORBER_ITEM.get()) {
            return true;
        } else if (item == EssencesModule.BIOME_ABSORBER_ITEM.get()) {
            return true;
        } else if (DimletItem.isReadyDimlet(stack)) {
            DimletKey dimletKey = DimletTools.getDimletKey(stack);
            if (dimletKey == null) {
                return false;
            }
            return dimletKey.getType().usesKnowledgeSystem();
        }
        return false;
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(ResearcherTileEntity::new)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/researcher"))
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @SuppressWarnings("deprecation")
            @Nonnull
            @Override
            public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
                return SLAB;
            }
        };
    }

    @Override
    protected void tickServer() {
        if (!items.getStackInSlot(SLOT_OUT).isEmpty()) {
            return; // Can't do anything
        }

        long consume = (long) (WorkbenchConfig.RESEARCHER_USE_PER_TICK.get() / (1 + infusable.getInfusedFactor() / 3.0f));
        if (energyStorage.getEnergy() >= consume) {
            ItemStack stack = items.getStackInSlot(SLOT_IN);
            if (!stack.isEmpty()) {
                progress--;
                if (progress <= 0) {
                    progress = 0;
                    research();
                    markDirtyClient();
                }
                energyStorage.consumeEnergy(consume);
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
            } else if (item == EssencesModule.FLUID_ABSORBER_ITEM.get()) {
                researchFluidAbsorber(stack);
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
            ItemStack researched = LostKnowledgeItem.createLostKnowledge(level, key);
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
                if (level.getRandom().nextInt(100) < absorberProgress) {
                    ItemStack researched = LostKnowledgeItem.createLostKnowledge(level, key);
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
                if (level.getRandom().nextInt(100) < absorberProgress) {
                    ItemStack researched = LostKnowledgeItem.createLostKnowledge(level, key);
                    items.setStackInSlot(SLOT_OUT, researched);
                }
            }
        }
        items.decrStackSize(SLOT_IN, 1);
    }

    private void researchFluidAbsorber(ItemStack stack) {
        String fluidId = FluidAbsorberTileEntity.getFluid(stack);
        if (fluidId != null && !fluidId.isEmpty()) {
            DimletKey key = DimletDictionary.get().getFluidDimlet(fluidId);
            if (key != null) {
                int absorberProgress = FluidAbsorberTileEntity.getProgress(stack);
                if (level.getRandom().nextInt(100) < absorberProgress) {
                    ItemStack researched = LostKnowledgeItem.createLostKnowledge(level, key);
                    items.setStackInSlot(SLOT_OUT, researched);
                }
            }
        }
        items.decrStackSize(SLOT_IN, 1);
    }

    private void researchKnowledge(LostKnowledgeItem item) {
        DimletRarity rarity = item.getRarity();
        ItemStack researched = LostKnowledgeItem.createRandomLostKnowledge(level, rarity, level.getRandom());
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
        return (max - progress) * 100 / max;
    }

    private int getMaxProgress() {
        int p = WorkbenchConfig.RESEARCH_TIME.get();
        return (int) (p / (1 + infusable.getInfusedFactor()));
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        progress = tagCompound.getInt("progress");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        tagCompound.putInt("progress", progress);
        super.saveAdditional(tagCompound);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        // Item is required at client side because it is rendered in world
        saveItemHandlerCap(tagCompound);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        loadItemHandlerCap(tagCompound);
    }

}
