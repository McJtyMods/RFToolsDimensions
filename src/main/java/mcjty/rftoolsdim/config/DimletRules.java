package mcjty.rftoolsdim.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DimletRules {

    public static void readRules(File directory) {
        File file = new File(directory.getPath() + File.separator + "rftools", "dimlets.json");
        if (file.exists()) {
            readExistingRules(file);
        } else {
            createDefaultRules(file);
        }
    }

    private static void readExistingRules(File file) {
        BufferedReader br;
        try {
            FileInputStream inputstream = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
        } catch (FileNotFoundException e) {
            Logging.logError("Error reading file: " + file.getName());
            return;
        } catch (UnsupportedEncodingException e) {
            Logging.logError("Error reading file: " + file.getName());
            return;
        }
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(br);
        for (JsonElement entry : element.getAsJsonArray()) {
            if (!readRule(entry)) {
                return;
            }
        }
    }

    private static boolean readRule(JsonElement ruleElement) {
        JsonElement rule = ruleElement.getAsJsonObject().get("rule");
        if (rule == null) {
            Logging.logError("Error reading dimlets.json: rule is missing");
            return false;
        }

        JsonElement filter = rule.getAsJsonObject().get("filter");
        JsonElement settings = rule.getAsJsonObject().get("settings");
        return parseRule(filter, settings);
    }

    private static boolean parseRule(JsonElement filterElement, JsonElement settingsElement) {
        Filter filter = Filter.parse(filterElement);
        return true;
    }

    private static void createDefaultRules(File file) {

    }


    public static class Filter {
        private static Set<String> mods;
        private static Set<String> names;
        private static Set<DimletType> types;

        private Filter(Set<String> mods, Set<String> names, Set<DimletType> types) {
            this.mods = mods;
            this.names = names;
            this.types = types;
        }

        public static final Filter MATCHALL = new Filter(null, null, null);

        public static Filter parse(JsonElement element) {
            if (element == null) {
                return MATCHALL;
            } else {
                Builder builder = new Builder();
                JsonObject jsonObject = element.getAsJsonObject();
                JsonElement modElement = jsonObject.get("mod");
                if (modElement != null) {
                    if (modElement.isJsonArray()) {
                        for (JsonElement jsonElement : modElement.getAsJsonArray()) {
                            builder.mod(jsonElement.getAsString());
                        }
                    } else {
                        builder.mod(modElement.getAsString());
                    }
                }
                JsonElement nameElement = jsonObject.get("name");
                if (nameElement != null) {
                    if (nameElement.isJsonArray()) {
                        for (JsonElement jsonElement : nameElement.getAsJsonArray()) {
                            builder.name(jsonElement.getAsString());
                        }
                    } else {
                        builder.mod(nameElement.getAsString());
                    }
                }
                JsonElement typeElement = jsonObject.get("type");
                if (typeElement != null) {
                    if (typeElement.isJsonArray()) {
                        for (JsonElement jsonElement : typeElement.getAsJsonArray()) {
                            builder.type(DimletType.getTypeByName(jsonElement.getAsString()));
                        }
                    } else {
                        builder.type(DimletType.getTypeByName(typeElement.getAsString()));
                    }
                }

                return builder.build();
            }
        }

        public static class Builder {
            private static Set<String> mods = null;
            private static Set<String> names = null;
            private static Set<DimletType> types = null;

            public Builder mod(String mod) {
                if (mods == null) {
                    mods = new HashSet<>();
                }
                mods.add(mod);
                return this;
            }

            public Builder name(String name) {
                if (names == null) {
                    names = new HashSet<>();
                }
                names.add(name);
                return this;
            }

            public Builder type(DimletType type) {
                if (types == null) {
                    types = new HashSet<>();
                }
                types.add(type);
                return this;
            }

            public Filter build() {
                return new Filter(mods, names, types);
            }
        }
    }

}
