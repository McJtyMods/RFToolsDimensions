package mcjty.rftoolsdim.dimension.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.tools.DimensionHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

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

    private static final DimensionManager instance = new DimensionManager();

    public static DimensionManager get() {
        return instance;
    }

    /**
     * Get the dimension information for a given world
     */
    public CompiledDescriptor getDimensionInformation(@Nullable World world) {
        if (world == null) {
            return null;
        }
        RegistryKey<World> type = world.getDimensionKey();
        ResourceLocation id = type.getLocation();
        if (!compiledDescriptorMap.containsKey(id)) {
            ChunkGenerator generator = ((ServerWorld) world).getChunkProvider().generator;
            if (generator instanceof BaseChunkGenerator) {
                CompiledDescriptor compiledDescriptor = ((BaseChunkGenerator) generator).getSettings().getCompiledDescriptor();
                compiledDescriptorMap.put(id, compiledDescriptor);
            } else {
                RFToolsDim.setup.getLogger().error(id.toString() + " is not a dimension managed by us!");
                return null;
            }
        }
        return compiledDescriptorMap.get(id);
    }

    // Function to get the RFTools Dimensions world for the given name. Supports both rftoolsdim:xxx notation
    // as well as just xxx
    public World getDimWorld(String name) {
        ResourceLocation id = new ResourceLocation(name);
        DimensionId type = DimensionId.fromResourceLocation(id);
        ServerWorld world = type.getWorld();
        if (world == null) {
            if (!name.contains(":")) {
                id = new ResourceLocation(RFToolsDim.MODID, name);
                type = DimensionId.fromResourceLocation(id);
                return type.getWorld();
            }
        }
        return world;
    }

    public ServerWorld createWorld(World world, String name, DimensionDescriptor descriptor) {
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
        DescriptorError error = compiledDescriptor.compile(descriptor);
        if (!error.isOk()) {
            RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error.getMessage());
            throw new RuntimeException("Error compiling dimension descriptor: " + error.getMessage());
        }
        TerrainType terrainType = compiledDescriptor.getTerrainType();

        DimensionSettings settings = new DimensionSettings(descriptor.compact());

        RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, id);
        DimensionType type = world.getServer().func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY).getOrDefault(terrainType.getTypeId());
        ServerWorld result = DimensionHelper.getOrCreateWorld(world.getServer(), key,
                (server, registryKey) -> new Dimension(() -> type, terrainType.getGeneratorSupplier().apply(server, settings)));

        data = new DimensionData(id, descriptor);
        mgr.register(data);
        return result;

    }

    // Returns null on success, otherwise an error string
    public String createDimension(World world, String name, String filename) {
//        RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(RFToolsDim.MODID, name));
        DimensionId id = DimensionId.fromResourceLocation(new ResourceLocation(RFToolsDim.MODID, name));
        if (id.loadWorld(world) != null) {
            return "Dimension already exists!";
        }

//        if (compiledDescriptorMap.containsKey(new ResourceLocation(RFToolsDim.MODID, name))) {
//            return "Dimension already exists!";
//        }
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

        createWorld(world, name, descriptor);
        return null;
    }
}
