package mcjty.rftoolsdim.dimension.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.tools.DimensionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.server.ServerLifecycleHooks;

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
        private final long reservationTime;
        private final BlockPos pos;
        private final ResourceKey<Level> world;

        public ReservedName(Level world, BlockPos pos, long reservationTime) {
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
    public CompiledDescriptor getCompiledDescriptor(@Nullable Level world) {
        if (world == null) {
            return null;
        }
        ResourceKey<Level> type = world.dimension();
        ResourceLocation id = type.location();
        return getCompiledDescriptor(world, id);
    }

    public CompiledDescriptor getCompiledDescriptor(Level overworld, ResourceLocation id) {
        if (!compiledDescriptorMap.containsKey(id)) {
            ServerLevel world = LevelTools.getLevel(overworld, id);
            if (world == null) {
                // No data yet
                return null;
            }
            ChunkGenerator generator = world.getChunkSource().getGenerator();
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
    public void markReservedName(Level world, BlockPos pos, String name) {
        reservedDimensionNames.put(name, new ReservedName(world, pos, System.currentTimeMillis()));
    }

    // Function to get the RFTools Dimensions world for the given name. Supports both rftoolsdim:xxx notation
    // as well as just xxx
    public Level getDimWorld(String name) {
        ResourceLocation id = new ResourceLocation(name);
        ResourceKey<Level> type = LevelTools.getId(id);
        ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(type);
        if (world == null) {
            if (!name.contains(":")) {
                id = new ResourceLocation(RFToolsDim.MODID, name);
                type = LevelTools.getId(id);
                return ServerLifecycleHooks.getCurrentServer().getLevel(type);
            }
        }
        return world;
    }

    // Check if a given name is available for making a dimension
    // If pos is null we don't check on position
    public boolean isNameAvailable(Level world, @Nullable BlockPos pos, String name) {
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
    public boolean isDescriptorAvailable(Level world, DimensionDescriptor descriptor) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        DimensionData data = mgr.getData(descriptor);
        return data == null;
    }

    public ServerLevel createWorld(Level world, String name, long seed, DimensionDescriptor descriptor, DimensionDescriptor randomizedDescriptor) {
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

        ResourceKey<Level> key = LevelTools.getId(id);
        RegistryAccess registryAccess = world.getServer().registryAccess();
        DimensionType type = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(timeType.getDimensionType());
        ServerLevel result = DimensionHelper.getOrCreateWorld(world.getServer(), key,
                (server, registryKey) -> {
//                    ChunkGenerator generator = terrainType.getGeneratorSupplier().apply(server, settings);
                    // @todo 1.18
                    NoiseGeneratorSettings noiseSettings = registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(NoiseGeneratorSettings.OVERWORLD);
                    ChunkGenerator generator = new RFToolsChunkGenerator(registryAccess.registryOrThrow(Registry.NOISE_REGISTRY),
                            MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY)), seed,
                            () -> noiseSettings);
                    return new LevelStem(() -> type, generator);
                });

        data = new DimensionData(id, descriptor, randomizedDescriptor);
        mgr.register(data);
        return result;

    }

    // Returns null on success, otherwise an error string
    public String createDimension(Level world, String name, long seed, String filename) {
        ResourceKey<Level> id = LevelTools.getId(new ResourceLocation(RFToolsDim.MODID, name));
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
