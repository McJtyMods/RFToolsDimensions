package mcjty.rftoolsdim.modules.dimlets.data;

import com.google.gson.*;
import mcjty.lib.varia.TagTools;
import mcjty.rftoolsdim.RFToolsDim;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.BiFunction;

public class DimletPackages {
    public static void writePackage(String filename, String modid) throws IOException {
        Path configPath = FMLPaths.CONFIGDIR.get();

        new File(configPath + File.separator + "rftoolsdim").mkdirs();

        JsonArray root = new JsonArray();
        writeStructures(root, modid);
        writeBlocks(root, modid);
        writeFluids(root, modid);
        writeBiomes(root, modid);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(root);

        File file = new File(configPath + File.separator + "rftoolsdim" + File.separator + filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        }
    }

    private static void writeBiomes(JsonArray root, String modid) {
        Set<DimletKey> dimlets = DimletDictionary.get().getDimlets();
        for (var entry : ForgeRegistries.BIOMES.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (modid.toLowerCase().equals(id.getNamespace())) {
                if (!dimlets.contains(new DimletKey(DimletType.BIOME, id.toString()))) {
                    JsonObject object = new JsonObject();
                    object.addProperty("type", DimletType.BIOME.name().toLowerCase());
                    object.addProperty("key", id.toString());
                    DimletSettings settings = DimletSettings.create(DimletRarity.COMMON, 10, 10, 1)
                            .dimlet(true)
                            .worldgen(true)
                            .build();
                    settings.buildElement(object);
                    root.add(object);
                }
            }
        }
    }

    private static void writeFluids(JsonArray root, String modid) {
        Set<DimletKey> dimlets = DimletDictionary.get().getDimlets();
        for (var entry : ForgeRegistries.FLUIDS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (modid.toLowerCase().equals(id.getNamespace())) {
                Fluid fluid = entry.getValue();
                if (fluid.defaultFluidState().createLegacyBlock().getBlock() != Blocks.AIR) {
                    if (!dimlets.contains(new DimletKey(DimletType.FLUID, id.toString()))) {
                        JsonObject object = new JsonObject();
                        object.addProperty("type", DimletType.FLUID.name().toLowerCase());
                        object.addProperty("key", id.toString());
                        DimletSettings settings = DimletSettings.create(DimletRarity.COMMON, 10, 10, 10)
                                .dimlet(true)
                                .worldgen(true)
                                .build();
                        settings.buildElement(object);
                        root.add(object);
                    }
                }
            }
        }
    }

    private static void writeBlocks(JsonArray root, String modid) {
        Set<DimletKey> dimlets = DimletDictionary.get().getDimlets();
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (modid.toLowerCase().equals(id.getNamespace())) {
                Block block = entry.getValue();
                boolean hasTileEntity = block.defaultBlockState().hasBlockEntity();
                // Skip blocks with tile entities
                if (!hasTileEntity) {
                    if (!dimlets.contains(new DimletKey(DimletType.BLOCK, id.toString()))) {
                        boolean isOre = TagTools.hasTag(block, Tags.Blocks.ORES);
                        JsonObject object = new JsonObject();
                        object.addProperty("type", DimletType.BLOCK.name().toLowerCase());
                        object.addProperty("key", id.toString());
                        DimletSettings settings = DimletSettings.create(isOre ? DimletRarity.UNCOMMON : DimletRarity.COMMON,
                                        isOre ? 100 : 10, isOre ? 100 : 10, isOre ? 100 : 10)
                                .dimlet(true)
                                .worldgen(true)
                                .build();
                        settings.buildElement(object);
                        root.add(object);
                    }
                }
            }
        }
    }

    private static void writeStructures(JsonArray root, String modid) {
        Set<DimletKey> dimlets = DimletDictionary.get().getDimlets();
        for (var entry : ForgeRegistries.STRUCTURE_FEATURES.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (modid.toLowerCase().equals(id.getNamespace())) {
                if (!dimlets.contains(new DimletKey(DimletType.STRUCTURE, id.toString()))) {
                    JsonObject object = new JsonObject();
                    object.addProperty("type", DimletType.STRUCTURE.name().toLowerCase());
                    object.addProperty("key", id.toString());
                    DimletSettings settings = DimletSettings.create(DimletRarity.UNCOMMON, 100, 100, 100)
                            .dimlet(true)
                            .worldgen(true)
                            .build();
                    settings.buildElement(object);
                    root.add(object);
                }
            }
        }
    }

    public static void readPackage(String filename, BiFunction<DimletKey, DimletSettings, Boolean> consumer) {
        InputStream inputStream = null;

        Path configPath = FMLPaths.CONFIGDIR.get();
        new File(configPath + File.separator + "rftoolsdim").mkdirs();
        File file = new File(configPath + File.separator + "rftoolsdim" + File.separator + filename);
        if (file.exists()) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (inputStream == null) {
            inputStream = RFToolsDim.class.getResourceAsStream("/data/rftoolsdim/dimletpackages/" + filename);
            if (inputStream == null) {
                RFToolsDim.setup.getLogger().error("Can't find dimlet package: " + filename);
                throw new IllegalStateException("Can't find dimlet package: " + filename);
            }
        }

        int cnt = 0;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            JsonElement root = JsonParser.parseReader(br);
            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                String type = object.getAsJsonPrimitive("type").getAsString();
                String key = object.getAsJsonPrimitive("key").getAsString();
                DimletKey dimletKey = new DimletKey(DimletType.byName(type), key);
                DimletSettings settings = DimletSettings.parse(object);
                Boolean success = consumer.apply(dimletKey, settings);
                if (Boolean.TRUE.equals(success)) {
                    cnt++;
                }
            }

        } catch (IOException ex) {
            RFToolsDim.setup.getLogger().error("Error loading dimlet package: " + filename);
            throw new UncheckedIOException(ex);
        }
        RFToolsDim.setup.getLogger().info("Reading dimlet package: " + filename + ", " + cnt + " valid dimlets found");

    }
}
