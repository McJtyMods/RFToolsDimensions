package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.SoundTools;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcjty.rftoolsdim.config.Settings;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class MaterialAbsorberTileEntity extends GenericTileEntity implements ITickable {
    private static final int ABSORB_SPEED = 2;

    private int absorbing = 0;
    private IBlockState blockState = null;
    private int timer = ABSORB_SPEED;
    private Set<BlockPos> toscan = new HashSet<>();

    @Override
    public void update() {
        if (getWorld().isRemote) {
            checkStateClient();
        } else {
            checkStateServer();
        }
    }

    private void checkStateClient() {
        if (absorbing > 0) {
            Random rand = getWorld().rand;

            double u = rand.nextFloat() * 2.0f - 1.0f;
            double v = (float) (rand.nextFloat() * 2.0f * Math.PI);
            double x = Math.sqrt(1 - u * u) * Math.cos(v);
            double y = Math.sqrt(1 - u * u) * Math.sin(v);
            double z = u;
            double r = 1.0f;

            getWorld().spawnParticle(EnumParticleTypes.PORTAL, getPos().getX() + 0.5f + x * r, getPos().getY() + 0.5f + y * r, getPos().getZ() + 0.5f + z * r, -x, -y, -z);
        }
    }

    private void checkBlock(BlockPos c, EnumFacing direction) {
        BlockPos c2 = c.offset(direction);
        if (blockMatches(c2)) {
            toscan.add(c2);
        }
    }

    private boolean blockMatches(BlockPos c) {
        return getWorld().getBlockState(c).equals(blockState);
    }

    public int getAbsorbing() {
        return absorbing;
    }

    public IBlockState getBlockState() {
        return blockState;
    }

    private IBlockState isValidSourceBlock(BlockPos coordinate) {
        IBlockState state = getWorld().getBlockState(coordinate);
        return isValidDimletBlock(state) ? state : null;
    }


    private void checkStateServer() {
        if (absorbing > 0 || blockState == null) {
            timer--;
            if (timer <= 0) {
                timer = ABSORB_SPEED;
                IBlockState b = isValidSourceBlock(getPos().down());
                if (b != null) {
                    if (blockState == null) {
                        absorbing = DimletConstructionConfiguration.maxBlockAbsorbtion;
                        if (b.getBlock() == Blocks.LIT_REDSTONE_ORE) {
                            b = Blocks.REDSTONE_ORE.getDefaultState();
                        }
                        if (Item.getItemFromBlock(b.getBlock()) != null) {
                            // Safety
                            blockState = b;
                            toscan.clear();
                        }
                    }
                    toscan.add(getPos().down());
                }

                if (!toscan.isEmpty()) {
                    int r = getWorld().rand.nextInt(toscan.size());
                    Iterator<BlockPos> iterator = toscan.iterator();
                    BlockPos c = null;
                    for (int i = 0 ; i <= r ; i++) {
                        c = iterator.next();
                    }
                    toscan.remove(c);
                    checkBlock(c, EnumFacing.DOWN);
                    checkBlock(c, EnumFacing.UP);
                    checkBlock(c, EnumFacing.EAST);
                    checkBlock(c, EnumFacing.WEST);
                    checkBlock(c, EnumFacing.SOUTH);
                    checkBlock(c, EnumFacing.NORTH);

                    if (blockMatches(c)) {
                        SoundTools.playSound(getWorld(), blockState.getBlock().getSoundType().breakSound, getPos().getX(), getPos().getY(), getPos().getZ(), 1.0f, 1.0f);
                        getWorld().setBlockToAir(c);
                        absorbing--;
                        IBlockState state = getWorld().getBlockState(c);
                        getWorld().notifyBlockUpdate(c, state, state, 3);
                    }
                }
            }
            markDirtyClient();
        }
    }

    private boolean isValidDimletBlock(IBlockState state) {
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        DimletKey key = new DimletKey(DimletType.DIMLET_MATERIAL, block.getRegistryName() + "@" + meta);
        Settings settings = KnownDimletConfiguration.getSettings(key);
        return settings != null && settings.isDimlet();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
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
        tagCompound.setIntArray("toscanx", x);
        tagCompound.setIntArray("toscany", y);
        tagCompound.setIntArray("toscanz", z);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("absorbing", absorbing);
        if (blockState != null) {
            String resourcePath = blockState.getBlock().getRegistryName().toString();
            tagCompound.setString("block", resourcePath);
            int meta = blockState.getBlock().getMetaFromState(blockState);
            tagCompound.setInteger("meta", meta);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        int[] x = tagCompound.getIntArray("toscanx");
        int[] y = tagCompound.getIntArray("toscany");
        int[] z = tagCompound.getIntArray("toscanz");
        toscan.clear();
        for (int i = 0 ; i < x.length ; i++) {
            toscan.add(new BlockPos(x[i], y[i], z[i]));
        }
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        absorbing = tagCompound.getInteger("absorbing");
        if (tagCompound.hasKey("block")) {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(tagCompound.getString("block")));
            int meta = tagCompound.getInteger("meta");
            blockState = block.getStateFromMeta(meta);
        } else {
            blockState = null;
        }
    }


}

