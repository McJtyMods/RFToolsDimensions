package mcjty.rftoolsdim.dimension;

import com.google.gson.*;
import mcjty.rftoolsdim.dimension.biomes.BiomeDescriptor;
import mcjty.rftoolsdim.dimension.features.FeatureDescriptor;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a dimension. It can be used to generate DimensionInformation objects
 *
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

    private TerrainType terrainType;
    private List<FeatureDescriptor> features = new ArrayList<>();
    private List<ResourceLocation> baseBlocks = new ArrayList<>();
    private BiomeDescriptor biomeDescriptor = BiomeDescriptor.DEFAULT;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public void read(String json) {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(json);
        JsonObject object = root.getAsJsonObject();

        read(object);
    }

    public void read(JsonObject object) {
        readTerrainType(object);
        readFeatures(object);
        readBaseBlocks(object);
        readBiomes(object);
    }

    private void readBiomes(JsonObject object) {
        if (object.has("biomes")) {
            JsonObject biomes = object.get("biomes").getAsJsonObject();
            biomeDescriptor = BiomeDescriptor.readFromJson(biomes);
        }
    }

    private void readTerrainType(JsonObject object) {
        if (object.has("terrain")) {
            String name = object.get("terrain").getAsString();
            terrainType = TerrainType.byName(name);
            if (terrainType == null) {
                throw new RuntimeException("Bad terrain type: " + name + "!");
            }
        } else {
            terrainType = TerrainType.NORMAL;
        }
    }

    private void readBaseBlocks(JsonObject object) {
        baseBlocks.clear();
        if (object.has("baseblocks")) {
            for (JsonElement element : object.get("baseblocks").getAsJsonArray()) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    baseBlocks.add(new ResourceLocation(element.getAsString()));
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
                if (element.isJsonObject()) {
                    JsonObject featureEl = element.getAsJsonObject();
                    String id = featureEl.get("id").getAsString();
                    features.add(new FeatureDescriptor(id, featureEl.get("config")));
                } else {
                    throw new RuntimeException("Illegal feature descriptor!");
                }
            }
        }
    }

    public String write() {
        JsonObject root = new JsonObject();
        writeTerrainType(root);
        writeFeatures(root);
        writeBaseBlocks(root);
        writeBiomes(root);
        return GSON.toJson(root);
    }

    private void writeBiomes(JsonObject root) {
        root.add("biomes", biomeDescriptor.writeToJson());
    }

    private void writeTerrainType(JsonObject root) {
        root.addProperty("terrain", terrainType.getName());
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
        for (FeatureDescriptor feature : features) {
            JsonObject featureEl = new JsonObject();
            featureEl.addProperty("id", feature.getId());
            if (feature.getConfigElement() != null) {
                featureEl.add("config", feature.getConfigElement());
            }
            array.add(featureEl);
        }
        root.add("features", array);
    }

    public List<FeatureDescriptor> getFeatures() {
        return features;
    }

    public List<ResourceLocation> getBaseBlocks() {
        return baseBlocks;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public BiomeDescriptor getBiomeDescriptor() {
        return biomeDescriptor;
    }
}
