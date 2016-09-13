package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.dimensions.description.WeatherDescriptor;

import java.util.HashMap;
import java.util.Map;

public class WeatherRegistry {

    private static final Map<DimletKey, WeatherDescriptor> weatherDescriptorMap = new HashMap<>();

    public static void registerWeather(DimletKey key, WeatherDescriptor descriptor) {
        weatherDescriptorMap.put(key, descriptor);
    }

    public static WeatherDescriptor getWeatherDescriptor(DimletKey key) {
        return weatherDescriptorMap.get(key);
    }
}
