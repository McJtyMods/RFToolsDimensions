package mcjty.rftoolsdim.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mcjty.rftoolsdim.dimensions.dimlets.types.DimletType;
import mcjty.rftoolsdim.varia.JsonTools;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filter {
    private final Set<String> mods;
    private final Set<String> names;
    private final Set<Pattern> nameRegexps;
    private final Set<DimletType> types;

    private Filter(Set<String> mods, Set<String> names, Set<Pattern> nameRegexps, Set<DimletType> types) {
        this.mods = mods;
        this.names = names;
        this.nameRegexps = nameRegexps;
        this.types = types;
    }

    public boolean match(DimletType type, String mod, String name) {
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
        return true;
    }

    public static final Filter MATCHALL = new Filter(null, null, null, null);

    public JsonElement buildElement() {
        if (mods == null && names == null && nameRegexps == null && types == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        JsonTools.addArrayOrSingle(jsonObject, "mod", mods);

        Set<String> namedAndRegexps;
        if (names == null && nameRegexps == null) {
            namedAndRegexps = null;
        } else if (names == null) {
            namedAndRegexps = nameRegexps.stream().map(p -> p.toString()).collect(Collectors.toSet());
        } else if (nameRegexps == null) {
            namedAndRegexps = names;
        } else {
            namedAndRegexps = Stream.concat(names.stream(), nameRegexps.stream().map(p -> p.toString())).collect(Collectors.toSet());
        }
        JsonTools.addArrayOrSingle(jsonObject, "name", namedAndRegexps);
        JsonTools.addArrayOrSingle(jsonObject, "type", types == null ? null : types.stream().map(t -> t.dimletType.getName().toLowerCase()).collect(Collectors.toList()));

        return jsonObject;
    }

    public static Filter parse(JsonElement element) {
        if (element == null) {
            return MATCHALL;
        } else {
            Builder builder = new Builder();
            JsonObject jsonObject = element.getAsJsonObject();
            JsonTools.getElement(jsonObject, "mod")
                    .ifPresent(e -> JsonTools.asArrayOrSingle(e)
                            .map(el -> el.getAsString())
                            .forEach(s -> builder.mod(s)));
            JsonTools.getElement(jsonObject, "name")
                    .ifPresent(e -> JsonTools.asArrayOrSingle(e)
                            .map(el -> el.getAsString())
                            .forEach(s -> builder.name(s)));
            JsonTools.getElement(jsonObject, "type")
                    .ifPresent(e -> JsonTools.asArrayOrSingle(e)
                            .map(el -> DimletType.getTypeByName(el.getAsString()))
                            .forEach(s -> builder.type(s)));

            return builder.build();
        }
    }

    public static class Builder {
        private Set<String> mods = null;
        private Set<String> names = null;
        private Set<Pattern> name_regexps = null;
        private Set<DimletType> types = null;

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

        public Filter build() {
            return new Filter(mods, names, name_regexps, types);
        }
    }
}
