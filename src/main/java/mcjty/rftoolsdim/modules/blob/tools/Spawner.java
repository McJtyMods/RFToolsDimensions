package mcjty.rftoolsdim.modules.blob.tools;

import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class Spawner {

    public static void spawnOne(ServerWorld world, PlayerEntity player, Random random) {
        double distanceX;
        double distanceZ;
        distanceX = random.nextDouble() * 100 - 50;
        distanceZ = random.nextDouble() * 100 - 50;
        while (distanceX < 22 && distanceZ < 22) {
            distanceX = random.nextDouble() * 100 - 50;
            distanceZ = random.nextDouble() * 100 - 50;
        }
        int x = (int) (player.getPosX() + distanceX);
        int z = (int) (player.getPosZ() + distanceZ);
        EntityType<DimensionalBlobEntity> type = randomBlob(random);
        BlockPos pos = getValidSpawnablePosition(random, world, x, z);
        boolean nocollisions = world.hasNoCollisions(type.getBoundingBoxWithSizeApplied(x, pos.getY(), z));
        boolean canSpawn = true;//EntitySpawnPlacementRegistry.canSpawnEntity(type, world, SpawnReason.NATURAL, new BlockPos(x, pos.getY(), z), random);
        if (!nocollisions || !canSpawn) {
            return;
        }
        DimensionalBlobEntity entity = type.create(world);
        entity.setLocationAndAngles(x, pos.getY(), z, random.nextFloat() * 360.0F, 0.0F);
        if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(entity, world, x, pos.getY(), z, null, SpawnReason.NATURAL) == -1) {
            return;
        }
        if (entity.canSpawn(world, SpawnReason.NATURAL) && entity.isNotColliding(world)) {
            entity.onInitialSpawn(world, world.getDifficultyForLocation(entity.getPosition()), SpawnReason.NATURAL, null, null);
            world.func_242417_l(entity);
        }
    }

    private static EntityType<DimensionalBlobEntity> randomBlob(Random random) {
        // @todo base this on power of the dimension
        if (random.nextFloat() < .1) {
            if (random.nextFloat() < .1) {
                return BlobModule.DIMENSIONAL_BLOB_LEGENDARY.get();
            }
            return BlobModule.DIMENSIONAL_BLOB_RARE.get();
        }
        return BlobModule.DIMENSIONAL_BLOB_COMMON.get();
    }

    private static BlockPos getValidSpawnablePosition(Random random, IWorldReader worldIn, int x, int z) {
        int height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE, x, z) + 1;
        height = random.nextInt(height + 1);
        BlockPos blockPos = new BlockPos(x, height-1, z);
        while (blockPos.getY() > 1 && !isValidSpawnPos(worldIn, blockPos)) {
            blockPos = blockPos.down();
        }
        return blockPos;
    }

    private static boolean isValidSpawnPos(IWorldReader world, BlockPos pos) {
        if (!world.getBlockState(pos).allowsMovement(world, pos, PathType.LAND)) {
            return false;
        }
        return world.getBlockState(pos.down()).isSolid();
    }
}
