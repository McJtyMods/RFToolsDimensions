package mcjty.rftoolsdim.modules.blob.tools;

import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;

public class Spawner {

    public static void spawnOne(ServerLevel world, Player player, CompiledDescriptor compiledDescriptor, DimensionData data, Random random) {
        double distanceX;
        double distanceZ;
        distanceX = random.nextDouble() * 100 - 50;
        distanceZ = random.nextDouble() * 100 - 50;
        while (distanceX < 22 && distanceZ < 22) {
            distanceX = random.nextDouble() * 100 - 50;
            distanceZ = random.nextDouble() * 100 - 50;
        }
        int x = (int) (player.getX() + distanceX);
        int z = (int) (player.getZ() + distanceZ);
        EntityType<DimensionalBlobEntity> type = randomBlob(compiledDescriptor, data, random);
        BlockPos pos = getValidSpawnablePosition(random, world, x, z);
        if (pos == null) {
            return;
        }
        boolean nocollisions = world.noCollision(type.getAABB(x, pos.getY(), z));
        boolean canSpawn = true;//EntitySpawnPlacementRegistry.canSpawnEntity(type, world, SpawnReason.NATURAL, new BlockPos(x, pos.getY(), z), random);
        if (!nocollisions || !canSpawn) {
            return;
        }
        DimensionalBlobEntity entity = type.create(world);
        entity.moveTo(x, pos.getY(), z, random.nextFloat() * 360.0F, 0.0F);
        if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(entity, world, x, pos.getY(), z, null, MobSpawnType.NATURAL) == -1) {
            return;
        }
        if (entity.checkSpawnRules(world, MobSpawnType.NATURAL) && entity.checkSpawnObstruction(world)) {
            entity.finalizeSpawn(world, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.NATURAL, null, null);
            world.addFreshEntityWithPassengers(entity);
        }
    }

    private static EntityType<DimensionalBlobEntity> randomBlob(CompiledDescriptor compiledDescriptor, DimensionData data, Random random) {
        float perTick = compiledDescriptor != null ? compiledDescriptor.getActualPowerCost() : 0;
        perTick = Math.min(perTick, 50000.0f);
        float rareChance = .1f + perTick / 150000.0f;

        if (random.nextFloat() < rareChance) {
            if (random.nextFloat() < rareChance) {
                return BlobModule.DIMENSIONAL_BLOB_LEGENDARY.get();
            }
            return BlobModule.DIMENSIONAL_BLOB_RARE.get();
        }
        return BlobModule.DIMENSIONAL_BLOB_COMMON.get();
    }

    @Nullable
    private static BlockPos getValidSpawnablePosition(Random random, LevelReader worldIn, int x, int z) {
        int height = worldIn.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        if (height <= 3) {
            return null;
        }
        height = random.nextInt(height - 3) + 3;
        BlockPos blockPos = new BlockPos(x, height-1, z);
        while (!isValidSpawnPos(worldIn, blockPos)) {
            blockPos = blockPos.below();
            if (blockPos.getY() <= 1) {
                return null;
            }
        }
        return blockPos;
    }

    private static boolean isValidSpawnPos(LevelReader world, BlockPos pos) {
        if (!world.getBlockState(pos).isPathfindable(world, pos, PathComputationType.LAND)) {
            return false;
        }
        return world.getBlockState(pos.below()).canOcclude();
    }
}
