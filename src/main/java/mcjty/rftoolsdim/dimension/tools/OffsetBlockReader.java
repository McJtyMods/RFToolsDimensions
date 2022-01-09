package mcjty.rftoolsdim.dimension.tools;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OffsetBlockReader implements BlockGetter {

    private final BlockState belowState;
    private final int offset;

    public OffsetBlockReader(BlockState belowState, int offset) {
        this.belowState = belowState;
        this.offset = offset;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@Nonnull BlockPos pos) {
        return null;
    }

    @Nonnull
    @Override
    public BlockState getBlockState(BlockPos pos) {
        int y = pos.getY();
        return y >= 0 && y <= offset ? belowState : Blocks.AIR.defaultBlockState();
    }

    @Nonnull
    @Override
    public FluidState getFluidState(@Nonnull BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }
}
