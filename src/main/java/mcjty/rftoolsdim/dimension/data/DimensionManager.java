package mcjty.rftoolsdim.dimension.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.tools.DimensionHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages runtime handling of a dimension. That includes the compiled descriptors and creation of dimensions
 */
public class DimensionManager {

    private final Map<ResourceLocation, CompiledDescriptor> compiledDescriptorMap = new HashMap<>();

    // This information can be used by the dimension builder to place the matter receiver if it can't find the commandblock
    // (because it may be overwritten by other things)
    private final Map<ResourceLocation, Integer> platformHeightMap = new HashMap<>();

    // A transient map containing dimension names that are being created (with a timestamp). It's up to the
    // dimension builder to keep this up to date
    private static class ReservedName {
        private long reservationTime;
        private final BlockPos pos;
        private final RegistryKey<World> world;

        public ReservedName(World world, BlockPos pos, long reservationTime) {
            this.pos = pos;
            this.world = world.dimension();
            this.reservationTime = reservationTime;
        }
    }
    private final Map<String, ReservedName> reservedDimensionNames = new HashMap<>();

    private static final DimensionManager instance = new DimensionManager();

    public static DimensionManager get() {
        return instance;
    }

    public void clear() {
        platformHeightMap.clear();
        compiledDescriptorMap.clear();
    }

    /**
     * Get the dimension information for a given world
     */
    public CompiledDescriptor getCompiledDescriptor(@Nullable World world) {
        if (world == null) {
            return null;
        }
        RegistryKey<World> type = world.dimension();
        ResourceLocation id = type.location();
        return getCompiledDescriptor(world, id);
    }

    public CompiledDescriptor getCompiledDescriptor(World overworld, ResourceLocation id) {
        if (!compiledDescriptorMap.containsKey(id)) {
            ServerWorld world = WorldTools.getLevel(overworld, id);
            if (world == null || world.getChunkSource() == null) {
                // No data yet
                return null;
            }
            ChunkGenerator generator = world.getChunkSource().generator;
            if (generator instanceof BaseChunkGenerator) {
                CompiledDescriptor compiledDescriptor = ((BaseChunkGenerator) generator).getDimensionSettings().getCompiledDescriptor();
                compiledDescriptorMap.put(id, compiledDescriptor);
            } else {
                RFToolsDim.setup.getLogger().error(id.toString() + " is not a dimension managed by us!");
                return null;
            }
        }
        return compiledDescriptorMap.get(id);
    }

    // Mark a name of a dimension as being reserved for a given time
    public void markReservedName(World world, BlockPos pos, String name) {
        reservedDimensionNames.put(name, new ReservedName(world, pos, System.currentTimeMillis()));
    }

    // Function to get the RFTools Dimensions world for the given name. Supports both rftoolsdim:xxx notation
    // as well as just xxx
    public World getDimWorld(String name) {
        ResourceLocation id = new ResourceLocation(name);
        RegistryKey<World> type = WorldTools.getId(id);
        ServerWorld world = ServerLifecycleHooks.getCurrentServer().getLevel(type);
        if (world == null) {
            if (!name.contains(":")) {
                id = new ResourceLocation(RFToolsDim.MODID, name);
                type = WorldTools.getId(id);
                return ServerLifecycleHooks.getCurrentServer().getLevel(type);
            }
        }
        return world;
    }

    // Check if a given name is available for making a dimension
    // If pos is null we don't check on position
    public boolean isNameAvailable(World world, @Nullable BlockPos pos, String name) {
        long currentTime = System.currentTimeMillis();
        ReservedName reservedName = reservedDimensionNames.get(name);
        if (reservedName != null) {
            // We wait at least 10 seconds before freeing a reserved name
            if (currentTime < reservedName.reservationTime + 10000) {
                if (!reservedName.pos.equals(pos) || !reservedName.world.equals(world.dimension())) {
                    return false;
                }
            }
        }

        ResourceLocation id = new ResourceLocation(RFToolsDim.MODID, name);

        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        DimensionData data = mgr.getData(id);
        return data == null;
    }

    // Check if a given dimlet descriptor is available for making a new dimension
    public boolean isDescriptorAvailable(World world, DimensionDescriptor descriptor) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        DimensionData data = mgr.getData(descriptor);
        return data == null;
    }

    public ServerWorld createWorld(World world, String name, long seed, DimensionDescriptor descriptor, DimensionDescriptor randomizedDescriptor) {
        ResourceLocation id = new ResourceLocation(RFToolsDim.MODID, name);

        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        DimensionData data = mgr.getData(id);
        if (data != null) {
            RFToolsDim.setup.getLogger().error("There is already a dimension with this id: " + name);
            throw new RuntimeException("There is already a dimension with this id: " + name);
        }

        data = mgr.getData(descriptor);
        if (data != null) {
            RFToolsDim.setup.getLogger().error("There is already a dimension with this descriptor: " + name);
            throw new RuntimeException("There is already a dimension with this descriptor: " + name);
        }

        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();
        DescriptorError error = compiledDescriptor.compile(descriptor, randomizedDescriptor);
        if (!error.isOk()) {
            RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error.getMessage());
            throw new RuntimeException("Error compiling dimension descriptor: " + error.getMessage());
        }
        compiledDescriptor.complete();
        TerrainType terrainType = compiledDescriptor.getTerrainType();

        DimensionSettings settings = new DimensionSettings(seed, descriptor.compact(), randomizedDescriptor.compact());

        TimeType timeType = compiledDescriptor.getTimeType();

        RegistryKey<World> key = WorldTools.getId(id);
        DimensionType type = world.getServer().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(timeType.getDimensionType());
        ServerWorld result = DimensionHelper.getOrCreateWorld(world.getServer(), key,
                (server, registryKey) -> new Dimension(() -> type, terrainType.getGeneratorSupplier().apply(server, settings)));

        data = new DimensionData(id, descriptor, randomizedDescriptor);
        mgr.register(data);
        return result;

    }

    // Returns null on success, otherwise an error string
    public String createDimension(World world, String name, long seed, String filename) {
        RegistryKey<World> id = WorldTools.getId(new ResourceLocation(RFToolsDim.MODID, name));
        if (world.getServer().getLevel(id) != null) {
            return "Dimension already exists!";
        }

        DimensionDescriptor descriptor = new DimensionDescriptor();
        if (!filename.endsWith(".json")) {
            filename += ".json";
        }
        try(InputStream inputstream = RFToolsDim.class.getResourceAsStream("/data/rftoolsdim/rftdim/" + filename)) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(br);
                descriptor.read(element.getAsJsonArray());
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        createWorld(world, name, seed, descriptor, DimensionDescriptor.EMPTY);
        return null;
    }

    public void registerPlatformHeight(ResourceLocation location, int floorHeight) {
        platformHeightMap.put(location, floorHeight);
    }

    public int getPlatformHeight(ResourceLocation location) {
        return platformHeightMap.getOrDefault(location, 65);
    }

}
