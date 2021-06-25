package mcjty.rftoolsdim.modules.essences.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.FluidTools;
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
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.block.AbstractBlock;

public class FluidAbsorberTileEntity extends GenericTileEntity implements ITickableTileEntity {

    private static final int ABSORB_SPEED = 2;

    private int absorbing = 0;
    private Block absorbingBlock = null;
    private int timer = ABSORB_SPEED;
    private final Set<BlockPos> toscan = new HashSet<>();

    public FluidAbsorberTileEntity() {
        super(EssencesModule.TYPE_FLUID_ABSORBER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(AbstractBlock.Properties.of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .tileEntitySupplier(FluidAbsorberTileEntity::new)
                .manualEntry(ManualHelper.create("rftoolsdim:dimlets/dimlet_workbench"))
                .topDriver(RFToolsDimensionsTOPDriver.DRIVER)
                .info(key("message.rftoolsdim.shiftmessage"))
                .infoShift(header(),
                        parameter("fluid", FluidAbsorberTileEntity::getFluidName),
                        parameter("progress", FluidAbsorberTileEntity::getProgressName)
                        )) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    private static String getFluidName(ItemStack stack) {
        String block = NBTTools.getInfoNBT(stack, CompoundNBT::getString, "fluid", null);
        if (block == null) {
            return "<Not Set>";
        } else {
            Fluid b = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(block));
            if (b != null) {
                return I18n.get(b.defaultFluidState().createLegacyBlock().getBlock().getDescriptionId());
            } else {
                return "<Invalid>";
            }
        }
    }

    public static String getFluid(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundNBT::getString, "fluid", null);
    }

    private static String getProgressName(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return "n.a.";
        } else {
            int pct = ((EssencesConfig.maxFluidAbsorption.get() - absorbing) * 100) / EssencesConfig.maxFluidAbsorption.get();
            return pct + "%";
        }
    }

    public static int getProgress(ItemStack stack) {
        int absorbing = NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "absorbing", -1);
        if (absorbing == -1) {
            return -1;
        } else {
            return ((EssencesConfig.maxFluidAbsorption.get() - absorbing) * 100) / EssencesConfig.maxFluidAbsorption.get();
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
                        absorbing = EssencesConfig.maxFluidAbsorption.get();
                        // Safety
                        absorbingBlock = b.getBlock();
                        toscan.clear();
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
                        FluidState oldFluidState = level.getFluidState(c);
                        SoundTools.playSound(level, absorbingBlock.getSoundType(oldFluidState.createLegacyBlock(), level, c, null).getBreakSound(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 1.0f, 1.0f);

                        BlockPos finalC = c;
                        FluidTools.pickupFluidBlock(level, c, s -> true, () -> {
                            level.setBlock(finalC, Blocks.AIR.defaultBlockState(), 2);
                        });

                        absorbing--;

                        BlockState newState = level.getBlockState(c);
                        level.sendBlockUpdated(c, oldState, newState, 3);
                    }
                }
            }
            markDirtyClient();
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
        return level.getFluidState(c).createLegacyBlock().getBlock().equals(absorbingBlock);
    }

    public int getAbsorbing() {
        return absorbing;
    }

    public Block getAbsorbingBlock() {
        return absorbingBlock;
    }

    public Fluid getAbsorbingFluid() {
        if (absorbingBlock != null) {
            return absorbingBlock.defaultBlockState().getFluidState().getType();
        } else {
            return null;
        }
    }

    private BlockState isValidSourceBlock(BlockPos coordinate) {
        FluidState state = level.getFluidState(coordinate);
        if (isValidDimletFluid(state)) {
            return state.createLegacyBlock();
        } else {
            return null;
        }
    }

    private boolean isValidDimletFluid(FluidState fluidState) {
        if (fluidState != null && !fluidState.isEmpty()) {
            Fluid fluid = fluidState.getType();
            DimletKey key = new DimletKey(DimletType.FLUID, fluid.getRegistryName().toString());
            DimletSettings settings = DimletDictionary.get().getSettings(key);
            return settings != null && settings.isDimlet();
        }
        return false;
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
            if (info.contains("fluid")) {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(info.getString("fluid")));
                if (fluid != null) {
                    absorbingBlock = fluid.defaultFluidState().createLegacyBlock().getBlock();
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
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
            info.putString("fluid", absorbingBlock.defaultBlockState().getFluidState().getType().getRegistryName().toString());
        }
    }
}
