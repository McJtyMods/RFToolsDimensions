package mcjty.rftoolsdim.dimension;

import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.terraintypes.WavesChunkGenerator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

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
            DimensionInformation info = DimensionInformation.createFrom(descriptor);
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
//        try(InputStream inputstream = RFToolsDim.class.getResourceAsStream("/data/rftoolsdim/dimensions/" + filename)) {
//            try(BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
//                JsonParser parser = new JsonParser();
//                JsonElement element = parser.parse(br);
//                descriptor.read(element.getAsJsonObject());
//            }
//        } catch (IOException ex) {
//            throw new UncheckedIOException(ex);
//        }

        dimensions.put(name, descriptor);
        markDirty();
        TerrainType terrainType = descriptor.getTerrainType();

        RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(RFToolsDim.MODID, name));
        DimensionType voidType = world.getServer().func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY).getOrDefault(DimensionRegistry.VOID_ID);
        DimensionType wavesType = world.getServer().func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY).getOrDefault(DimensionRegistry.WAVES_ID);
//        DimensionHelper.getOrCreateWorld(world.getServer(), key, (server, registryKey) -> new Dimension(() -> voidType, new VoidChunkGenerator(server)));
        DimensionHelper.getOrCreateWorld(world.getServer(), key, (server, registryKey) -> new Dimension(() -> wavesType, new WavesChunkGenerator(server)));

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
