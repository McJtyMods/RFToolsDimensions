package mcjty.rftoolsdim.dimension.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static mcjty.rftoolsdim.dimension.data.DimensionSettings.SETTINGS_CODEC;

public class RFTBiomeProvider extends BiomeSource {

    public static final Codec<RFTBiomeProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(RFTBiomeProvider::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(RFTBiomeProvider::getSettings)
            ).apply(instance, RFTBiomeProvider::new));

    private final List<Biome> biomes;
    private final Set<Biome.BiomeCategory> biomeCategories;
    private final Map<ResourceLocation, Biome> biomeMapping = new HashMap<>();
    private final Registry<Biome> biomeRegistry;
    private final DimensionSettings settings;
    private final MultiNoiseBiomeSource multiNoiseBiomeSource;
    private final boolean defaultBiomes;
    private Biome biome1 = null;   // For single and checker
    private Biome biome2 = null;   // For checker

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

    private Biome getMappedBiome(Biome biome) {
        if (defaultBiomes) {
            return biome;
        }
        return biomeMapping.computeIfAbsent(biome.getRegistryName(), resourceLocation -> {
            List<Biome> biomes = getBiomes(biomeRegistry, settings);
            final float[] minDist = {1000000000};
            final Biome[] desired = {biome};
            if (biomes.isEmpty()) {
                // Biomes was empty. Try to get one with the correct category
                if (!biomeCategories.contains(desired[0].getBiomeCategory())) {
                    biomeRegistry.stream().forEach(b -> {
                        if (biomeCategories.contains(b.getBiomeCategory())) {
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
                for (Biome b : biomes) {
                    if (biomeCategories.isEmpty() || biomeCategories.contains(b.getBiomeCategory())) {
                        float dist = distance(b, biome);
                        if (dist < minDist[0]) {
                            desired[0] = b;
                            minDist[0] = dist;
                        }
                    }
                }
            }
            return desired[0];
        });
    }

    private static float distance(Biome biome1, Biome biome2) {
        float d1 = biome1.getBiomeCategory() == biome2.getBiomeCategory() ? 0 : 1;
        float d2 = Math.abs(biome1.getBaseTemperature() - biome2.getBaseTemperature());
        float d3 = Math.abs(biome1.getDownfall() - biome2.getDownfall());
        float d4 = biome1.isHumid() == biome2.isHumid() ? 0 : 1;
        return d1 + d2*d2 + d3*d3 + d4;
    }

    private List<Biome> getBiomes(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        List<ResourceLocation> biomes = settings.getCompiledDescriptor().getBiomes();
        return biomes.stream().map(biomeRegistry::get).collect(Collectors.toList());
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
    public Set<Biome> possibleBiomes() {
        if (defaultBiomes) {
            return multiNoiseBiomeSource.possibleBiomes();
        } else {
            return multiNoiseBiomeSource.possibleBiomes().stream()
                    .map(this::getMappedBiome)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public List<StepFeatureData> featuresPerStep() {
        return multiNoiseBiomeSource.featuresPerStep();
    }

    @Override
    public void addMultinoiseDebugInfo(List<String> list, BlockPos pos, Climate.Sampler climate) {
        multiNoiseBiomeSource.addMultinoiseDebugInfo(list, pos, climate);
    }

    @Nullable
    @Override
    public BlockPos findBiomeHorizontal(int x, int y, int z, int radius, Predicate<Biome> predicate, Random random, Climate.Sampler climate) {
        return multiNoiseBiomeSource.findBiomeHorizontal(x, y, z, radius, predicate, random, climate);
    }

    @Nullable
    @Override
    public BlockPos findBiomeHorizontal(int x, int y, int z, int p_186699_, int p_186700_, Predicate<Biome> predicate, Random random, boolean p_186703_, Climate.Sampler climate) {
        return multiNoiseBiomeSource.findBiomeHorizontal(x, y, z, p_186699_, p_186700_, predicate, random, p_186703_, climate);
    }

    @Override
    public Set<Biome> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler climate) {
        if (defaultBiomes) {
            return multiNoiseBiomeSource.getBiomesWithin(x, y, z, radius, climate);
        } else {
            return multiNoiseBiomeSource.getBiomesWithin(x, y, z, radius, climate).stream()
                    .map(this::getMappedBiome)
                    .collect(Collectors.toSet());
        }
    }

    private void getBiome1And2() {
        if (biome1 == null) {
            if (biomes.isEmpty()) {
                // Try to get from categories
                List<Biome> list = biomeRegistry.stream().filter(b -> biomeCategories.contains(b.getBiomeCategory())).collect(Collectors.toList());
                biome1 = list.get(0);
                if (list.size() > 1) {
                    biome2 = list.get(1);
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
            biome1 = getMappedBiome(biome1);
            biome2 = getMappedBiome(biome2);
        }
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z, Climate.Sampler climate) {
        switch (settings.getCompiledDescriptor().getBiomeControllerType()) {
            case CHECKER -> {
                getBiome1And2();
                if ((x+y)%2 == 0) {
                    return biome1;
                } else {
                    return biome2;
                }
            }
            case SINGLE -> {
                getBiome1And2();
                return biome1;
            }
            default -> {
                if (defaultBiomes) {
                    return multiNoiseBiomeSource.getNoiseBiome(x, y, z, climate);
                } else {
                    return getMappedBiome(multiNoiseBiomeSource.getNoiseBiome(x, y, z, climate));
                }
            }
        }
    }
}
