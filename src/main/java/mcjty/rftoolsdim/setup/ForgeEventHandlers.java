package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.features.RFTFeature;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.modules.blob.tools.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Random;

public class ForgeEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> RFTFeature.RFTFEATURE_CONFIGURED);
    }

    private Random random = new Random();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        // This should be in PotentialSpawns but that doesn't appear to be working correctly
        if (event.phase == TickEvent.Phase.START && !event.world.isRemote) {
            if (RFToolsDim.MODID.equals(event.world.getDimensionKey().getLocation().getNamespace())) {
                if (random.nextInt(20) == 10) {
                    ServerWorld serverWorld = (ServerWorld) event.world;
                    long count = serverWorld.getEntities().filter(s -> s instanceof DimensionalBlobEntity).count();
                    if (count < 50) {
                        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                            for (int i = 0 ; i < 20 ; i++) {
                                Spawner.spawnOne(serverWorld, player, random);
                            }
                        }
                    }
                }
            }
        }
    }

    //    private MobSpawnInfo.Spawners blobEntry = null;
//
//    @SubscribeEvent
//    public void onPotentialSpawn(WorldEvent.PotentialSpawns event) {
//        IWorld iworld = event.getWorld();
//        if (iworld instanceof World) {
//            if (blobEntry == null) {
//                blobEntry = new MobSpawnInfo.Spawners(BlobModule.DIMENSIONAL_BLOB.get(), 300, 6, 20);
//            }
//            if (RFToolsDim.MODID.equals(((World) iworld).getDimensionKey().getLocation().getNamespace())) {
//                event.getList().add(blobEntry);
//            }
//        }
//
//    }

//    private static List<MobSpawnInfo.Spawners> func_241463_a_(ServerWorld world, StructureManager structureManager, ChunkGenerator generator, EntityClassification entityClassification, BlockPos pos, @Nullable Biome biome) {
//        return ((entityClassification == EntityClassification.MONSTER)
//                && (world.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICKS)
//                && structureManager.getStructureStart(pos, false, Structure.FORTRESS).isValid())
//                ? Structure.FORTRESS.getSpawnList()
//                : generator.func_230353_a_((biome != null)
//                            ? biome
//                            : world.getBiome(pos),
//                    structureManager, entityClassification, pos);
//    }
//
}
