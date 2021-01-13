package mcjty.rftoolsdim.dimension.terraintypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public abstract class BaseChunkGenerator extends ChunkGenerator {

    static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("dimlets").forGetter(Settings::getDimlets)
            ).apply(instance, Settings::new));

    protected final Settings settings;
    private CompiledDescriptor compiledDescriptor = null;

    public BaseChunkGenerator(Registry<Biome> registry, Settings settings) {
        super(new RFTBiomeProvider(registry), new DimensionStructuresSettings(false));
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public CompiledDescriptor getCompiledDescriptor() {
        if (compiledDescriptor == null) {
            DimensionDescriptor descriptor = new DimensionDescriptor();
            descriptor.read(settings.getDimlets());
            compiledDescriptor = new CompiledDescriptor(descriptor);
        }
        return compiledDescriptor;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((RFTBiomeProvider)biomeProvider).getBiomeRegistry();
    }


    public static class Settings {
        private final String dimlets;

        public Settings(String dimlets) {
            this.dimlets = dimlets;
        }

        public String getDimlets() {
            return dimlets;
        }
    }
}
