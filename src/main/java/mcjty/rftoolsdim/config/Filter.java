package mcjty.rftoolsdim.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.network.ByteBufTools;
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
    private final Set<Integer> metas;
    private final Map<String, String> properties;

    private Filter(Set<String> mods, Set<String> names, Set<Pattern> nameRegexps, Set<DimletType> types, Set<Feature> features, Set<Integer> metas, Map<String, String> properties) {
        this.mods = mods;
        this.names = names;
        this.nameRegexps = nameRegexps;
        this.types = types;
        this.features = features;
        this.metas = metas;
        this.properties = properties;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufTools.writeSetAsStrings(buf, mods);
        ByteBufTools.writeSetAsStrings(buf, names);
        ByteBufTools.writeSetAsStrings(buf, nameRegexps);
        ByteBufTools.writeSetAsEnums(buf, types);
        ByteBufTools.writeSetAsEnums(buf, features);
        ByteBufTools.writeSetAsShorts(buf, metas);
        ByteBufTools.writeMapAsStrings(buf, properties);
    }

    public Filter(ByteBuf buf) {
        mods = ByteBufTools.readSetFromStrings(buf);
        names = ByteBufTools.readSetFromStrings(buf);
        nameRegexps = ByteBufTools.readSetFromStringsWithMapper(buf, p -> Pattern.compile(p));
        types = ByteBufTools.readSetFromShortsWithMapper(buf, p -> DimletType.values()[p]);
        features = ByteBufTools.readSetFromShortsWithMapper(buf, p -> Feature.values()[p]);
        metas = ByteBufTools.readSetFromShorts(buf);
        properties = ByteBufTools.readMapFromStrings(buf);
    }

    public boolean match(DimletType type, String mod, String name, int metaIn, Map<String, String> propertiesIn, Set<Feature> featuresIn) {
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

        if (metas != null) {
            if (!metas.contains(metaIn)) {
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

        if (properties != null) {
            if (propertiesIn == null) {
                return false;
            }
            if (!properties.entrySet().stream().allMatch(e -> safeCompare(propertiesIn, e))) {
                return false;
            }
        }

        return true;
    }

    private boolean safeCompare(Map<String, String> propertiesIn, Map.Entry<String, String> e) {
        if (!propertiesIn.containsKey(e.getKey())) {
            return false;
        }
        if (propertiesIn.get(e.getKey()) == null) {
            return false;
        }
        return propertiesIn.get(e.getKey()).equals(e.getValue());
    }

    public static final Filter MATCHALL = new Filter(null, null, null, null, null, null, null);

    public JsonElement buildElement() {
        if (mods == null && names == null && nameRegexps == null && types == null && features == null && metas == null && properties == null) {
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
        JSonTools.addIntArrayOrSingle(jsonObject, "meta", metas);
        JSonTools.addArrayOrSingle(jsonObject, "type", types == null ? null : types.stream().map(t -> t.dimletType.getName().toLowerCase()).collect(Collectors.toList()));
        JSonTools.addArrayOrSingle(jsonObject, "feature", features == null ? null : features.stream().map(t -> t.name().toLowerCase()).collect(Collectors.toList()));
        JSonTools.addPairs(jsonObject, "property", properties);

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
            JSonTools.getElement(jsonObject, "meta")
                    .ifPresent(e -> JSonTools.asArrayOrSingle(e)
                            .map(JsonElement::getAsInt)
                            .forEach(builder::meta));
            JSonTools.getElement(jsonObject, "property")
                    .ifPresent(e -> JSonTools.asPairs(e)
                            .forEach(p -> builder.property(p.getKey(), p.getValue())));

            return builder.build();
        }
    }

    public static class Builder {
        private Set<String> mods = null;
        private Set<String> names = null;
        private Set<Pattern> name_regexps = null;
        private Set<DimletType> types = null;
        private Set<Feature> features = null;
        private Set<Integer> metas = null;
        private Map<String, String> properties = null;

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

        public Builder property(String name, String value) {
            if (properties == null) {
                properties = new HashMap<>();
            }
            properties.put(name, value);
            return this;
        }

        public Builder meta(Integer meta) {
            if (metas == null) {
                metas = new HashSet<>();
            }
            metas.add(meta);
            return this;
        }

        public Filter build() {
            return new Filter(mods, names, name_regexps, types, features, metas, properties);
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
