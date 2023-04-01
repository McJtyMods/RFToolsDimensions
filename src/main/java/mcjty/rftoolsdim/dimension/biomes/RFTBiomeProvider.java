package mcjty.rftoolsdim.dimension.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static mcjty.rftoolsdim.dimension.data.DimensionSettings.SETTINGS_CODEC;

public class RFTBiomeProvider extends BiomeSource {

    public static final Codec<RFTBiomeProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(RFTBiomeProvider::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(RFTBiomeProvider::getSettings)
            ).apply(instance, RFTBiomeProvider::new));

    private final List<Holder<Biome>> biomes;
    private final Set<TagKey<Biome>> biomeCategories;
    private final Map<ResourceLocation, Holder<Biome>> biomeMapping = new HashMap<>();
    private final Registry<Biome> biomeRegistry;
    private final DimensionSettings settings;
    private final MultiNoiseBiomeSource multiNoiseBiomeSource;
    private final boolean defaultBiomes;
    private Holder<Biome> biome1 = null;   // For single and checker
    private Holder<Biome> biome2 = null;   // For checker

    public RFTBiomeProvider(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        super(() -> getDefaultBiomes(biomeRegistry, settings));
        this.settings = settings;
        this.biomeRegistry = biomeRegistry;
        multiNoiseBiomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(biomeRegistry, true);
        biomes = getBiomes(biomeRegistry, settings);
        biomeCategories = getBiomeCategories(settings);

        defaultBiomes = biomes.isEmpty() && biomeCategories.isEmpty();
        biomeRegistry.stream().forEach(this::getMappedBiome);

        getBiome1And2();
    }

    private static List<Holder<Biome>> getDefaultBiomes(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        List<ResourceLocation> biomes = settings.getCompiledDescriptor().getBiomes();
        if (biomes.isEmpty()) {
            return biomeRegistry.stream().map(b -> biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(b)))).collect(Collectors.toList());
        }
        return biomes.stream().map(biomeRegistry::get).map(b -> biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(b)))).collect(Collectors.toList());
    }

    public DimensionSettings getSettings() {
        return settings;
    }

    private boolean isCategoryMatching(Biome biome) {
        if (biomeCategories.isEmpty()) {
            return true;
        }
        return biomeRegistry.getResourceKey(biome).map(key -> biomeRegistry.getHolderOrThrow(key).tags().filter(biomeCategories::contains).findAny().isPresent()).orElse(false);
    }

    private Holder<Biome> getMappedBiome(Biome biome) {
        if (defaultBiomes) {
            return biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(biome)));
        }
        return biomeMapping.computeIfAbsent(biomeRegistry.getKey(biome), resourceLocation -> {
            List<Holder<Biome>> biomes = getBiomes(biomeRegistry, settings);
            final float[] minDist = {1000000000};
            final Biome[] desired = {biome};
            if (biomes.isEmpty()) {
                // Biomes was empty. Try to get one with the correct category
                // @todo why does the first one have to fail???
                if (!isCategoryMatching(desired[0])) {
                    biomeRegistry.stream().forEach(b -> {
                        if (isCategoryMatching(b)) {
                            float dist = distance(b, biome);
                            if (dist < minDist[0]) {
                                desired[0] = b;
                                minDist[0] = dist;
                            }
                        }
                    });
                }
            } else {
                // If there are biomes we try to find one while also keeping category in mind
                for (Holder<Biome> b : biomes) {
                    if (biomeCategories.isEmpty() || isCategoryMatching(b.value())) {
                        float dist = distance(b.value(), biome);
                        if (dist < minDist[0]) {
                            desired[0] = b.value();
                            minDist[0] = dist;
                        }
                    }
                }
            }
            return biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(desired[0])));
        });
    }

    private float distance(Biome biome1, Biome biome2) {
        if (Objects.equals(biome1, biome2)) {
            return -1;
        }
        var tags1 = biomeRegistry.getHolderOrThrow(biomeRegistry.getResourceKey(biome1).get()).tags().collect(Collectors.toSet());
        var tags2 = biomeRegistry.getHolderOrThrow(biomeRegistry.getResourceKey(biome2).get()).tags().collect(Collectors.toSet());
        tags1.removeAll(tags2);
        tags1 = biomeRegistry.getHolderOrThrow(biomeRegistry.getResourceKey(biome1).get()).tags().collect(Collectors.toSet());
        tags2.removeAll(tags1);
        float d1 = Math.max(tags1.size(), tags2.size());    // Use the number of differences in tags as a measure
        float d2 = Math.abs(biome1.getBaseTemperature() - biome2.getBaseTemperature());
        float d3 = Math.abs(biome1.getDownfall() - biome2.getDownfall());
        float d4 = biome1.isHumid() == biome2.isHumid() ? 0 : 1;
        return d1 + d2*d2 + d3*d3 + d4;
    }

    private List<Holder<Biome>> getBiomes(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        List<ResourceLocation> biomes = settings.getCompiledDescriptor().getBiomes();
        return biomes.stream().map(biomeRegistry::get).map(b -> biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(b)))).collect(Collectors.toList());
    }

    private Set<TagKey<Biome>> getBiomeCategories(DimensionSettings settings) {
        Set<TagKey<Biome>> categories = settings.getCompiledDescriptor().getBiomeCategories();
        return categories;
    }

    public Registry<Biome> getBiomeRegistry() {
        return biomeRegistry;
    }

    @Nonnull
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }


    private void getBiome1And2() {
        if (biome1 == null) {
            if (biomes.isEmpty()) {
                // Try to get from categories
                List<Biome> list = biomeRegistry.stream().filter(this::isCategoryMatching).toList();
                if (list.isEmpty()) {
                    // Safety, this is needed in case the category doesn't contain any biomes
                    biome1 = biome2 = biomeRegistry.getHolderOrThrow(Biomes.PLAINS);
                } else {
                    biome1 = biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(list.get(0))));
                    if (list.size() > 1) {
                        biome2 = biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry.getKey(list.get(1))));
                    } else {
                        biome2 = biome1;
                    }
                }
            } else {
                biome1 = biomes.get(0);
                if (biomes.size() > 1) {
                    biome2 = biomes.get(1);
                } else {
                    biome2 = biome1;
                }
            }
            biome1 = getMappedBiome(biome1.value());
            if (biome1 == null) {
                // Safety. Shouldn't be possible
                biome1 = biomeRegistry.getHolderOrThrow(Biomes.PLAINS);
            }
            biome2 = getMappedBiome(biome2.value());
            if (biome2 == null) {
                // Safety
                biome2 = biome1;
            }
        }
    }

    @Override
    @Nonnull
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler climate) {
        return switch (settings.getCompiledDescriptor().getBiomeControllerType()) {
            case CHECKER -> getCheckerBiome(x, z);
            case SINGLE -> getSingleBiome();
            default -> getDefaultBiome(x, y, z, climate);
        };
    }

    private Holder<Biome> getDefaultBiome(int x, int y, int z, Climate.Sampler climate) {
        if (defaultBiomes) {
            return multiNoiseBiomeSource.getNoiseBiome(x, y, z, climate);
        } else {
            return getMappedBiome(multiNoiseBiomeSource.getNoiseBiome(x, y, z, climate).value());
        }
    }

    private Holder<Biome> getSingleBiome() {
        return biome1;
    }

    private Holder<Biome> getCheckerBiome(int x, int z) {
        if (((x >>3)+(z >>3))%2 == 0) {
            return biome1;
        } else {
            return biome2;
        }
    }
}
