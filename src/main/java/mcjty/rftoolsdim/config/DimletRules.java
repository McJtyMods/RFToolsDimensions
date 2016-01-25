package mcjty.rftoolsdim.config;

import com.google.gson.*;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;

public class DimletRules {

    private static List<Pair<Filter, Settings>> rules;

    public static Settings getSettings(DimletType type, String mod, String name, Set<Filter.Feature> features, int meta, Map<String, String> properties) {
        Settings.Builder builder = new Settings.Builder();

        for (Pair<Filter, Settings> pair : rules) {
            Filter filter = pair.getLeft();
            if (filter.match(type, mod, name, meta, properties, features)) {
                Settings settings = pair.getRight();
                builder.merge(settings);
                if (settings.isComplete()) {
                    break;
                }
            }
        }

        return builder.complete().build();
    }

    public static Settings getSettings(DimletKey key, String mod, Set<Filter.Feature> features, Map<String, String> properties) {
        String id = key.getId();
        int meta = 0;
        if (DimletType.DIMLET_MATERIAL.equals(key.getType())) {
            int firstIndexOf = StringUtils.indexOf(id, ":");
            id = id.substring(firstIndexOf+1);
            int lastIndexOf = StringUtils.lastIndexOf(id, "@");
            meta = Integer.parseInt(id.substring(lastIndexOf+1));
            id = id.substring(0, lastIndexOf);
        } else if (DimletType.DIMLET_LIQUID.equals(key.getType())) {
            int firstIndexOf = StringUtils.indexOf(id, ":");
            id = id.substring(firstIndexOf+1);
        }
        return getSettings(key.getType(), mod, id, features, meta, properties);
    }

    public static Settings getSettings(DimletKey key, String mod) {
        return getSettings(key, mod, Collections.emptySet(), Collections.emptyMap());
    }

    public static List<Pair<Filter, Settings>> getRules() {
        return rules;
    }

    public static void readRules(File directory) {
        File file = new File(directory.getPath() + File.separator + "rftools", "dimlets.json");
        List<Pair<Filter, Settings>> userRules = Collections.emptyList();
        if (file.exists()) {
            // Read the existing rules from dimlets.json until we encounter a 'regen' line.
            Logging.log("Reading dimlets.json from config");
            userRules = readRulesFromFile(file);
        }

        Logging.log("Reading default dimlets.json");
        InputStream inputstream = RFToolsDim.class.getResourceAsStream("/assets/rftoolsdim/text/dimlets.json");
        List<Pair<Filter, Settings>> builtinRules = readRulesFromFile(inputstream, "Builtin dimlets.json");

        if (file.exists()) {
            file.delete();
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            Logging.logError("Error writing dimlets.json!");
            return;
        }

        // Check if the last rule is null. In that case we have to append the builtin ruiles.
        boolean addBuiltin = false;
        if (userRules.isEmpty()) {
            addBuiltin = true;
        } else if (userRules.get(userRules.size()-1) == null) {
            addBuiltin = true;
            userRules.remove(userRules.size()-1);       // Remove the null line
        }

        // Make a dummy array of rules to output with the 'regen' line added.
        List<Pair<Filter, Settings>> outputRules = new ArrayList<>(userRules);
        // If the last user rule is a null then we add the builtin stuff. Otherwise we don't
        if (addBuiltin) {
            outputRules.add(null);  // Add the regen line but for output only.
            outputRules.addAll(builtinRules);
        }
        writeRules(writer, outputRules);
        writer.close();

        rules = new ArrayList<>(userRules);
        if (addBuiltin) {
            rules.addAll(builtinRules);
        }
    }

    private static List<Pair<Filter,Settings>> readRulesFromFile(File file) {
        FileInputStream inputstream;
        try {
            inputstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logging.logError("Error reading file: " + file.getName());
            return Collections.emptyList();
        }
        try {
            return readRulesFromFile(inputstream, file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<Pair<Filter,Settings>> readRulesFromFile(InputStream inputstream, String name) {
        List<Pair<Filter, Settings>> rules = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Logging.logError("Error reading file: " + name);
            return rules;
        }
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(br);
        for (JsonElement entry : element.getAsJsonArray()) {
            Pair<Filter, Settings> rule = readRule(entry);
            if (rule == null) {
                // We add the null rule so that we know that it was there.
                rules.add(null);
                return rules;
            }
            rules.add(rule);
        }
        return rules;
    }

    private static Pair<Filter,Settings> readRule(JsonElement ruleElement) {
        if (ruleElement.isJsonPrimitive()) {
            // We stop here since this is not a valid rule.
            // This is useful to stop processing on "regen" line
            return null;
        }
        JsonElement filter = ruleElement.getAsJsonObject().get("filter");
        JsonElement settings = ruleElement.getAsJsonObject().get("settings");
        if (settings == null) {
            Logging.logError("Error reading dimlets.json: settings is missing");
            return null;
        }
        return parseRule(filter, settings);
    }

    private static Pair<Filter,Settings> parseRule(JsonElement filterElement, JsonElement settingsElement) {
        Filter filter = Filter.parse(filterElement);
        Settings settings = Settings.parse(settingsElement);
        return Pair.of(filter, settings);
    }

    private static void writeRules(PrintWriter writer, List<Pair<Filter, Settings>> rules) {
        JsonArray array = new JsonArray();
        for (Pair<Filter, Settings> rule : rules) {
            if (rule == null) {
                array.add(new JsonPrimitive("Everything below this line will be regenerated from defaults every time. Remove this line if you do not want that"));
            } else {
                JsonElement filterElement = rule.getLeft().buildElement();
                JsonElement settingsElement = rule.getRight().buildElement();
                JsonObject ruleObject = new JsonObject();
                if (filterElement != null) {
                    ruleObject.add("filter", filterElement);
                }
                ruleObject.add("settings", settingsElement);
                array.add(ruleObject);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer.print(gson.toJson(array));
    }

}
