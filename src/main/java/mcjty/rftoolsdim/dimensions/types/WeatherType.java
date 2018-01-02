package mcjty.rftoolsdim.dimensions.types;

public enum WeatherType {
    WEATHER_NONE(-1.0f, -1.0f),
    WEATHER_NORAIN(0.0f, -1.0f),
    WEATHER_LIGHTRAIN(0.5f, -1.0f),
    WEATHER_HARDRAIN(1.0f, -1.0f),
    WEATHER_NOTHUNDER(-1.0f, 0.0f),
    WEATHER_LIGHTTHUNDER(-1.0f, 0.5f),
    WEATHER_HARDTHUNDER(-1.0f, 1.0f);

    // -1: don't affect
    // 0: force none
    // 0.5: force light
    // 1: force heavy
    private final float rainStrength;
    private final float thunderStrength;

    WeatherType(float rainStrength, float thunderStrength) {
        this.rainStrength = rainStrength;
        this.thunderStrength = thunderStrength;
    }

    public float getRainStrength() {
        return rainStrength;
    }

    public float getThunderStrength() {
        return thunderStrength;
    }
}
