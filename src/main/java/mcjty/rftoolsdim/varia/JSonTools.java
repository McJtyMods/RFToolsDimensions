package mcjty.rftoolsdim.varia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class JSonTools {

    public static Optional<JsonElement> getElement(JsonObject element, String name) {
        JsonElement el = element.get(name);
        if (el != null) {
            return Optional.of(el);
        } else {
            return Optional.empty();
        }
    }

    public static Stream<JsonElement> asArrayOrSingle(JsonElement element) {
        if (element.isJsonArray()) {
            Stream.Builder<JsonElement> builder = Stream.<JsonElement>builder();
            for (JsonElement el : element.getAsJsonArray()) {
                builder.add(el);
            }
            return builder.build();
        } else {
            return Stream.of(element);
        }
    }

    public static void addArrayOrSingle(JsonObject parent, String name, Collection<String> strings) {
        if (strings != null) {
            if (strings.size() == 1) {
                parent.add(name, new JsonPrimitive(strings.iterator().next()));
            } else {
                JsonArray array = new JsonArray();
                for (String mod : strings) {
                    array.add(new JsonPrimitive(mod));
                }
                parent.add(name, array);
            }
        }
    }
}
