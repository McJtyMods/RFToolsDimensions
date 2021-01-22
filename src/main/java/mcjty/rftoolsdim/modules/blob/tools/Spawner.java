package mcjty.rftoolsdim.modules.blob.tools;

import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class Spawner {

    public static void spawnOne(ServerWorld world, PlayerEntity player, Random random) {
        double distanceX = random.nextDouble() * 60 - 30;
        double distanceZ = random.nextDouble() * 60 - 30;
        if (distanceX < 0) {
            distanceX -= 16;
        } else {
            distanceX += 16;
        }
        if (distanceZ < 0) {
            distanceZ -= 16;
        } else {
            distanceZ += 16;
        }
        int x = (int) (player.getPosX() + distanceX);
        int z = (int) (player.getPosZ() + distanceZ);
        EntityType<DimensionalBlobEntity> type = BlobModule.DIMENSIONAL_BLOB.get();
        BlockPos pos = getTopSolidOrLiquidBlock(random, world, type, x, z);
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
            System.out.println("Spawner.spawnOne");
        }
    }

    private static BlockPos getTopSolidOrLiquidBlock(Random random, IWorldReader worldIn, EntityType<?> entityType, int x, int z) {
        int height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE, x, z) + 1;
        height = random.nextInt(height + 1);
        BlockPos blockPos = new BlockPos(x, height-1, z);
        while (blockPos.getY() > 1 && !worldIn.getBlockState(blockPos).allowsMovement(worldIn, blockPos, PathType.LAND) &&
                !worldIn.getBlockState(blockPos.down()).isSolid()) {
            blockPos = blockPos.down();
        }
        return blockPos;
    }
}
