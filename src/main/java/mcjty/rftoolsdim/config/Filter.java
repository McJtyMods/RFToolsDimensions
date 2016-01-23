package mcjty.rftoolsdim.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.varia.JSonTools;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Filter {
    private final Set<String> mods;
    private final Set<String> names;
    private final Set<Pattern> nameRegexps;
    private final Set<DimletType> types;
    private final Set<Feature> features;

    private Filter(Set<String> mods, Set<String> names, Set<Pattern> nameRegexps, Set<DimletType> types, Set<Feature> features) {
        this.mods = mods;
        this.names = names;
        this.nameRegexps = nameRegexps;
        this.types = types;
        this.features = features;
    }

    public boolean match(DimletType type, String mod, String name, Set<Feature> featuresIn) {
        if (types != null) {
            if (!types.contains(type)) {
                return false;
            }
        }
        if (mods != null) {
            if (!mods.contains(mod)) {
                return false;
            }
        }

        if (names != null || nameRegexps != null) {
            if (names != null) {
                if (names.contains(name)) {
                    return true;
                }
            }
            if (nameRegexps != null) {
                for (Pattern pattern : nameRegexps) {
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches()) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (features != null) {
            if (featuresIn == null) {
                return false;
            }
            if (!features.stream().allMatch(featuresIn::contains)) {
                return false;
            }
        }

        return true;
    }

    public static final Filter MATCHALL = new Filter(null, null, null, null, null);

    public JsonElement buildElement() {
        if (mods == null && names == null && nameRegexps == null && types == null && features == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        JSonTools.addArrayOrSingle(jsonObject, "mod", mods);

        Set<String> namedAndRegexps;
        if (names == null && nameRegexps == null) {
            namedAndRegexps = null;
        } else if (names == null) {
            namedAndRegexps = nameRegexps.stream().map(Pattern::toString).collect(Collectors.toSet());
        } else if (nameRegexps == null) {
            namedAndRegexps = names;
        } else {
            namedAndRegexps = Stream.concat(names.stream(), nameRegexps.stream().map(Pattern::toString)).collect(Collectors.toSet());
        }
        JSonTools.addArrayOrSingle(jsonObject, "name", namedAndRegexps);
        JSonTools.addArrayOrSingle(jsonObject, "type", types == null ? null : types.stream().map(t -> t.dimletType.getName().toLowerCase()).collect(Collectors.toList()));
        JSonTools.addArrayOrSingle(jsonObject, "feature", features == null ? null : features.stream().map(t -> t.name().toLowerCase()).collect(Collectors.toList()));

        return jsonObject;
    }

    public static Filter parse(JsonElement element) {
        if (element == null) {
            return MATCHALL;
        } else {
            Builder builder = new Builder();
            JsonObject jsonObject = element.getAsJsonObject();
            JSonTools.getElement(jsonObject, "mod")
                    .ifPresent(e -> JSonTools.asArrayOrSingle(e)
                            .map(JsonElement::getAsString)
                            .forEach(builder::mod));
            JSonTools.getElement(jsonObject, "name")
                    .ifPresent(e -> JSonTools.asArrayOrSingle(e)
                            .map(JsonElement::getAsString)
                            .forEach(builder::name));
            JSonTools.getElement(jsonObject, "type")
                    .ifPresent(e -> JSonTools.asArrayOrSingle(e)
                            .map(el -> DimletType.getTypeByName(el.getAsString()))
                            .forEach(builder::type));
            JSonTools.getElement(jsonObject, "feature")
                    .ifPresent(e -> JSonTools.asArrayOrSingle(e)
                            .map(el -> Feature.getFeatureByName(el.getAsString()))
                            .forEach(builder::feature));

            return builder.build();
        }
    }

    public static class Builder {
        private Set<String> mods = null;
        private Set<String> names = null;
        private Set<Pattern> name_regexps = null;
        private Set<DimletType> types = null;
        private Set<Feature> features = null;

        public Builder mod(String mod) {
            if (mods == null) {
                mods = new HashSet<>();
            }
            mods.add(mod);
            return this;
        }

        public Builder name(String name) {
            if (name.contains("*")) {
                // A regexp
                if (name_regexps == null) {
                    name_regexps = new HashSet<>();
                }
                name_regexps.add(Pattern.compile(name));
            } else {
                if (names == null) {
                    names = new HashSet<>();
                }
                names.add(name);
            }
            return this;
        }

        public Builder type(DimletType type) {
            if (types == null) {
                types = EnumSet.noneOf(DimletType.class);
            }
            types.add(type);
            return this;
        }

        public Builder feature(Feature feature) {
            if (features == null) {
                features = EnumSet.noneOf(Feature.class);
            }
            features.add(feature);
            return this;
        }

        public Filter build() {
            return new Filter(mods, names, name_regexps, types, features);
        }
    }

    // Specific features that a certain block/biome/whatever may support
    public enum Feature {
        TILEENTITY,
        OREDICT,
        PLANTABLE,
        NOFULLBLOCK,
        FALLING;

        private static final Map<String,Feature> FEATURE_MAP = new HashMap<>();

        static {
            for (Feature type : Feature.values()) {
                FEATURE_MAP.put(type.name().toLowerCase(), type);
            }
        }

        public static Feature getFeatureByName(String name) {
            return FEATURE_MAP.get(name);
        }
    }
}
