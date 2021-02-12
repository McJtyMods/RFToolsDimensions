package mcjty.rftoolsdim.dimension.descriptor;

import com.google.gson.*;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

    private final List<DimletKey> dimlets = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Gson GSON_COMPACT = new GsonBuilder().disableHtmlEscaping().create();

    public static final DimensionDescriptor EMPTY = new DimensionDescriptor();

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

    /// Create a randomized descriptor based on this one (i.e. completing the things that are missing randomly)
    public DimensionDescriptor createRandomizedDescriptor(Random random) {
        DimensionDescriptor randomizedDescriptor = new DimensionDescriptor();

        List<DimletKey> dimlets = getDimlets();
        List<DimletKey> randomized = randomizedDescriptor.getDimlets();

        if (!hasTerrain(dimlets)) {
            DimletKey terrainDimlet = DimletDictionary.get().getRandomDimlet(DimletType.TERRAIN, random);
            if (terrainDimlet != null) {
                addBlockDimlets(randomized, random);
                addAttributeDimlets(randomized, random);
                addFluidDimlets(randomized, random, 1);
                randomized.add(terrainDimlet);
            }
        }
        if (!hasFeatures(dimlets)) {
            int cnt = random.nextInt(3);
            for (int i = 0 ; i < cnt ; i++) {
                DimletKey featureDimlet = DimletDictionary.get().getRandomDimlet(DimletType.FEATURE, random);
                if (featureDimlet != null) {
                    addBlockDimlets(randomized, random);
                    randomized.add(featureDimlet);
                }
            }
        }
        if (!hasBiomeController(dimlets)) {
            DimletKey controllerDimlet = DimletDictionary.get().getRandomDimlet(DimletType.BIOME_CONTROLLER, random);
            if (controllerDimlet != null) {
                addBiomeDimlets(randomized, random);
                randomized.add(controllerDimlet);
            }
        }
        if (!hasTimeDimlet(dimlets)) {
            DimletKey timeDimlet = DimletDictionary.get().getRandomDimlet(DimletType.TIME, random);
            if (timeDimlet != null) {
                randomized.add(timeDimlet);
            }
        }

        return randomizedDescriptor;
    }

    private void addAttributeDimlets(List<DimletKey> randomized, Random random) {
        int cnt = random.nextInt(3);
        for (int i = 0 ; i < cnt ; i++) {
            DimletKey dimlet = DimletDictionary.get().getRandomDimlet(DimletType.ATTRIBUTE, random);
            if (dimlet != null) {
                randomized.add(dimlet);
            }
        }
    }

    private void addBiomeDimlets(List<DimletKey> randomized, Random random) {
        int cnt = random.nextInt(8)+1;
        for (int i = 0 ; i < cnt ; i++) {
            DimletKey dimlet = DimletDictionary.get().getRandomDimlet(DimletType.BIOME, random);
            if (dimlet != null) {
                randomized.add(dimlet);
            }
        }
    }

    private void addFluidDimlets(List<DimletKey> randomized, Random random, int max) {
        int cnt = random.nextInt(Math.min(3, max+1));
        for (int i = 0 ; i < cnt ; i++) {
            DimletKey dimlet = DimletDictionary.get().getRandomDimlet(DimletType.FLUID, random);
            if (dimlet != null) {
                randomized.add(dimlet);
            }
        }
    }

    private void addBlockDimlets(List<DimletKey> randomized, Random random) {
        int cnt = Math.max(1, random.nextInt(6)-2);
        for (int i = 0 ; i < cnt ; i++) {
            DimletKey dimlet = DimletDictionary.get().getRandomDimlet(DimletType.BLOCK, random);
            if (dimlet != null) {
                randomized.add(dimlet);
            }
        }
    }

    private boolean hasTerrain(List<DimletKey> dimlets) {
        return dimlets.stream().anyMatch(key -> key.getType() == DimletType.TERRAIN);
    }

    private boolean hasFeatures(List<DimletKey> dimlets) {
        return dimlets.stream().anyMatch(key -> key.getType() == DimletType.FEATURE);
    }

    private boolean hasBiomeController(List<DimletKey> dimlets) {
        return dimlets.stream().anyMatch(key -> key.getType() == DimletType.BIOME_CONTROLLER);
    }

    private boolean hasTimeDimlet(List<DimletKey> dimlets) {
        return dimlets.stream().anyMatch(key -> key.getType() == DimletType.TIME);
    }

}
