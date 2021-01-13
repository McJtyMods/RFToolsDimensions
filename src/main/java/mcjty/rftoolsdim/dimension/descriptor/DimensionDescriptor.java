package mcjty.rftoolsdim.dimension.descriptor;

import com.google.gson.*;
import mcjty.rftoolsdim.dimlets.DimletType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a dimension by its list of dimlets. It can be used to generate DimensionInformation objects
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

    private List<DimletDescriptor> dimletDescriptors = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Gson GSON_COMPACT = new GsonBuilder().disableHtmlEscaping().create();

    public List<DimletDescriptor> getDimletDescriptors() {
        return dimletDescriptors;
    }

    public void read(String json) {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(json);
        JsonArray object = root.getAsJsonArray();

        read(object);
    }

    public void read(JsonArray object) {
        dimletDescriptors.clear();
        for (JsonElement element : object) {
            JsonObject dimletJson = element.getAsJsonObject();
            String type;
            if (dimletJson.has("type")) {
                type = dimletJson.getAsJsonPrimitive("type").getAsString();
            } else {
                type = dimletJson.getAsJsonPrimitive("t").getAsString();
            }
            DimletType dimletType = DimletType.byName(type);
            String name;
            if (dimletJson.has("name")) {
                name = dimletJson.get("name").getAsString();
            } else {
                name = dimletJson.get("n").getAsString();
            }
            DimletDescriptor dimletDescriptor = new DimletDescriptor(dimletType, name);
            dimletDescriptors.add(dimletDescriptor);
        }
    }

    public String write() {
        JsonArray root = new JsonArray();
        for (DimletDescriptor dimletDescriptor : dimletDescriptors) {
            JsonObject dimletJson = new JsonObject();
            dimletJson.addProperty("type", dimletDescriptor.getType().name());
            dimletJson.addProperty("name", dimletDescriptor.getName());
            root.add(dimletJson);
        }
        return GSON.toJson(root);
    }

    // Write a more compact form of the dimension. This is stored with the dimension itself
    public String compact() {
        JsonArray root = new JsonArray();
        for (DimletDescriptor dimletDescriptor : dimletDescriptors) {
            JsonObject dimletJson = new JsonObject();
            dimletJson.addProperty("t", dimletDescriptor.getType().getShortName());
            dimletJson.addProperty("n", dimletDescriptor.getName());
            root.add(dimletJson);
        }
        return GSON_COMPACT.toJson(root);

    }
}
