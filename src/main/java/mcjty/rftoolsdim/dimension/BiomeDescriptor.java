package mcjty.rftoolsdim.dimension;

public class BiomeDescriptor {

    private final String provider;
    private final String temperature;
    private final String category;

    public static final BiomeDescriptor DEFAULT = new BiomeDescriptor("default", null, null);

    public BiomeDescriptor(String provider, String temperature, String category) {
        this.provider = provider;
        this.temperature = temperature;
        this.category = category;
    }

    public String getProvider() {
        return provider;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCategory() {
        return category;
    }
}
