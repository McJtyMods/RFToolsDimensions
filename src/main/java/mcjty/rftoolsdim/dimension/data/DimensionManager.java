package mcjty.rftoolsdim.dimension.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.compat.LostCityCompat;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.additional.SkyType;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseGeneratorSettingsBuilder;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.tools.DynamicDimensionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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
            if (generator instanceof RFToolsChunkGenerator rftoolsGenerator) {
                CompiledDescriptor compiledDescriptor = rftoolsGenerator.getDimensionSettings().getCompiledDescriptor();
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

        randomizedDescriptor.log("Attempting to create dimension:");

        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();
        DescriptorError error = compiledDescriptor.compile(descriptor, randomizedDescriptor);
        if (!error.isOk()) {
            RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error.getMessage());
            throw new RuntimeException("Error compiling dimension descriptor: " + error.getMessage());
        }
        compiledDescriptor.complete();
        compiledDescriptor.log("Compiled Descriptor:");
        TerrainType terrainType = compiledDescriptor.getTerrainType();
        DimensionSettings settings = new DimensionSettings(seed, descriptor.compact(), randomizedDescriptor.compact());
        TimeType timeType = compiledDescriptor.getTimeType();

        ResourceKey<Level> key = LevelTools.getId(id);

        if (settings.getCompiledDescriptor().getAttributeTypes().contains(AttributeType.CITIES) && LostCityCompat.hasLostCities()) {
            LostCityCompat.registerDimension(key, LostCityCompat.getProfile(terrainType));
        }

        RegistryAccess registryAccess = world.getServer().registryAccess();
        ResourceLocation dimensionType = timeType.getDimensionType();
        if (terrainType == TerrainType.CAVERN) {
            dimensionType = DimensionRegistry.CAVERN_ID;
        }
        DimensionType type = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(dimensionType);
        ServerLevel result = DynamicDimensionManager.getOrCreateLevel(world.getServer(), key,
                (server, registryKey) -> {
                    var noiseGeneratorSettings = registryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
                    var noiseSettingsIn = noiseGeneratorSettings.getOrThrow(terrainType.getNoiseSettings());
                    var noiseSettings = adapt(noiseSettingsIn, settings);
                    ChunkGenerator generator = new RFToolsChunkGenerator(registryAccess.registryOrThrow(Registry.NOISE_REGISTRY),
                            new RFTBiomeProvider(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), settings),
                            seed, () -> noiseSettings, settings);
                    return new LevelStem(() -> type, generator);
                });

        // @todo move to a better place, use dimlets
        SkyType skyType = SkyType.NORMAL;
        if (terrainType == TerrainType.CAVERN) {
            skyType = SkyType.BLACK;
        }
        data = new DimensionData(id, descriptor, randomizedDescriptor, skyType);
        mgr.register(data);
        return result;

    }

    private NoiseGeneratorSettings adapt(NoiseGeneratorSettings in, DimensionSettings settings) {
        NoiseGeneratorSettingsBuilder builder = NoiseGeneratorSettingsBuilder.create(in);
        CompiledDescriptor compiledDescriptor = settings.getCompiledDescriptor();

        if (compiledDescriptor.getAttributeTypes().contains(AttributeType.NOOCEANS)) {
            builder.seaLevel(-64);
        }
        if (compiledDescriptor.getAttributeTypes().contains(AttributeType.WATERWORLD)) {
            builder.seaLevel(200);
        }
        if (compiledDescriptor.getTerrainType().isVoidLike()) {
            // No oceans on void style levels
            builder.seaLevel(-64);
        }

        if (compiledDescriptor.getBaseBlock() != null) {
            builder.baseBlock(compiledDescriptor.getBaseBlock());
        }
        builder.liquidBlock(compiledDescriptor.getBaseLiquid());
        return builder.build(settings);
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
