package mcjty.rftoolsdim.dimension.descriptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BiomeDescriptor {

    private final String type;
    private final List<String> temperatures = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();

    public static final BiomeDescriptor DEFAULT = new BiomeDescriptor("default");

    public BiomeDescriptor(String type) {
        this.type = type;
    }

    public static BiomeDescriptor readFromJson(JsonObject biomes) {
        String provider = "default";
        if (biomes.has("type")) {
            provider = biomes.get("type").getAsJsonPrimitive().getAsString();
        }
        BiomeDescriptor descriptor = new BiomeDescriptor(provider);

        if (biomes.has("temperature")) {
            for (JsonElement element : biomes.get("temperature").getAsJsonArray()) {
                String temperature = element.getAsJsonPrimitive().getAsString();
                descriptor.addTemperature(temperature);
            }
        }
        if (biomes.has("category")) {
            for (JsonElement element : biomes.get("category").getAsJsonArray()) {
                String category = element.getAsJsonPrimitive().getAsString();
                descriptor.addCategory(category);
            }
        }
        return descriptor;
    }

    public JsonObject writeToJson() {
        JsonObject biomes = new JsonObject();

        if (getType() != null) {
            biomes.addProperty("type", getType());
        }

        if (!getTemperatures().isEmpty()) {
            JsonArray array = new JsonArray();
            for (String temperature : getTemperatures()) {
                array.add(temperature);
            }
            biomes.add("temperature", array);
        }
        if (!getCategories().isEmpty()) {
            JsonArray array = new JsonArray();
            for (String category : getCategories()) {
                array.add(category);
            }
            biomes.add("category", array);
        }
        return biomes;
    }

    public void addTemperature(String temperature) {
        temperatures.add(temperature);
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public String getType() {
        return type;
    }

    public Collection<String> getTemperatures() {
        return temperatures;
    }

    public Collection<String> getCategories() {
        return categories;
    }
}
