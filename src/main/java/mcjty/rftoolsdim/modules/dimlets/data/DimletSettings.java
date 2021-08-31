package mcjty.rftoolsdim.modules.dimlets.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.varia.JSonTools;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class DimletSettings {

    private final DimletRarity rarity;
    private final int createCost;
    private final int maintainCost;
    private final int tickCost;
    private final boolean worldgen;
    private final boolean dimlet;
    private final ItemStack essence;

    private DimletSettings(Builder builder) {
        this.rarity = builder.rarity;
        this.createCost = builder.createCost;
        this.maintainCost = builder.maintainCost;
        this.tickCost = builder.tickCost;
        this.worldgen = builder.worldgen;
        this.dimlet = builder.dimlet;
        this.essence = builder.essence;
        if (rarity == null) {
            throw new IllegalStateException("Dimlet without rarity!");
        }
    }

    public DimletSettings(PacketBuffer buf) {
        rarity = DimletRarity.values()[buf.readInt()];
        createCost = buf.readInt();
        maintainCost = buf.readInt();
        tickCost = buf.readInt();
        worldgen = buf.readBoolean();
        dimlet = buf.readBoolean();
        essence = buf.readItem();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(rarity.ordinal());
        buf.writeInt(createCost);
        buf.writeInt(maintainCost);
        buf.writeInt(tickCost);
        buf.writeBoolean(worldgen);
        buf.writeBoolean(dimlet);
        buf.writeItemStack(essence, false);
    }

    public void buildElement(JsonObject jsonObject) {
        if (rarity != null) {
            jsonObject.add("rarity", new JsonPrimitive(rarity.name().toLowerCase()));
        }
        jsonObject.add("create", new JsonPrimitive(createCost));
        jsonObject.add("maintain", new JsonPrimitive(maintainCost));
        jsonObject.add("ticks", new JsonPrimitive(tickCost));
        jsonObject.add("worldgen", new JsonPrimitive(worldgen));
        jsonObject.add("dimlet", new JsonPrimitive(dimlet));
        if (!essence.isEmpty()) {
            JsonObject json = JSonTools.itemStackToJson(essence);
            jsonObject.add("essence", json);
        }

    }

    public static DimletSettings parse(JsonObject jsonObject) {
        Builder builder = new Builder();

        builder.rarity(DimletRarity.byName(JSonTools.getElement(jsonObject, "rarity").orElseThrow(() -> new IllegalStateException("Missing rarity")).getAsString()));
//        JSonTools.getElement(jsonObject, "rarity").ifPresent(e -> builder.rarity(DimletRarity.byName(e.getAsString())));
        JSonTools.getElement(jsonObject, "create").ifPresent(e -> builder.createCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "maintain").ifPresent(e -> builder.maintainCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "ticks").ifPresent(e -> builder.tickCost(e.getAsInt()));
        JSonTools.getElement(jsonObject, "worldgen").ifPresent(e -> builder.worldgen(e.getAsBoolean()));
        JSonTools.getElement(jsonObject, "dimlet").ifPresent(e -> builder.dimlet(e.getAsBoolean()));
        if (jsonObject.has("essence")) {
            builder.essence(JSonTools.jsonToItemStack(jsonObject.getAsJsonObject("essence")));
        }

        return builder.build();
    }

    public ItemStack getEssence() {
        return essence;
    }

    public DimletRarity getRarity() {
        return rarity;
    }

    public int getCreateCost() {
        return createCost;
    }

    public int getMaintainCost() {
        return maintainCost;
    }

    public int getTickCost() {
        return tickCost;
    }

    public boolean isWorldgen() {
        return worldgen;
    }

    public boolean isDimlet() {
        return dimlet;
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
        private ItemStack essence = ItemStack.EMPTY;

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

        public Builder essence(ItemStack stack) {
            this.essence = stack;
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
