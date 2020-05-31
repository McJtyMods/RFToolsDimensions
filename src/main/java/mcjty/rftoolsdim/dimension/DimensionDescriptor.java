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

        readFeatures(object);
        readBaseBlocks(object);
    }

    private void readBaseBlocks(JsonObject object) {
        baseBlocks.clear();
        if (object.has("baseblocks")) {
            for (JsonElement element : object.get("baseblocks").getAsJsonArray()) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    features.add(new ResourceLocation(element.getAsString()));
                } else {
                    throw new RuntimeException("Illegal base block!");
                }
            }
        }
    }

    private void readFeatures(JsonObject object) {
        features.clear();
        if (object.has("features")) {
            for (JsonElement element : object.get("features").getAsJsonArray()) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    features.add(new ResourceLocation(element.getAsString()));
                } else {
                    throw new RuntimeException("Illegal feature descriptor!");
                }
            }
        }
    }

    public String write() {
        JsonObject root = new JsonObject();
        writeFeatures(root);
        writeBaseBlocks(root);
        return GSON.toJson(root);
    }

    private void writeBaseBlocks(JsonObject root) {
        JsonArray array = new JsonArray();
        for (ResourceLocation block : baseBlocks) {
            array.add(block.toString());
        }
        root.add("baseblocks", array);
    }

    private void writeFeatures(JsonObject root) {
        JsonArray array = new JsonArray();
        for (ResourceLocation feature : features) {
            array.add(feature.toString());
        }
        root.add("features", array);
    }

    public List<ResourceLocation> getFeatures() {
        return features;
    }

    public List<ResourceLocation> getBaseBlocks() {
        return baseBlocks;
    }
}
