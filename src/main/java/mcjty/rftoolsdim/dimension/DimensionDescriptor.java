package mcjty.rftoolsdim.dimension;

import com.google.gson.*;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * A biome has:
 *  - Carvers: "cave", "hell_cave", "canyon", "underwater_canyon", "underwater_cave"
 *  - Features (structure, lake, ore, vegetation, ...)
 *      - Using a placement
 *  - Surface builder
 *
 * Biome provider
 *
 * Chunk generator
 */
public class DimensionDescriptor {

    private List<ResourceLocation> features = new ArrayList<>();
    private List<ResourceLocation> baseBlocks = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public void read(String json) {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(json);
        JsonObject object = root.getAsJsonObject();

        features.clear();
        if (object.has("features")) {
            readFeatures(object.get("features").getAsJsonArray());
        }

        baseBlocks.clear();
        if (object.has("baseblocks")) {
            readBaseBlocks(object.get("baseblocks").getAsJsonArray());
        }
    }

    private void readBaseBlocks(JsonArray array) {
        for (JsonElement element : array) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                features.add(new ResourceLocation(element.getAsString()));
            } else {
                throw new RuntimeException("Illegal base block!");
            }
        }
    }

    private void readFeatures(JsonArray array) {
        for (JsonElement element : array) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                features.add(new ResourceLocation(element.getAsString()));
            } else {
                throw new RuntimeException("Illegal feature descriptor!");
            }
        }
    }

    public List<ResourceLocation> getFeatures() {
        return features;
    }

    public List<ResourceLocation> getBaseBlocks() {
        return baseBlocks;
    }
}
