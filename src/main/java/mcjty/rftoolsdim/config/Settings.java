package mcjty.rftoolsdim.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mcjty.rftoolsdim.api.dimlet.ISettingsBuilder;
import mcjty.rftoolsdim.varia.JSonTools;

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

    public void toBytes(ByteBuf buf) {
        if (rarity == null) {
            buf.writeByte(127);
        } else {
            buf.writeByte(rarity);
        }
        if (createCost == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeInt(createCost);
        }
        if (maintainCost == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeInt(maintainCost);
        }
        if (tickCost == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeInt(tickCost);
        }
        if (worldgen == null) {
            buf.writeByte(127);
        } else {
            buf.writeByte(worldgen ? 1 : 0);
        }
        if (dimlet == null) {
            buf.writeByte(127);
        } else {
            buf.writeByte(dimlet ? 1 : 0);
        }
    }

    public Settings(ByteBuf buf) {
        byte b = buf.readByte();
        if (b == 127) {
            rarity = null;
        } else {
            rarity = (int) b;
        }
        if (buf.readBoolean()) {
            createCost = buf.readInt();
        } else {
            createCost = null;
        }
        if (buf.readBoolean()) {
            maintainCost = buf.readInt();
        } else {
            maintainCost = null;
        }
        if (buf.readBoolean()) {
            tickCost = buf.readInt();
        } else {
            tickCost = null;
        }
        b = buf.readByte();
        if (b == 127) {
            worldgen = null;
        } else {
            worldgen = b == 1;
        }
        b = buf.readByte();
        if (b == 127) {
            dimlet = null;
        } else {
            dimlet = b == 1;
        }
    }

    public boolean isBlacklisted() {
        return !worldgen && !dimlet;
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

        JSonTools.getElement(jsonObject, "rarity").ifPresent(e -> builder.rarity(e.getAsInt()));
        JSonTools.getElement(jsonObject, "create").ifPresent(e -> builder.createCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "maintain").ifPresent(e -> builder.maintainCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "ticks").ifPresent(e -> builder.tickCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "worldgen").ifPresent(e -> builder.worldgen(e.getAsBoolean()));
        JSonTools.getElement(jsonObject, "dimlet").ifPresent(e -> builder.dimlet(e.getAsBoolean()));

        return builder.build();
    }

    public Integer getCreateCost() {
        return createCost;
    }

    public boolean isDimlet() {
        return dimlet == null ? false : dimlet;
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

    public boolean isWorldgen() {
        return worldgen == null ? false : worldgen;
    }

    public static class Builder implements ISettingsBuilder {
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

        @Override
        public Builder rarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        @Override
        public Builder createCost(int createCost) {
            this.createCost = createCost;
            return this;
        }

        @Override
        public Builder maintainCost(int maintainCost) {
            this.maintainCost = maintainCost;
            return this;
        }

        @Override
        public Builder tickCost(int tickCost) {
            this.tickCost = tickCost;
            return this;
        }

        @Override
        public Builder worldgen(boolean worldgen) {
            this.worldgen = worldgen;
            return this;
        }

        @Override
        public Builder dimlet(boolean dimlet) {
            this.dimlet = dimlet;
            return this;
        }

        public Settings build() {
            return new Settings(this);
        }

    }
}
