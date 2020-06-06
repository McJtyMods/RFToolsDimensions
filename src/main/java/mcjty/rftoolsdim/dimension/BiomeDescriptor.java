package mcjty.rftoolsdim.dimension;

public class BiomeDescriptor {

    private final String provider;
    private final String temperature;

    public static final BiomeDescriptor DEFAULT = new BiomeDescriptor("default", null);

    public BiomeDescriptor(String provider, String temperature) {
        this.provider = provider;
        this.temperature = temperature;
    }

    public String getProvider() {
        return provider;
    }

    public String getTemperature() {
        return temperature;
    }
}
