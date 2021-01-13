package mcjty.rftoolsdim.dimension.tools;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class OffsetBlockReader implements IBlockReader {

    private final BlockState belowState;
    private final int offset;

    public OffsetBlockReader(BlockState belowState, int offset) {
        this.belowState = belowState;
        this.offset = offset;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int y = pos.getY();
        return y >= 0 && y <= offset ? belowState : Blocks.AIR.getDefaultState();
    }

    public FluidState getFluidState(BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }
}
