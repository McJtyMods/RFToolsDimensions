package mcjty.rftoolsdim.dimensions.dimlets;

import java.util.HashMap;
import java.util.Map;

import mcjty.rftoolsdim.dimensions.types.WeatherType;

public class WeatherRegistry {

    private static final Map<DimletKey, WeatherType> weatherTypeMap = new HashMap<>();

    public static void registerWeather(DimletKey key, WeatherType descriptor) {
        weatherTypeMap.put(key, descriptor);
    }

    public static WeatherType getWeatherType(DimletKey key) {
        return weatherTypeMap.get(key);
    }
}
