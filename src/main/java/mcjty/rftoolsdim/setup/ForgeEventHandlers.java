package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.features.RFTFeature;
import mcjty.rftoolsdim.dimension.power.PowerHandler;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.modules.blob.tools.Spawner;
import mcjty.rftoolsdim.modules.dimlets.DimletConfig;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.util.Random;

public class ForgeEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> RFTFeature.RFTFEATURE_CONFIGURED);
    }

    private final Random random = new Random();
    private final PowerHandler powerHandler = new PowerHandler();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.world.isClientSide) {
            // This should be in PotentialSpawns but that doesn't appear to be working correctly
            handleSpawning(event);

            if (event.world.dimension() == World.OVERWORLD) {
                powerHandler.handlePower(event.world);
            }
        }
    }

    private void handleSpawning(TickEvent.WorldTickEvent event) {
        if (RFToolsDim.MODID.equals(event.world.dimension().location().getNamespace())) {
            if (random.nextInt(20) == 10) {
                ServerWorld serverWorld = (ServerWorld) event.world;
                CompiledDescriptor compiledDescriptor = DimensionManager.get().getCompiledDescriptor(serverWorld);
                DimensionData data = PersistantDimensionManager.get(serverWorld).getData(serverWorld.dimension().location());
                if (!compiledDescriptor.getAttributeTypes().contains(AttributeType.NOBLOBS)) {
                    long count = serverWorld.getEntities().filter(s -> s instanceof DimensionalBlobEntity).count();
                    if (count < 20) {
                        for (ServerPlayerEntity player : serverWorld.players()) {
                            for (int i = 0; i < 5; i++) {
                                Spawner.spawnOne(serverWorld, player, compiledDescriptor, data, random);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(FMLServerStartedEvent event) {
        RFToolsDim.setup.getLogger().info("Reading dimlet packages: ");
        DimletDictionary.get().reset();
        for (String file : DimletConfig.DIMLET_PACKAGES.get()) {
            DimletDictionary.get().readPackage(file);
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

//    private static List<MobSpawnInfo.Spawners> mobsAt(ServerWorld world, StructureManager structureManager, ChunkGenerator generator, EntityClassification entityClassification, BlockPos pos, @Nullable Biome biome) {
//        return ((entityClassification == EntityClassification.MONSTER)
//                && (world.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICKS)
//                && structureManager.getStructureStart(pos, false, Structure.FORTRESS).isValid())
//                ? Structure.FORTRESS.getSpawnList()
//                : generator.getMobsAt((biome != null)
//                            ? biome
//                            : world.getBiome(pos),
//                    structureManager, entityClassification, pos);
//    }
//
}
