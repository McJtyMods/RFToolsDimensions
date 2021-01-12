package mcjty.rftoolsdim.dimension.descriptor;

import com.google.gson.JsonElement;

public class FeatureDescriptor {

    private final String id;
    private final JsonElement configElement;

    public FeatureDescriptor(String id, JsonElement configElement) {
        this.id = id;
        this.configElement = configElement;
    }

    public String getId() {
        return id;
    }

    public JsonElement getConfigElement() {
        return configElement;
    }
}
