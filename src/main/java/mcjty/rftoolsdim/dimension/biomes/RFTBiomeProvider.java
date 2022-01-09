package mcjty.rftoolsdim.dimension.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static mcjty.rftoolsdim.dimension.data.DimensionSettings.SETTINGS_CODEC;

public class RFTBiomeProvider extends BiomeSource {

//    public static final Codec<RFTBiomeProvider> CODEC = RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY)
//            .xmap(RFTBiomeProvider::new, RFTBiomeProvider::getBiomeRegistry).codec();

    public static final Codec<RFTBiomeProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(RFTBiomeProvider::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(RFTBiomeProvider::getSettings)
            ).apply(instance, RFTBiomeProvider::new));

//    private final Layer genBiomes;
    private final List<Biome> biomes;
    private final Registry<Biome> biomeRegistry;
    private final DimensionSettings settings;

    public RFTBiomeProvider(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        super(getBiomes(biomeRegistry, settings));
        this.settings = settings;
        this.biomeRegistry = biomeRegistry;
        biomes = getBiomes(biomeRegistry, settings);
//        this.genBiomes = Layers.getDefaultLayer(settings.getSeed(), false, 4, 4);
    }



    public DimensionSettings getSettings() {
        return settings;
    }

    private static List<Biome> getBiomes(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        List<Biome> biomes;
        biomes = settings.getCompiledDescriptor().getBiomes()
                .stream().map(biomeRegistry::get).collect(Collectors.toList());
        if (biomes.isEmpty()) {
            biomes.add(biomeRegistry.get(Biomes.PLAINS.location()));
        }
        return biomes;
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
    public Biome getNoiseBiome(int x, int y, int z, Climate.Sampler climate) {
        switch (settings.getCompiledDescriptor().getBiomeControllerType()) {
            case DEFAULT:
                // @todo 1.18
//                return this.genBiomes.get(this.biomeRegistry, x, z);
                return biomes.get(0);
            case CHECKER:
                if ((x+y)%2 == 0 || biomes.size() <= 1) {
                    return biomes.get(0);
                } else {
                    return biomes.get(1);
                }
            case SINGLE:
                return biomes.get(0);
        }
        return null;
    }
}
