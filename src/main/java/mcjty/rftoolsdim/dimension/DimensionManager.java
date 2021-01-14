package mcjty.rftoolsdim.dimension;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.BaseChunkGenerator;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DimensionManager {

    private final Map<ResourceLocation, CompiledDescriptor> compiledDescriptorMap = new HashMap<>();

    private static final DimensionManager instance = new DimensionManager();

    public static DimensionManager get() {
        return instance;
    }

    /**
     * Get the dimension information for a given world
     */
    public CompiledDescriptor getDimensionInformation(World world) {
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

    private void createWorld(World world, String name, DimensionDescriptor descriptor) {
        CompiledDescriptor compiledDescriptor = new CompiledDescriptor();
        String error = compiledDescriptor.compile(descriptor);
        if (error != null) {
            RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error);
            throw new RuntimeException("Error compiling dimension descriptor: " + error);
        }
        TerrainType terrainType = compiledDescriptor.getTerrainType();

        DimensionSettings settings = new DimensionSettings(descriptor.compact());

        RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(RFToolsDim.MODID, name));
        DimensionType type = world.getServer().func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY).getOrDefault(terrainType.getTypeId());
        DimensionHelper.getOrCreateWorld(world.getServer(), key,
                (server, registryKey) -> new Dimension(() -> type, terrainType.getGeneratorSupplier().apply(server, settings)));
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
