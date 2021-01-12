package mcjty.rftoolsdim.dimension;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DimensionManager extends AbstractWorldData<DimensionManager> {

    private static final String NAME = "RFToolsDimensionManager";

    private long id = 0;
    private Map<String, DimensionDescriptor> dimensions = new HashMap<>();
    private Map<RegistryKey<World>, DimensionInformation> dimensionInformations = new HashMap<>();

    public DimensionManager() {
        super(NAME);
    }

    public static DimensionManager get(World world) {
        return getData(world, DimensionManager::new, NAME);
    }

    /**
     * Get the dimension information for a given world
     */
    public DimensionInformation getDimensionInformation(World world) {
        RegistryKey<World> type = world.getDimensionKey();
        if (!dimensionInformations.containsKey(type)) {
            String name = type.getRegistryName().getPath();
            DimensionDescriptor descriptor = dimensions.get(name);
            if (descriptor == null) {
                // @todo proper logging
                System.out.println("This is not a dimension managed by us!");
                return null;
            }
            CompiledDescriptor compiledDescriptor = new CompiledDescriptor(descriptor);
            DimensionInformation info = DimensionInformation.createFrom(compiledDescriptor);
            dimensionInformations.put(type, info);
        }
        return dimensionInformations.get(type);
    }

    // Returns null on success, otherwise an error string
    public String createDimension(World world, String name, String filename) {
        if (dimensions.containsKey(name)) {
            return "Dimension already exists!";
        }
        DimensionDescriptor descriptor = new DimensionDescriptor();
        try(InputStream inputstream = RFToolsDim.class.getResourceAsStream("/data/rftoolsdim/rftdim/" + filename)) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(br);
                descriptor.read(element.getAsJsonArray());
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        dimensions.put(name, descriptor);
        markDirty();

        CompiledDescriptor compiledDescriptor = new CompiledDescriptor(descriptor);
        TerrainType terrainType = compiledDescriptor.getTerrainType();

        RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(RFToolsDim.MODID, name));
        DimensionType type = world.getServer().func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY).getOrDefault(terrainType.getTypeId());
        DimensionHelper.getOrCreateWorld(world.getServer(), key,
                (server, registryKey) -> new Dimension(() -> type, terrainType.getGeneratorSupplier().apply(server)));

        return null;
    }

    @Override
    public void read(CompoundNBT nbt) {
        id = nbt.getLong("dimId");

        CompoundNBT dimensionMap = nbt.getCompound("dimensions");
        for (String name : dimensionMap.keySet()) {
            DimensionDescriptor descriptor = new DimensionDescriptor();
            descriptor.read(dimensionMap.getString(name));
            dimensions.put(name, descriptor);
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putLong("dimId", id);

        CompoundNBT dimensionMap = new CompoundNBT();
        for (Map.Entry<String, DimensionDescriptor> entry : dimensions.entrySet()) {
            dimensionMap.putString(entry.getKey(), entry.getValue().write());
        }
        compound.put("dimensions", dimensionMap);

        return compound;
    }
}
