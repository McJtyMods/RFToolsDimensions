package mcjty.rftoolsdim.modules.various.blocks;

import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ActivityProbeBlock extends Block {

    public ActivityProbeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide()) {
            if (newState.getBlock() != this) {
                if (((ServerLevel) level).getChunkSource().getGenerator() instanceof RFToolsChunkGenerator) {
                    PersistantDimensionManager mgr = PersistantDimensionManager.get(level);
                    DimensionData data =  mgr.getData(level.dimension().location());
                    data.setActivityProbes(data.getActivityProbes()-1);
                    mgr.save();
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) {
            if (((ServerLevel) level).getChunkSource().getGenerator() instanceof RFToolsChunkGenerator) {
                PersistantDimensionManager mgr = PersistantDimensionManager.get(level);
                DimensionData data =  mgr.getData(level.dimension().location());
                data.setActivityProbes(data.getActivityProbes()+1);
                mgr.save();
            }
        }
    }
}
