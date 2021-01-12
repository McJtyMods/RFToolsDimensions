package mcjty.rftoolsdim.dimension.descriptor;

import com.google.gson.*;
import mcjty.rftoolsdim.dimlets.DimletType;

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

    private List<DimletDescriptor> dimletDescriptors = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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
            String type = dimletJson.getAsJsonPrimitive("type").getAsString();
            DimletType dimletType = DimletType.valueOf(type.toUpperCase());
            String name = dimletJson.get("name").getAsString();
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
}
