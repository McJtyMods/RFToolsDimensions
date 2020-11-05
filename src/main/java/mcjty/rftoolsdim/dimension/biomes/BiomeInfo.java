package mcjty.rftoolsdim.dimension.biomes;

import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class BiomeInfo {

    private final BiomeLayoutType biomeLayoutType;
    private final Set<Biome.Climate> tempCategory = new HashSet<>();
    private final Set<Biome.Category> biomeCategory = new HashSet<>();

    private BiomeInfo(BiomeLayoutType biomeLayoutType) {
        this.biomeLayoutType = biomeLayoutType;
    }

    @Nonnull
    public Set<Biome.Climate> getTempCategory() {
        return tempCategory;
    }

    @Nonnull
    public Set<Biome.Category> getBiomeCategory() {
        return biomeCategory;
    }

    @Nonnull
    public BiomeLayoutType getBiomeLayoutType() {
        return biomeLayoutType;
    }

    public static BiomeInfo createFrom(BiomeDescriptor descriptor) {
        BiomeLayoutType biomeLayoutType = BiomeLayoutType.DEFAULT;
        if (descriptor.getType() != null) {
            biomeLayoutType = BiomeLayoutType.valueOf(descriptor.getType().toUpperCase());
        }
        BiomeInfo info = new BiomeInfo(biomeLayoutType);

        // @todo 1.16
//        for (String temperature : descriptor.getTemperatures()) {
//            Biome.Climate tempCategory = Biome.Climate.valueOf(temperature.toUpperCase());
//            info.tempCategory.add(tempCategory);
//        }

        for (String category : descriptor.getCategories()) {
            Biome.Category cat = Biome.Category.valueOf(category.toUpperCase());
            info.biomeCategory.add(cat);
        }

        return info;
    }
}