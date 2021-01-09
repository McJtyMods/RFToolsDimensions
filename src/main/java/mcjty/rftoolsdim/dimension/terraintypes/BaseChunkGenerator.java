package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public abstract class BaseChunkGenerator extends ChunkGenerator {

    protected final Settings settings;

    public BaseChunkGenerator(Registry<Biome> registry, Settings settings) {
        super(new RFTBiomeProvider(registry), new DimensionStructuresSettings(false));
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((RFTBiomeProvider)biomeProvider).getBiomeRegistry();
    }


    static class Settings {
        private final int baseHeight;
        private final float verticalVariance;
        private final float horizontalVariance;

        public Settings(int baseHeight, float verticalVariance, float horizontalVariance) {
            this.baseHeight = baseHeight;
            this.verticalVariance = verticalVariance;
            this.horizontalVariance = horizontalVariance;
        }

        public float getVerticalVariance() {
            return verticalVariance;
        }

        public int getBaseHeight() {
            return baseHeight;
        }

        public float getHorizontalVariance() {
            return horizontalVariance;
        }
    }
}
