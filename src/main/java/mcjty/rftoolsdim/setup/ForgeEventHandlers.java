package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.power.PowerHandler;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.modules.blob.tools.Spawner;
import mcjty.rftoolsdim.modules.dimlets.DimletConfig;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import mcjty.rftoolsdim.modules.dimlets.network.PacketSendDimletPackages;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgeEventHandlers {

    private final Random random = new Random();
    private final PowerHandler powerHandler = new PowerHandler();

    private static int counter = 50;

    @SubscribeEvent
    public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        // UGLY HACK to prevent stack overflow in rftools dimensions with block updates
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.getChunkSource().getGenerator() instanceof RFToolsChunkGenerator) {
                counter--;
                if (counter < 0) {
                    counter = 50;
                    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                    if (stacktrace.length > 400) {
                        RFToolsDim.setup.getLogger().warn("Canceled a possible stackoverflow: " + stacktrace.length);
                        event.setCanceled(true);
                        counter = 1;    // Force to check sooner
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.level.isClientSide) {
            // This should be in PotentialSpawns but that doesn't appear to be working correctly
            handleSpawning(event);

            if (event.level.dimension() == Level.OVERWORLD) {
                powerHandler.handlePower(event.level);
            }
        }
    }

    private void handleSpawning(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide) {
            return;
        }
        ServerLevel serverWorld = (ServerLevel) event.level;
        if (serverWorld.players().isEmpty()) {
            return;
        }
        if (serverWorld.getChunkSource().getGenerator() instanceof RFToolsChunkGenerator chunkGenerator) {
            if (random.nextInt(20) == 10) {
                CompiledDescriptor compiledDescriptor = chunkGenerator.getDimensionSettings().getCompiledDescriptor();
                DimensionData data = PersistantDimensionManager.get(serverWorld).getData(serverWorld.dimension().location());
                if (!compiledDescriptor.getAttributeTypes().contains(AttributeType.NOBLOBS)) {
                    int count = 0;
                    for (Entity entity : serverWorld.getEntities().getAll()) {
                        if (entity instanceof DimensionalBlobEntity) {
                            count++;
                        }
                    }

                    if (count < 20) {
                        for (ServerPlayer player : serverWorld.players()) {
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
    public void onServerStarted(ServerStartedEvent event) {
        RFToolsDim.setup.getLogger().info("Reading dimlet packages: ");
        DimletDictionary.get().reset();
        for (String file : DimletConfig.DIMLET_PACKAGES.get()) {
            DimletDictionary.get().readPackage(event.getServer().registryAccess(), file);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Send over the dimlets to the client
        RFToolsDim.setup.getLogger().info("Client logged in: sending dimlet packages");
        Map<DimletKey, DimletSettings> collected = new HashMap<>();
        DimletDictionary dictionary = DimletDictionary.get();
        for (DimletKey key : dictionary.getDimlets()) {
            collected.put(key, dictionary.getSettings(key));
            if (collected.size() >= 100) {
                RFToolsDimMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                        new PacketSendDimletPackages(collected));
                collected.clear();
            }
        }
        if (!collected.isEmpty()) {
            RFToolsDimMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                    new PacketSendDimletPackages(collected));
        }
    }

    //    private MobSpawnInfo.Spawners blobEntry = null;
//
//    @SubscribeEvent
//    public void onPotentialSpawn(WorldEvent.PotentialSpawns event) {
//        IWorld ilevel = event.getLevel();
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
