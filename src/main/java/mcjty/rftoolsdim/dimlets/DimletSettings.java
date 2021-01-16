package mcjty.rftoolsdim.dimlets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.varia.JSonTools;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;

public class DimletSettings {

    private final DimletRarity rarity;
    private final int createCost;
    private final int maintainCost;
    private final int tickCost;
    private final boolean worldgen;
    private final boolean dimlet;

    private DimletSettings(Builder builder) {
        this.rarity = builder.rarity;
        this.createCost = builder.createCost;
        this.maintainCost = builder.maintainCost;
        this.tickCost = builder.tickCost;
        this.worldgen = builder.worldgen;
        this.dimlet = builder.dimlet;
    }

    public JsonElement buildElement() {
        JsonObject jsonObject = new JsonObject();
        if (rarity != null) {
            jsonObject.add("rarity", new JsonPrimitive(rarity.name()));
        }
        jsonObject.add("create", new JsonPrimitive(createCost));
        jsonObject.add("maintain", new JsonPrimitive(maintainCost));
        jsonObject.add("ticks", new JsonPrimitive(tickCost));
        jsonObject.add("worldgen", new JsonPrimitive(worldgen));
        jsonObject.add("dimlet", new JsonPrimitive(dimlet));

        return jsonObject;
    }

    public static DimletSettings parse(JsonElement element) {
        Builder builder = new Builder();

        JsonObject jsonObject = element.getAsJsonObject();

        JSonTools.getElement(jsonObject, "rarity").ifPresent(e -> builder.rarity(DimletRarity.valueOf(e.getAsString().toUpperCase())));
        JSonTools.getElement(jsonObject, "create").ifPresent(e -> builder.createCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "maintain").ifPresent(e -> builder.maintainCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "ticks").ifPresent(e -> builder.tickCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "worldgen").ifPresent(e -> builder.worldgen(e.getAsBoolean()));
        JSonTools.getElement(jsonObject, "dimlet").ifPresent(e -> builder.dimlet(e.getAsBoolean()));

        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DimletSettings.Builder create(DimletRarity rarity, int createCost, int maintainCost, int tickCost) {
        return builder()
                .rarity(rarity)
                .createCost(createCost)
                .maintainCost(maintainCost)
                .tickCost(tickCost)
                .worldgen(true)
                .dimlet(true);
    }

    public static class Builder {
        private DimletRarity rarity;
        private Integer createCost;
        private Integer maintainCost;
        private Integer tickCost;
        private Boolean worldgen;
        private Boolean dimlet;

        private Builder() {

        }

        public Builder complete() {
            if (rarity == null) {
                rarity = DimletRarity.COMMON;
            }
            if (createCost == null) {
                createCost = 1;
            }
            if (maintainCost == null) {
                maintainCost = 1;
            }
            if (tickCost == null) {
                tickCost = 1;
            }
            if (worldgen == null) {
                worldgen = false;
            }
            if (dimlet == null) {
                dimlet = false;
            }
            return this;
        }

        public Builder rarity(DimletRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder createCost(int createCost) {
            this.createCost = createCost;
            return this;
        }

        public Builder maintainCost(int maintainCost) {
            this.maintainCost = maintainCost;
            return this;
        }

        public Builder tickCost(int tickCost) {
            this.tickCost = tickCost;
            return this;
        }

        public Builder worldgen(boolean worldgen) {
            this.worldgen = worldgen;
            return this;
        }

        public Builder dimlet(boolean dimlet) {
            this.dimlet = dimlet;
            return this;
        }

        public DimletSettings build() {
            return new DimletSettings(this);
        }

    }

}
