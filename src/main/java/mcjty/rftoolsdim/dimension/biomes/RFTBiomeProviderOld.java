package mcjty.rftoolsdim.dimension.biomes;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import mcjty.rftoolsdim.dimension.DimensionInformation;
import mcjty.rftoolsdim.dimension.DimensionManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RFTBiomeProviderOld extends BiomeProvider {

    private static final Set<RegistryKey<Biome>> BIOMES = ImmutableSet.of(Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
    private Layer genBiomes;
    private final World world;
    private final Map<ResourceLocation, Biome> replacementBiomes = new HashMap<>();
    private Set<Biome> filteredBiomes = null;
    private BiomeInfo biomeInfo = null;

    public RFTBiomeProviderOld(World world) {
        super(getStandardBiomes());
        this.world = world;
        this.genBiomes = LayerUtil.func_237215_a_(((ServerWorld) world).getSeed(), false, 4, 4);
        // @todo 1.16 check
//        this.genBiomes = LayerUtil.func_227474_a_(world.getSeed(), world.getWorldType(), new OverworldGenSettings());
    }

    private static List<Biome> getStandardBiomes() {
        return BIOMES.stream().map(k -> ForgeRegistries.BIOMES.getValue(k.getLocation())).collect(Collectors.toList());
    }

    private BiomeInfo getBiomeInfo() {
        if (biomeInfo == null) {
            DimensionInformation info = DimensionManager.get(world).getDimensionInformation(world);
            if (info != null) {
                biomeInfo = info.getBiomeInfo();
            }
        }
        return biomeInfo;
    }

    private boolean isOk(Biome biome) {
        BiomeInfo info = getBiomeInfo();
        if (info == null) {
            return true;
        }
        Set<Biome.Climate> tempCategory = info.getTempCategory();
        Set<Biome.Category> biomeCategory = info.getBiomeCategory();

        // @todo 1.16
//        if (!tempCategory.isEmpty() && !tempCategory.contains(biome.getTempCategory())) {
//            return false;
//        }
        if (!biomeCategory.isEmpty() && !biomeCategory.contains(biome.getCategory())) {
            return false;
        }
        return true;
    }

    private Set<Biome> getFilteredBiomes() {
        if (filteredBiomes == null) {
            filteredBiomes = biomes.stream().filter(this::isOk).collect(Collectors.toSet());
        }
        return filteredBiomes;
    }

    private float calculateDistance(Biome biome1, Biome biome2) {
        if (biome1 == biome2) {
            return 0;
        }
        float category = biome1.getCategory() == biome2.getCategory() ? 0 : 12;
        float temperature = Math.abs(biome1.getTemperature()-biome2.getTemperature()) < 0.0001f ? 0 : 10;
        float precipitation = biome1.getPrecipitation() == biome2.getPrecipitation() ? 0 : 8;
        float downfallDifference = Math.abs(biome1.getDownfall() - biome2.getDownfall());

        // @todo 1.16.3
//        float temperatureDifference = Math.abs(biome1.getDefaultTemperature() - biome2.getDefaultTemperature());
        float temperatureDifference = 0;

        float depthDifference = Math.abs(biome1.getDepth() - biome2.getDepth());
        float scaleDifference = Math.abs(biome1.getScale() - biome2.getScale());
        return temperature + category + precipitation + downfallDifference + temperatureDifference + depthDifference + scaleDifference;
    }

    private Biome findReplacement(Biome original) {
        Biome biome = replacementBiomes.get(original.getRegistryName());
        if (biome == null) {
            if (isOk(original)) {
                replacementBiomes.put(original.getRegistryName(), original);
                return original;
            }
            // Try to find the best matching biome that is ok by the filter
            float bestDistance = 1000000.0f;
            Biome best = null;
            for (Biome b : getFilteredBiomes()) {
                float dist = calculateDistance(b, original);
                if (dist < bestDistance) {
                    bestDistance = dist;
                    best = b;
                }
            }
            replacementBiomes.put(original.getRegistryName(), best);
            return best;
        }
        return biome;
    }

    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        // @todo 1.16 check?
        return new RFTBiomeProviderOld(this.world);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        DynamicRegistries.Impl dynamicRegistries = DynamicRegistries.func_239770_b_();
        Registry<Biome> registry = dynamicRegistries.getRegistry(Registry.BIOME_KEY);
        return findReplacement(this.genBiomes.func_242936_a(registry, x, z));
    }

//    @Override
//    public Biome getNoiseBiome(int x, int y, int z) {
//        return findReplacement(this.genBiomes.func_215738_a(x, z));
//    }
}
