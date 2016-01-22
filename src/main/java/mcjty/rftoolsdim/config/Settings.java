package mcjty.rftoolsdim.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.rftoolsdim.varia.JsonTools;

public class Settings {
    private final Integer rarity;
    private final Integer createCost;
    private final Integer maintainCost;
    private final Integer tickCost;
    private final Boolean worldgen;
    private final Boolean dimlet;

    private Settings(Builder builder) {
        this.rarity = builder.rarity;
        this.createCost = builder.createCost;
        this.maintainCost = builder.maintainCost;
        this.tickCost = builder.tickCost;
        this.worldgen = builder.worldgen;
        this.dimlet = builder.dimlet;
    }

    public boolean isComplete() {
        return rarity != null && createCost != null && maintainCost != null && tickCost != null && worldgen != null && dimlet != null;
    }

    @Override
    public String toString() {
        return "Settings{ R:" + rarity + ", cost:" + createCost + "/" + maintainCost + "/" + tickCost + ", W:" + worldgen + ", D:" + dimlet + " }";
    }

    public JsonElement buildElement() {
        JsonObject jsonObject = new JsonObject();
        if (rarity != null) {
            jsonObject.add("rarity", new JsonPrimitive(rarity));
        }
        if (createCost != null) {
            jsonObject.add("create", new JsonPrimitive(createCost));
        }
        if (maintainCost != null) {
            jsonObject.add("maintain", new JsonPrimitive(maintainCost));
        }
        if (tickCost != null) {
            jsonObject.add("ticks", new JsonPrimitive(tickCost));
        }
        if (worldgen != null) {
            jsonObject.add("worldgen", new JsonPrimitive(worldgen));
        }
        if (dimlet != null) {
            jsonObject.add("dimlet", new JsonPrimitive(dimlet));
        }

        return jsonObject;
    }

    public static Settings parse(JsonElement element) {
        Builder builder = new Builder();

        JsonObject jsonObject = element.getAsJsonObject();

        JsonTools.getElement(jsonObject, "rarity").ifPresent(e -> builder.rarity(e.getAsInt()));
        JsonTools.getElement(jsonObject, "create").ifPresent(e -> builder.createCost(e.getAsInt()));
        JsonTools.getElement(jsonObject, "maintain").ifPresent(e -> builder.maintainCost(e.getAsInt()));
        JsonTools.getElement(jsonObject, "ticks").ifPresent(e -> builder.tickCost(e.getAsInt()));
        JsonTools.getElement(jsonObject, "worldgen").ifPresent(e -> builder.worldgen(e.getAsBoolean()));
        JsonTools.getElement(jsonObject, "dimlet").ifPresent(e -> builder.dimlet(e.getAsBoolean()));

        return builder.build();
    }

    public Integer getCreateCost() {
        return createCost;
    }

    public Boolean getDimlet() {
        return dimlet;
    }

    public Integer getMaintainCost() {
        return maintainCost;
    }

    public Integer getRarity() {
        return rarity;
    }

    public Integer getTickCost() {
        return tickCost;
    }

    public Boolean getWorldgen() {
        return worldgen;
    }

    public static class Builder {
        private Integer rarity;
        private Integer createCost;
        private Integer maintainCost;
        private Integer tickCost;
        private Boolean worldgen;
        private Boolean dimlet;

        public Builder merge(Settings settings) {
            if (rarity == null) {
                rarity = settings.rarity;
            }
            if (createCost == null) {
                createCost = settings.createCost;
            }
            if (maintainCost == null) {
                maintainCost = settings.maintainCost;
            }
            if (tickCost == null) {
                tickCost = settings.tickCost;
            }
            if (worldgen == null) {
                worldgen = settings.worldgen;
            }
            if (dimlet == null) {
                dimlet = settings.dimlet;
            }
            return this;
        }

        public Builder complete() {
            if (rarity == null) {
                rarity = 0;
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

        public Builder rarity(int rarity) {
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

        public Settings build() {
            return new Settings(this);
        }

    }
}
