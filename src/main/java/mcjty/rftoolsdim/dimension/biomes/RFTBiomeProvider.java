package mcjty.rftoolsdim.dimension.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.data.DimensionSettings;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static mcjty.rftoolsdim.dimension.data.DimensionSettings.SETTINGS_CODEC;

public class RFTBiomeProvider extends BiomeProvider {

//    public static final Codec<RFTBiomeProvider> CODEC = RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY)
//            .xmap(RFTBiomeProvider::new, RFTBiomeProvider::getBiomeRegistry).codec();

    public static final Codec<RFTBiomeProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(RFTBiomeProvider::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(RFTBiomeProvider::getSettings)
            ).apply(instance, RFTBiomeProvider::new));

    private final Layer genBiomes;
    private final List<Biome> biomes;
    private final Registry<Biome> biomeRegistry;
    private final DimensionSettings settings;

    public RFTBiomeProvider(Registry<Biome> biomeRegistry, DimensionSettings settings) {
        super(getBiomes(biomeRegistry, settings));
        this.settings = settings;
        this.biomeRegistry = biomeRegistry;
        biomes = getBiomes(biomeRegistry, settings);
        this.genBiomes = LayerUtil.getDefaultLayer(settings.getSeed(), false, 4, 4);
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

    @Override
    public boolean canGenerateStructure(@Nonnull Structure<?> structure) {
        return false;
    }

    @Nonnull
    @Override
    protected Codec<? extends BiomeProvider> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public BiomeProvider withSeed(long seed) {
        return new RFTBiomeProvider(getBiomeRegistry(), settings);
    }

    @Nonnull
    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        switch (settings.getCompiledDescriptor().getBiomeControllerType()) {
            case DEFAULT:
                return this.genBiomes.get(this.biomeRegistry, x, z);
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
