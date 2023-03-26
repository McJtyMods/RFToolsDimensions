package mcjty.rftoolsdim.dimension.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.compat.LostCityCompat;
import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.additional.SkyDimletType;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.noisesettings.NoiseGeneratorSettingsBuilder;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.dimension.terraintypes.RFToolsChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.tools.DynamicDimensionManager;
import mcjty.rftoolsdim.tools.Primes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Manages runtime handling of a dimension. That includes the compiled descriptors and creation of dimensions
 */
public class DimensionCreator {

    private final Map<ResourceLocation, CompiledDescriptor> compiledDescriptorMap = new HashMap<>();

    // This information can be used by the dimension builder to place the matter receiver if it can't find the commandblock
    // (because it may be overwritten by other things)
    private final Map<ResourceLocation, Integer> platformHeightMap = new HashMap<>();

    // A transient map containing dimension names that are being created (with a timestamp). It's up to the
    // dimension builder to keep this up to date
    private record ReservedName(long reservationTime, BlockPos pos,
                                ResourceKey<Level> world) {

        public static ReservedName create(Level world, BlockPos pos, long reservationTime) {
            return new ReservedName(reservationTime, pos, world.dimension());
        }
    }
    private final Map<String, ReservedName> reservedDimensionNames = new HashMap<>();

    private static final DimensionCreator instance = new DimensionCreator();

    public static DimensionCreator get() {
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
        reservedDimensionNames.put(name, ReservedName.create(world, pos, System.currentTimeMillis()));
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

    public ServerLevel createWorld(Level world, String name, long seed,
                                   DimensionDescriptor descriptor, DimensionDescriptor randomizedDescriptor,
                                   UUID owner) {
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
        try {
            compiledDescriptor.compile(descriptor, randomizedDescriptor);
        } catch (DescriptorError error) {
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
        Holder<DimensionType> type = registryAccess.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DIMENSION_TYPE, dimensionType));
        ServerLevel result = DynamicDimensionManager.getOrCreateLevel(world.getServer(), key,
                (server, registryKey) -> {
                    var noiseGeneratorSettings = registryAccess.registryOrThrow(Registries.NOISE_SETTINGS);
                    var noiseSettingsIn = noiseGeneratorSettings.getOrThrow(terrainType.getNoiseSettings());
                    var noiseSettings = adapt(noiseSettingsIn, settings);

                    ChunkGenerator generator = new RFToolsChunkGenerator(
                            getStructures(server, settings),
                            new RFTBiomeProvider(
                                    registryAccess.registryOrThrow(Registries.WORLD_PRESET).asLookup(),
                                    registryAccess.registryOrThrow(Registries.BIOME).asLookup(), settings),
                            seed, Holder.direct(noiseSettings), settings);
                    return new LevelStem(type, generator);
                });

        long skyDimletTypes = compiledDescriptor.getSkyDimletTypes();
        if (skyDimletTypes == 0 && terrainType == TerrainType.CAVERN) {
            skyDimletTypes = SkyDimletType.BLACK.getMask() | SkyDimletType.BLACKFOG.getMask(); // Use black as default in case of cavern world
        }
        data = new DimensionData(id, descriptor, randomizedDescriptor, owner, skyDimletTypes);
        mgr.register(data);
        return result;
    }

    public static ServerLevel getOverworld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(Level.OVERWORLD);
    }

    @NotNull
    private List<Holder<StructureSet>> getStructures(MinecraftServer server, DimensionSettings settings) {
        List<ResourceLocation> structures = settings.getCompiledDescriptor().getStructures();
        List<Holder<StructureSet>> list = new ArrayList<>();
        Primes primes = new Primes();
        for (ResourceLocation structure : structures) {
            if (structure.getPath().equals("none")) {
                list.clear();  // No structures
                break;
            } else if (structure.getPath().equals("default"))  {
                return Collections.emptyList();
            } else {
                var registryName = Registries.STRUCTURE;
                var registry = getOverworld().registryAccess().registryOrThrow(registryName);
                var tagKey = TagKey.create(registryName, structure);
                var tag = registry.getOrCreateTag(tagKey);
                if (tag.size() != 0) {
                    tag.forEach(st -> {
                        StructureSet set = new StructureSet(st, new RandomSpreadStructurePlacement(12, 5, RandomSpreadType.LINEAR, primes.nextIntUnsigned()));
                        list.add(Holder.direct(set));
                    });
                } else {
                    if (BuiltInRegistries.STRUCTURE_TYPE.containsKey(structure)) {
                        server.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolder(ResourceKey.create(Registries.STRUCTURE, structure)).ifPresent(cfg -> {
                            StructureSet set = new StructureSet(cfg, new RandomSpreadStructurePlacement(12, 5, RandomSpreadType.LINEAR, primes.nextIntUnsigned()));
                            list.add(Holder.direct(set));
                        });
                    }
                }
                server.registryAccess().registryOrThrow(Registries.STRUCTURE_SET).getHolder(ResourceKey.create(Registries.STRUCTURE_SET, structure)).ifPresent(list::add);
            }
        }
        return list;
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
            SurfaceRules.RuleSource adapted = adaptSurfaceRule(in.surfaceRule(), compiledDescriptor.getBaseBlock());
            builder.ruleSource(adapted);
        }
        builder.liquidBlock(compiledDescriptor.getBaseLiquid());
        return builder.build(settings);
    }

    private SurfaceRules.RuleSource adaptSurfaceRule(SurfaceRules.RuleSource input, BlockState baseBlock) {
        if (input instanceof SurfaceRules.BlockRuleSource) {
            return new SurfaceRules.BlockRuleSource(baseBlock);
        } else if (input instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
            SurfaceRules.SequenceRuleSource output = new SurfaceRules.SequenceRuleSource(new ArrayList<>());
            for (SurfaceRules.RuleSource source : sequenceRuleSource.sequence()) {
                output.sequence().add(adaptSurfaceRule(source, baseBlock));
            }
            return output;
        } else {
            return input;
        }
    }

    // Returns null on success, otherwise an error string
    public String createDimension(Level world, String name, long seed, String filename, UUID owner) {
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

        createWorld(world, name, seed, descriptor, DimensionDescriptor.EMPTY, owner);
        return null;
    }

    public void registerPlatformHeight(ResourceLocation location, int floorHeight) {
        platformHeightMap.put(location, floorHeight);
    }

    public int getPlatformHeight(ResourceLocation location) {
        return platformHeightMap.getOrDefault(location, 65);
    }

}
