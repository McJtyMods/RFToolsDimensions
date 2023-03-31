package mcjty.rftoolsdim.dimension.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mcjty.rftoolsdim.dimension.data.DimensionSettings.SETTINGS_CODEC;

public class RFTBiomeProvider extends BiomeSource {

    public static final Codec<RFTBiomeProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryOps.retrieveRegistryLookup(Registries.WORLD_PRESET).forGetter(RFTBiomeProvider::getWorldPresetLookup),
                    RegistryOps.retrieveRegistryLookup(Registries.BIOME).forGetter(RFTBiomeProvider::getBiomeLookup),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(RFTBiomeProvider::getSettings)
            ).apply(instance, RFTBiomeProvider::new));

    private final List<Holder<Biome>> biomes;
    private final Set<TagKey<Biome>> biomeCategories;
    private final Map<ResourceLocation, Holder<Biome>> biomeMapping = new HashMap<>();
    private final HolderLookup.RegistryLookup<WorldPreset> worldPresetLookup;
    private final HolderLookup.RegistryLookup<Biome> biomeLookup;
    private final DimensionSettings settings;
    private final MultiNoiseBiomeSource multiNoiseBiomeSource;
    private final boolean defaultBiomes;
    private Holder<Biome> biome1 = null;   // For single and checker
    private Holder<Biome> biome2 = null;   // For checker

    public RFTBiomeProvider(HolderLookup.RegistryLookup<WorldPreset> worldPresetLookup, HolderLookup.RegistryLookup<Biome> biomeLookup, DimensionSettings settings) {
        super();
        this.settings = settings;
        this.biomeLookup = biomeLookup;
        this.worldPresetLookup = worldPresetLookup;
        // @todo 1.19.4 is this right?
        Optional<Holder.Reference<WorldPreset>> worldPreset = worldPresetLookup.get(WorldPresets.NORMAL);
        multiNoiseBiomeSource = (MultiNoiseBiomeSource) worldPreset.get().get().overworld().get().generator().getBiomeSource();
//        multiNoiseBiomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(biomeLookup, true);
        biomes = getBiomes(biomeLookup, settings);
        biomeCategories = getBiomeCategories(settings);

        defaultBiomes = biomes.isEmpty() && biomeCategories.isEmpty();
        biomeLookup.listElements().forEach(this::getMappedBiome);
    }

    public HolderLookup.RegistryLookup<WorldPreset> getWorldPresetLookup() {
        return worldPresetLookup;
    }

    public DimensionSettings getSettings() {
        return settings;
    }

    private static List<Holder<Biome>> getDefaultBiomes(HolderLookup.RegistryLookup<Biome> biomeLookup, DimensionSettings settings) {
        List<ResourceLocation> biomes = settings.getCompiledDescriptor().getBiomes();
        if (biomes.isEmpty()) {
            return biomeLookup.listElements().collect(Collectors.toList());
        }
        return biomes.stream().map(rl -> biomeLookup.get(ResourceKey.create(Registries.BIOME, rl))).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return getDefaultBiomes(biomeLookup, settings).stream();
    }

//    @Override
//    @Nonnull
//    public Set<Holder<Biome>> possibleBiomes() {
//        return biomeLookup.listElements().map(this::getMappedBiome).collect(Collectors.toSet());
//        if (defaultBiomes) {
//            return multiNoiseBiomeSource.possibleBiomes();
//        } else {
//            return new HashSet<>(multiNoiseBiomeSource.possibleBiomes());
//        }
//    }


    private boolean isCategoryMatching(Holder<Biome> biome) {
        if (biomeCategories.isEmpty()) {
            return true;
        }

        return biomeLookup.getOrThrow(biome.unwrapKey().get()).tags().filter(biomeCategories::contains).findAny().isPresent();
    }

    private Holder<Biome> getMappedBiome(Holder<Biome> biome) {
        if (defaultBiomes) {
            Optional<ResourceKey<Biome>> rk = biome.unwrapKey();
            return biomeLookup.get(rk.get()).get();
        }
        return biomeMapping.computeIfAbsent(biome.unwrapKey().get().location(), resourceLocation -> {
            List<Holder<Biome>> biomes = getBiomes(biomeLookup, settings);
            final float[] minDist = {1000000000};
            final Holder<?>[] desired = {biome};
            if (biomes.isEmpty()) {
                // Biomes was empty. Try to get one with the correct category
                // @todo why does the first one have to fail???
                if (!isCategoryMatching((Holder<Biome>)desired[0])) {
                    biomeLookup.listElements().forEach(b -> {
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
                    if (biomeCategories.isEmpty() || isCategoryMatching(b)) {
                        float dist = distance(b, biome);
                        if (dist < minDist[0]) {
                            desired[0] = b;
                            minDist[0] = dist;
                        }
                    }
                }
            }
            return (Holder<Biome>) desired[0];
        });
    }

    private float distance(Holder<Biome> biome1, Holder<Biome> biome2) {
        var tags1 = biome1.tags().collect(Collectors.toSet());
        var tags2 = biome2.tags().collect(Collectors.toSet());
        tags1.removeAll(tags2);
        tags1 = biome1.tags().collect(Collectors.toSet());
        tags2.removeAll(tags1);
        float d1 = Math.max(tags1.size(), tags2.size());    // Use the number of differences in tags as a measure
        float d2 = Math.abs(biome1.value().getBaseTemperature() - biome2.value().getBaseTemperature());
//        float d3 = Math.abs(biome1.value().getDownfall() - biome2.value().getDownfall());
//        float d4 = biome1.value().isHumid() == biome2.value().isHumid() ? 0 : 1;
        return d1 + d2 * d2;// @todo 1.19.4 + d3 * d3 + d4;
    }

    private List<Holder<Biome>> getBiomes(HolderLookup.RegistryLookup<Biome> holderLookup, DimensionSettings settings) {
        List<ResourceLocation> biomes = settings.getCompiledDescriptor().getBiomes();
        return biomes.stream().map(rl -> biomeLookup.get(ResourceKey.create(Registries.BIOME, rl))).map(Optional::get).collect(Collectors.toList());
    }

    private Set<TagKey<Biome>> getBiomeCategories(DimensionSettings settings) {
        Set<TagKey<Biome>> categories = settings.getCompiledDescriptor().getBiomeCategories();
        return categories;
    }

    public HolderLookup.RegistryLookup<Biome> getBiomeLookup() {
        return biomeLookup;
    }

    @Nonnull
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

//    @Nonnull
//    @Override
//    public BiomeSource withSeed(long seed) {
//        return new RFTBiomeProvider(getBiomeRegistry(), settings);
//    }


//    @Override
//    public List<StepFeatureData> featuresPerStep() {
//        return multiNoiseBiomeSource.featuresPerStep();
//    }

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
                List<Holder<Biome>> list = biomeLookup.listElements().filter(this::isCategoryMatching).collect(Collectors.toList());
                if (list.isEmpty()) {
                    // Safety, this is needed in case the category doesn't contain any biomes
                    biome1 = biome2 = biomeLookup.get(Biomes.PLAINS).get();
                } else {
                    biome1 = list.get(0);
                    if (list.size() > 1) {
                        biome2 = list.get(1);
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
            biome1 = getMappedBiome(biome1);
            if (biome1 == null) {
                // Safety. Shouldn't be possible
                biome1 = biomeLookup.get(Biomes.PLAINS).get();
            }
            biome2 = getMappedBiome(biome2);
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
            return getMappedBiome(multiNoiseBiomeSource.getNoiseBiome(x, y, z, climate));
        }
    }

    private Holder<Biome> getSingleBiome() {
        getBiome1And2();
        return biome1;
    }

    private Holder<Biome> getCheckerBiome(int x, int z) {
        getBiome1And2();
        if (((x >> 3) + (z >> 3)) % 2 == 0) {
            return biome1;
        } else {
            return biome2;
        }
    }
}
