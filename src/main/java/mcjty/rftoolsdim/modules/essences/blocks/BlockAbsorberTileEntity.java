package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.FakePlayerGetter;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.SoundTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsdim.compat.RFToolsDimensionsTOPDriver;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import mcjty.rftoolsdim.modules.essences.EssencesConfig;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.block.AbstractBlock;

import javax.annotation.Nonnull;

public class BlockAbsorberTileEntity extends GenericTileEntity implements ITickableTileEntity {

    private static final int ABSORB_SPEED = 2;

    private int absorbing = 0;
    private Block absorbingBlock = null;
    private int timer = ABSORB_SPEED;
    private final Set<BlockPos> toscan = new HashSet<>();

    private final FakePlayerGetter harvester = new FakePlayerGetter(this, "rftools_absorber");

    public BlockAbsorberTileEntity() {
        super(EssencesModule.TYPE_BLOCK_ABSORBER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(AbstractBlock.Properties.of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .tileEntitySupplier(BlockAbsorberTileEntity::new)
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/dimlet_workbench"))
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
        String block = NBTTools.getInfoNBT(stack, CompoundNBT::getString, "block", null);
        if (block == null) {
            return "<Not Set>";
        } else {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(block));
            if (b != null) {
                return I18n.get(b.getDescriptionId());
            } else {
                return "<Invalid>";
            }
        }
    }

    public static String getBlock(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundNBT::getString, "block", null);
    }

    private static String getProgressName(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxBlockAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBlockAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return -1;
        } else {
            return ((EssencesConfig.maxBlockAbsorption.get() - absorbing) * 100) / EssencesConfig.maxBlockAbsorption.get();
        }
    }


    @Override
    public void tick() {
        if (level.isClientSide) {
            tickClient();
        } else {
            tickServer();
        }
    }

    private void tickServer() {
        if (absorbing > 0 || absorbingBlock == null) {
            timer--;
            if (timer <= 0) {
                timer = ABSORB_SPEED;
                BlockState b = isValidSourceBlock(getBlockPos().below());
                if (b != null) {
                    if (absorbingBlock == null) {
                        absorbing = EssencesConfig.maxBlockAbsorption.get();
                        if (b.getBlock().asItem() != Items.AIR) {
                            // Safety
                            absorbingBlock = b.getBlock();
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
                        SoundTools.playSound(level, absorbingBlock.getSoundType(oldState, level, c, null).getBreakSound(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 1.0f, 1.0f);
                        level.setBlockAndUpdate(c, Blocks.AIR.defaultBlockState());
                        absorbing--;
                        BlockState newState = level.getBlockState(c);
                        level.sendBlockUpdated(c, oldState, newState, Constants.BlockFlags.DEFAULT);
                    }
                }
            }
            setChanged();
        }
    }

    private void tickClient() {
        if (absorbing > 0) {
            Random rand = level.random;

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
        return level.getBlockState(c).getBlock().equals(absorbingBlock);
    }

    public int getAbsorbing() {
        return absorbing;
    }

    public Block getAbsorbingBlock() {
        return absorbingBlock;
    }

    public static boolean allowedToBreak(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        float speed = state.getDestroySpeed(world, pos);
        if (speed < 0) {
            return false;
        }
        if (!state.canHarvestBlock(world, pos, player)) {
            return false;
        }
        if (!state.getBlock().canEntityDestroy(state, world, pos, player)) {
            return false;
        }
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
        MinecraftForge.EVENT_BUS.post(event);
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
        DimletKey key = new DimletKey(DimletType.BLOCK, block.getRegistryName().toString());
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        return settings != null && settings.isDimlet();
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        int[] x = tagCompound.getIntArray("toscanx");
        int[] y = tagCompound.getIntArray("toscany");
        int[] z = tagCompound.getIntArray("toscanz");
        toscan.clear();
        for (int i = 0 ; i < x.length ; i++) {
            toscan.add(new BlockPos(x[i], y[i], z[i]));
        }
    }

    @Override
    protected void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        if (tagCompound.contains("Info")) {
            CompoundNBT info = tagCompound.getCompound("Info");
            absorbing = info.getInt("absorbing");
            if (info.contains("block")) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(info.getString("block")));
                if (block != null) {
                    absorbingBlock = block;
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        super.save(tagCompound);
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
        return tagCompound;
    }

    @Override
    protected void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("absorbing", absorbing);
        if (absorbingBlock != null) {
            info.putString("block", absorbingBlock.getRegistryName().toString());
        }
    }
}
