package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.function.Supplier;

public class RFToolsChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<RFToolsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(RegistryLookupCodec.create(
                    Registry.NOISE_REGISTRY).forGetter((ins) -> ins.noises),
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter((ins) -> ins.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter((ins) -> ins.seed),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((ins) -> ins.settings))
            .apply(instance, instance.stable(RFToolsChunkGenerator::new)));

    // Mirror because the one in NoiseBasedChunkGenerator is private
    private final Registry<NormalNoise.NoiseParameters> noises;

    public RFToolsChunkGenerator(Registry<NormalNoise.NoiseParameters> noiseRegistry, BiomeSource biomeSource, long seed,
                                 Supplier<NoiseGeneratorSettings> settingsSupplier) {
        super(noiseRegistry, biomeSource, seed, settingsSupplier);
        this.noises = noiseRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
}
