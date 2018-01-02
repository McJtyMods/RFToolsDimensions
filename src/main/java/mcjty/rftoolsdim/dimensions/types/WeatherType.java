package mcjty.rftoolsdim.dimensions.types;

public enum WeatherType {
    WEATHER_DEFAULT(-1.0f, -1.0f),
    WEATHER_NORAIN(0.0f, null),
    WEATHER_LIGHTRAIN(0.5f, null),
    WEATHER_HARDRAIN(1.0f, null),
    WEATHER_NOTHUNDER(null, 0.0f),
    WEATHER_LIGHTTHUNDER(null, 0.5f),
    WEATHER_HARDTHUNDER(null, 1.0f);

    // null: use the previous value
    // -1: use default weather cycle
    // 0: force none
    // 0.5: force light
    // 1: force heavy
    private final Float rainStrength;
    private final Float thunderStrength;

    WeatherType(Float rainStrength, Float thunderStrength) {
        this.rainStrength = rainStrength;
        this.thunderStrength = thunderStrength;
    }

    public Float getRainStrength() {
        return rainStrength;
    }

    public Float getThunderStrength() {
        return thunderStrength;
    }
}
