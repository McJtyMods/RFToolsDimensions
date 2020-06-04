package mcjty.rftoolsdim.dimension.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class RFTBiomeProvider extends BiomeProvider {

    private static final List<Biome> SPAWN = Collections.singletonList(Biomes.PLAINS);

    public RFTBiomeProvider() {
        super(new HashSet<>(SPAWN));
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return Biomes.PLAINS;
    }
}
