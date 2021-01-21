package mcjty.rftoolsdim.dimension.tools;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import mcjty.rftoolsdim.dimension.network.DimensionUpdatePacket;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import mcjty.rftoolsdim.tools.ReflectionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

// Kindly copied and adapted from Hyperbox (Commoble). Thanks a lot for this!
public class DimensionHelper {

    public static final Function<MinecraftServer, IChunkStatusListenerFactory> CHUNK_STATUS_LISTENER_FACTORY_FIELD =
            ReflectionHelper.getInstanceFieldGetter(MinecraftServer.class, "field_213220_d");
    public static final Function<MinecraftServer, Executor> BACKGROUND_EXECUTOR_FIELD =
            ReflectionHelper.getInstanceFieldGetter(MinecraftServer.class, "field_213217_au");
    public static final Function<MinecraftServer, SaveFormat.LevelSave> ANVIL_CONVERTER_FOR_ANVIL_FILE_FIELD =
            ReflectionHelper.getInstanceFieldGetter(MinecraftServer.class, "field_71310_m");

    /**
     * Gets a world, dynamically creating and registering one if it doesn't exist.
     * The dimension registry is stored in the server's level file, all previously registered dimensions are loaded
     * and recreated and reregistered whenever the server starts.<br>
     * Static, singular dimensions can be registered via this getOrCreateWorld method
     * in the FMLServerStartingEvent, which runs immediately after existing dimensions are loaded and registered.<br>
     * Dynamic dimensions (mystcraft, etc) seem to be able to be registered at runtime with no repercussions aside from
     * lagging the server for a couple seconds while the world initializes.
     *
     * @param server           a MinecraftServer instance (you can get this from a ServerPlayerEntity or ServerWorld)
     * @param worldKey         A RegistryKey for your world, you can make one via RegistryKey.getOrCreateKey(Registry.WORLD_KEY, yourWorldResourceLocation);
     * @param dimensionFactory A function that produces a new Dimension instance if necessary, given the server and dimension id<br>
     *                         (dimension ID will be the same as the world ID from worldKey)<br>
     *                         It should be assumed that intended dimension has not been created or registered yet,
     *                         so making the factory attempt to get this dimension from the server's dimension registry will fail
     * @return Returns a ServerWorld, creating and registering a world and dimension for it if the world does not already exist
     */
    public static ServerWorld getOrCreateWorld(MinecraftServer server, RegistryKey<World> worldKey, BiFunction<MinecraftServer, RegistryKey<Dimension>, Dimension> dimensionFactory) {

        // this is marked as deprecated but it's not called from anywhere and I'm not sure how old it is,
        // it's probably left over from forge's previous dimension api
        // in any case we need to get at the server's world field, and if we didn't use this getter,
        // then we'd just end up making a private-field-getter for it ourselves anyway
        @SuppressWarnings("deprecation")
        Map<RegistryKey<World>, ServerWorld> map = server.forgeGetWorldMap();

        // if the world already exists, return it
        if (map.containsKey(worldKey)) {
            return map.get(worldKey);
        } else {
            // for vanilla worlds, forge fires the world load event *after* the world is put into the map
            // we'll do the same for consistency
            // (this is why we're not just using map::computeIfAbsent)
            ServerWorld newWorld = createAndRegisterWorldAndDimension(server, map, worldKey, dimensionFactory);

            return newWorld;
        }
    }

    @SuppressWarnings("deprecation") // markWorldsDirty is deprecated, see below
    private static ServerWorld createAndRegisterWorldAndDimension(MinecraftServer server, Map<RegistryKey<World>, ServerWorld> map, RegistryKey<World> worldKey, BiFunction<MinecraftServer, RegistryKey<Dimension>, Dimension> dimensionFactory) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        RegistryKey<Dimension> dimensionKey = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, worldKey.getLocation());
        Dimension dimension = dimensionFactory.apply(server, dimensionKey);

        // we need to get some private fields from MinecraftServer here
        // chunkStatusListenerFactory
        // backgroundExecutor
        // anvilConverterForAnvilFile
        // the int in create() here is radius of chunks to watch, 11 is what the server uses when it initializes worlds
        IChunkStatusListener chunkListener = CHUNK_STATUS_LISTENER_FACTORY_FIELD.apply(server).create(11);
        Executor executor = BACKGROUND_EXECUTOR_FIELD.apply(server);
        SaveFormat.LevelSave levelSave = ANVIL_CONVERTER_FOR_ANVIL_FILE_FIELD.apply(server);

        // this is the same order server init creates these worlds:
        // instantiate world, add border listener, add to map, fire world load event
        // (in server init, the dimension is already in the dimension registry,
        // that'll get registered here before the world is instantiated as well)

        IServerConfiguration serverConfig = server.getServerConfiguration();
        DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.getDimensionGeneratorSettings();
        // this next line registers the Dimension
        dimensionGeneratorSettings.func_236224_e_().register(dimensionKey, dimension, Lifecycle.experimental());
        DerivedWorldInfo derivedWorldInfo = new DerivedWorldInfo(serverConfig, serverConfig.getServerWorldInfo());
        // now we have everything we need to create the world instance
        ServerWorld newWorld = new ServerWorld(
                server,
                executor,
                levelSave,
                derivedWorldInfo,
                worldKey,
                dimension.getDimensionType(),
                chunkListener,
                dimension.getChunkGenerator(),
                dimensionGeneratorSettings.func_236227_h_(), // boolean: is-debug-world
                BiomeManager.getHashedSeed(dimensionGeneratorSettings.getSeed()),
                ImmutableList.of(), // "special spawn list"
                // phantoms, raiders, travelling traders, cats are overworld special spawns
                // the dimension loader is hardcoded to initialize preexisting non-overworld worlds with no special spawn lists
                // so this can probably be left empty for best results and spawns should be handled via other means
                false); // "tick time", true for overworld, always false for everything else

        // add world border listener
        overworld.getWorldBorder().addListener(new IBorderListener.Impl(newWorld.getWorldBorder()));

        // register world
        map.put(worldKey, newWorld);

        // update forge's world cache (very important, if we don't do this then the new world won't tick!)
        server.markWorldsDirty();

        // fire world load event
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld)); // event isn't cancellable

        RFToolsDimMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new DimensionUpdatePacket(worldKey, true));

        return newWorld;
    }
}