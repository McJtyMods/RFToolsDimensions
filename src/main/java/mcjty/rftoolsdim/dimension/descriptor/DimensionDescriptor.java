package mcjty.rftoolsdim.dimension.descriptor;

import com.google.gson.*;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private List<DimletKey> dimlets = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Gson GSON_COMPACT = new GsonBuilder().disableHtmlEscaping().create();

    public List<DimletKey> getDimlets() {
        return dimlets;
    }

    public void read(String json) {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(json);
        JsonArray object = root.getAsJsonArray();

        read(object);
    }

    public void read(JsonArray object) {
        dimlets.clear();
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
            DimletKey dimletDescriptor = new DimletKey(dimletType, name);
            dimlets.add(dimletDescriptor);
        }
    }

    public String write() {
        JsonArray root = new JsonArray();
        for (DimletKey dimletDescriptor : dimlets) {
            JsonObject dimletJson = new JsonObject();
            dimletJson.addProperty("type", dimletDescriptor.getType().name());
            dimletJson.addProperty("name", dimletDescriptor.getKey());
            root.add(dimletJson);
        }
        return GSON.toJson(root);
    }

    // Write a more compact form of the dimension. This is stored with the dimension itself
    public String compact() {
        JsonArray root = new JsonArray();
        for (DimletKey dimletDescriptor : dimlets) {
            JsonObject dimletJson = new JsonObject();
            dimletJson.addProperty("t", dimletDescriptor.getType().getShortName());
            dimletJson.addProperty("n", dimletDescriptor.getKey());
            root.add(dimletJson);
        }
        return GSON_COMPACT.toJson(root);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionDescriptor that = (DimensionDescriptor) o;
        return Objects.equals(dimlets, that.dimlets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimlets);
    }

    public void dump(PlayerEntity player) {
        for (DimletKey dimlet : dimlets) {
            player.sendStatusMessage(
                    new StringTextComponent(dimlet.getType().name() + ": ")
                    .mergeStyle(TextFormatting.AQUA)
                    .append(new StringTextComponent(dimlet.getKey()).mergeStyle(TextFormatting.WHITE))
                    , false);
        }

    }
}
