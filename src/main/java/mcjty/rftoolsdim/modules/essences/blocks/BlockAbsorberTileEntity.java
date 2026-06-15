package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.FakePlayerGetter;
import mcjty.lib.varia.SoundTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.essences.data.BlockFluidAbsorberData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.component.DataComponentMap;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BlockAbsorberTileEntity extends TickingTileEntity {

    private static final int ABSORB_SPEED = 2;

    private int timer = ABSORB_SPEED;
    private final Set<BlockPos> toscan = new HashSet<>();

    private final FakePlayerGetter harvester = new FakePlayerGetter(this, "rftools_absorber");

    public BlockAbsorberTileEntity(BlockPos pos, BlockState state) {
        super(EssencesModule.TYPE_BLOCK_ABSORBER.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .tileEntitySupplier(BlockAbsorberTileEntity::new)
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/absorbers"))
                .topDriver(RFToolsDimensionsTOPDriver.DRIVER)
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(),
                        parameter("block", BlockAbsorberTileEntity::getBlockName),
                        parameter("progress", BlockAbsorberTileEntity::getProgressName)
                        )) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    private static String getBlockName(ItemStack stack) {
        BlockFluidAbsorberData data = stack.get(EssencesModule.ITEM_BLOCKFLUID_ABSORBER_DATA);
        if (data == null || data.block() == null) {
            return "<Not Set>";
        } else {
            return I18n.get(data.block().toLanguageKey(Registries.BLOCK.location().getPath()).replace('/', '.'));
        }
    }

    public static ResourceLocation getBlock(ItemStack stack) {
        BlockFluidAbsorberData data = stack.getOrDefault(EssencesModule.ITEM_BLOCKFLUID_ABSORBER_DATA, BlockFluidAbsorberData.DEFAULT);
        return data.block();
    }

    private static String getProgressName(ItemStack stack) {
        BlockFluidAbsorberData data = stack.get(EssencesModule.ITEM_BLOCKFLUID_ABSORBER_DATA);
        if  (data == null) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxBlockAbsorption.get() - data.absorbing()) * 100) / EssencesConfig.maxBlockAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        BlockFluidAbsorberData data = stack.get(EssencesModule.ITEM_BLOCKFLUID_ABSORBER_DATA);
        if (data == null) {
            return -1;
        } else {
            return ((EssencesConfig.maxBlockAbsorption.get() - data.absorbing()) * 100) / EssencesConfig.maxBlockAbsorption.get();
        }
    }

    @Override
    protected void tickServer() {
        BlockFluidAbsorberData data = getData(EssencesModule.BLOCKFLUID_ABSORBER_DATA);
        ResourceLocation blockId = data.block();
        int absorbing = data.absorbing();
        if (absorbing > 0 || blockId == null) {
            timer--;
            if (timer <= 0) {
                timer = ABSORB_SPEED;
                BlockState b = isValidSourceBlock(getBlockPos().below());
                if (b != null) {
                    if (blockId == null) {
                        absorbing = EssencesConfig.maxBlockAbsorption.get();
                        if (b.getBlock().asItem() != Items.AIR) {
                            // Safety
                            blockId = Tools.getId(b.getBlock());
                            toscan.clear();
                        }
                    }
                    toscan.add(getBlockPos().below());
                }

                if (!toscan.isEmpty()) {
                    int r = level.random.nextInt(toscan.size());
                    Iterator<BlockPos> iterator = toscan.iterator();
                    BlockPos c = null;
                    for (int i = 0 ; i <= r ; i++) {
                        c = iterator.next();
                    }
                    toscan.remove(c);
                    checkBlock(c, Direction.DOWN);
                    checkBlock(c, Direction.UP);
                    checkBlock(c, Direction.EAST);
                    checkBlock(c, Direction.WEST);
                    checkBlock(c, Direction.SOUTH);
                    checkBlock(c, Direction.NORTH);

                    if (blockMatches(c)) {
                        BlockState oldState = level.getBlockState(c);
                        SoundTools.playSound(level, oldState.getSoundType(level, c, null).getBreakSound(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 1.0f, 1.0f);
                        level.setBlockAndUpdate(c, Blocks.AIR.defaultBlockState());
                        absorbing--;
                        BlockState newState = level.getBlockState(c);
                        level.sendBlockUpdated(c, oldState, newState, Block.UPDATE_ALL);
                    }
                }
            }
            setData(EssencesModule.BLOCKFLUID_ABSORBER_DATA, new BlockFluidAbsorberData(blockId, absorbing));
        }
    }

    @Override
    protected void tickClient() {
        BlockFluidAbsorberData data = getData(EssencesModule.BLOCKFLUID_ABSORBER_DATA);
        if (data.absorbing() > 0) {
            RandomSource rand = level.random;

            double u = rand.nextFloat() * 2.0f - 1.0f;
            double v = (float) (rand.nextFloat() * 2.0f * Math.PI);
            double x = Math.sqrt(1 - u * u) * Math.cos(v);
            double y = Math.sqrt(1 - u * u) * Math.sin(v);
            double z = u;
            double r = 1.0f;

            level.addParticle(ParticleTypes.PORTAL, getBlockPos().getX() + 0.5f + x * r, getBlockPos().getY() + 0.5f + y * r, getBlockPos().getZ() + 0.5f + z * r, -x, -y, -z);
        }
    }

    private void checkBlock(BlockPos c, Direction direction) {
        BlockPos c2 = c.relative(direction);
        if (blockMatches(c2)) {
            toscan.add(c2);
        }
    }

    private boolean blockMatches(BlockPos c) {
        BlockFluidAbsorberData data = getData(EssencesModule.BLOCKFLUID_ABSORBER_DATA);
        return Objects.equals(Tools.getId(level.getBlockState(c).getBlock()), data.block());
    }

    public int getAbsorbing() {
        BlockFluidAbsorberData data = getData(EssencesModule.BLOCKFLUID_ABSORBER_DATA);
        return data.absorbing();
    }

    @Nullable
    public ResourceLocation getAbsorbingBlock() {
        BlockFluidAbsorberData data = getData(EssencesModule.BLOCKFLUID_ABSORBER_DATA);
        return data.block();
    }

    public static boolean allowedToBreak(BlockState state, Level world, BlockPos pos, Player player) {
        float speed = state.getDestroySpeed(world, pos);
        if (speed < 0) {
            return false;
        }
        if (state.getDestroySpeed(world, pos) < 0) {
            return false;
        }
        if (!state.getBlock().canEntityDestroy(state, world, pos, player)) {
            return false;
        }
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    private BlockState isValidSourceBlock(BlockPos coordinate) {
        BlockState state = level.getBlockState(coordinate);
        if (!allowedToBreak(state, level, coordinate, harvester.get())) {
            return null;
        }
        return isValidDimletBlock(state) ? state : null;
    }

    private boolean isValidDimletBlock(BlockState state) {
        Block block = state.getBlock();
        DimletKey key = new DimletKey(DimletType.BLOCK, Tools.getId(block).toString());
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        return settings != null && settings.isDimlet();
    }

    @Override
    public void loadAdditional(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        int[] x = tagCompound.getIntArray("toscanx");
        int[] y = tagCompound.getIntArray("toscany");
        int[] z = tagCompound.getIntArray("toscanz");
        toscan.clear();
        for (int i = 0 ; i < x.length ; i++) {
            toscan.add(new BlockPos(x[i], y[i], z[i]));
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.saveAdditional(tagCompound, provider);
        int[] x = new int[toscan.size()];
        int[] y = new int[toscan.size()];
        int[] z = new int[toscan.size()];
        int i = 0;
        for (BlockPos c : toscan) {
            x[i] = c.getX();
            y[i] = c.getY();
            z[i] = c.getZ();
            i++;
        }
        tagCompound.putIntArray("toscanx", x);
        tagCompound.putIntArray("toscany", y);
        tagCompound.putIntArray("toscanz", z);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        BlockFluidAbsorberData data = input.get(EssencesModule.ITEM_BLOCKFLUID_ABSORBER_DATA);
        if (data != null) {
            setData(EssencesModule.BLOCKFLUID_ABSORBER_DATA, data);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        BlockFluidAbsorberData data = getData(EssencesModule.BLOCKFLUID_ABSORBER_DATA);
        builder.set(EssencesModule.ITEM_BLOCKFLUID_ABSORBER_DATA.get(), data);
    }
}
