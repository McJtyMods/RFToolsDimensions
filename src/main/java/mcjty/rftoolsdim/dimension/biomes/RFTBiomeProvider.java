package mcjty.rftoolsdim.dimension.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

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
    private final Set<Biome.BiomeCategory> biomeCategories;
    private final Map<ResourceLocation, Holder<Biome>> biomeMapping = new HashMap<>();
    private final Registry<Biome> biomeRegistry;
    private final DimensionSettings settings;
    private final MultiNoiseBiomeSource multiNoiseBiomeSource;
    private final boolean defaultBiomes;
    private Holder<Biome> biome1 = null;   // For single and checker
    private Holder<Biome> biome2 = null;   // For checker

    public RFTBiomeProvider(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        super(Collections.emptyList());
        this.settings = settings;
        this.biomeRegistry = biomeRegistry;
        multiNoiseBiomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(biomeRegistry, true);
        biomes = getBiomes(biomeRegistry, settings);
        biomeCategories = getBiomeCategories(settings);

        defaultBiomes = biomes.isEmpty() && biomeCategories.isEmpty();
        biomeRegistry.stream().forEach(this::getMappedBiome);
    }

    public DimensionSettings getSettings() {
        return settings;
    }

    private Holder<Biome> getMappedBiome(Biome biome) {
        if (defaultBiomes) {
            return biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biome.getRegistryName()));
        }
        return biomeMapping.computeIfAbsent(biome.getRegistryName(), resourceLocation -> {
            List<Holder<Biome>> biomes = getBiomes(biomeRegistry, settings);
            final float[] minDist = {1000000000};
            final Biome[] desired = {biome};
            if (biomes.isEmpty()) {
                // Biomes was empty. Try to get one with the correct category
                if (!biomeCategories.contains(Biome.getBiomeCategory(Holder.direct(desired[0])))) {
                    biomeRegistry.stream().forEach(b -> {
                        if (biomeCategories.contains(Biome.getBiomeCategory(Holder.direct(b)))) {
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
                    if (biomeCategories.isEmpty() || biomeCategories.contains(Biome.getBiomeCategory(b))) {
                        float dist = distance(b.value(), biome);
                        if (dist < minDist[0]) {
                            desired[0] = b.value();
                            minDist[0] = dist;
                        }
                    }
                }
            }
            return biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, desired[0].getRegistryName()));
        });
    }

    private static float distance(Biome biome1, Biome biome2) {
        float d1 = Biome.getBiomeCategory(Holder.direct(biome1)) == Biome.getBiomeCategory(Holder.direct(biome2)) ? 0 : 1;
        float d2 = Math.abs(biome1.getBaseTemperature() - biome2.getBaseTemperature());
        float d3 = Math.abs(biome1.getDownfall() - biome2.getDownfall());
        float d4 = biome1.isHumid() == biome2.isHumid() ? 0 : 1;
        return d1 + d2*d2 + d3*d3 + d4;
    }

    private List<Holder<Biome>> getBiomes(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        List<ResourceLocation> biomes = settings.getCompiledDescriptor().getBiomes();
        return biomes.stream().map(biomeRegistry::get).map(b -> biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, b.getRegistryName()))).collect(Collectors.toList());
    }

    private Set<Biome.BiomeCategory> getBiomeCategories(DimensionSettings settings) {
        Set<Biome.BiomeCategory> categories = settings.getCompiledDescriptor().getBiomeCategories();
        if (categories.size() == 1 && categories.iterator().next().equals(Biome.BiomeCategory.NONE)) {
            return Collections.emptySet();
        } else {
            return categories;
        }
    }

    public Registry<Biome> getBiomeRegistry() {
        return biomeRegistry;
    }

    @Nonnull
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public BiomeSource withSeed(long seed) {
        return new RFTBiomeProvider(getBiomeRegistry(), settings);
    }

    @Override
    public Set<Holder<Biome>> possibleBiomes() {
        if (defaultBiomes) {
            return multiNoiseBiomeSource.possibleBiomes();
        } else {
            return new HashSet<>(multiNoiseBiomeSource.possibleBiomes());
        }
    }

    @Override
    public List<StepFeatureData> featuresPerStep() {
        return multiNoiseBiomeSource.featuresPerStep();
    }

//    @Override
//    public void addMultinoiseDebugInfo(List<String> list, BlockPos pos, Climate.Sampler climate) {
//        multiNoiseBiomeSource.addMultinoiseDebugInfo(list, pos, climate);
//    }

//    @Nullable
//    @Override
//    public BlockPos findBiomeHorizontal(int x, int y, int z, int radius, Predicate<Biome> predicate, Random random, Climate.Sampler climate) {
//        return multiNoiseBiomeSource.findBiomeHorizontal(x, y, z, radius, predicate, random, climate);
//    }

//    @Nullable
//    @Override
//    public BlockPos findBiomeHorizontal(int x, int y, int z, int p_186699_, int p_186700_, Predicate<Biome> predicate, Random random, boolean p_186703_, Climate.Sampler climate) {
//        return multiNoiseBiomeSource.findBiomeHorizontal(x, y, z, p_186699_, p_186700_, predicate, random, p_186703_, climate);
//    }

//    @Override
//    public Set<Biome> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler climate) {
//        if (defaultBiomes) {
//            return multiNoiseBiomeSource.getBiomesWithin(x, y, z, radius, climate);
//        } else {
//            return multiNoiseBiomeSource.getBiomesWithin(x, y, z, radius, climate).stream()
//                    .map(this::getMappedBiome)
//                    .collect(Collectors.toSet());
//        }
//    }

    private void getBiome1And2() {
        if (biome1 == null) {
            if (biomes.isEmpty()) {
                // Try to get from categories
                List<Biome> list = biomeRegistry.stream().filter(b -> biomeCategories.contains(Biome.getBiomeCategory(Holder.direct(b)))).collect(Collectors.toList());
                biome1 = biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, list.get(0).getRegistryName()));
                if (list.size() > 1) {
                    biome2 = biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, list.get(1).getRegistryName()));
                } else {
                    biome2 = biome1;
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
            biome2 = getMappedBiome(biome2.value());
        }
    }

    @Override
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
        getBiome1And2();
        return biome1;
    }

    private Holder<Biome> getCheckerBiome(int x, int z) {
        getBiome1And2();
        if (((x >>3)+(z >>3))%2 == 0) {
            return biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biome1.value().getRegistryName()));
        } else {
            return biomeRegistry.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, biome2.value().getRegistryName()));
        }
    }
}
