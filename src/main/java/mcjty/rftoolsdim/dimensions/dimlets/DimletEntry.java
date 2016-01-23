package mcjty.rftoolsdim.dimensions.dimlets;

import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.config.Settings;

public class DimletEntry {
    private final DimletKey key;
    private final int rfCreateCost;     // Overrides the type default
    private final int rfMaintainCost;   // Overrides the type default
    private final int tickCost;         // Overrides the type default
    private final int rarity;           // Overrides the type default
    private final boolean worldgen;     // Allowed in worldgen
    private final boolean dimlet;       // Allowed as dimlet

    public DimletEntry(DimletKey key, int rfCreateCost, int rfMaintainCost, int tickCost, int rarity, boolean worldgen, boolean dimlet) {
        this.key = key;
        this.rfCreateCost = rfCreateCost;
        this.rfMaintainCost = rfMaintainCost;
        this.tickCost = tickCost;
        this.rarity = rarity;
        this.worldgen = worldgen;
        this.dimlet = dimlet;
    }

    public DimletEntry(DimletKey key, Settings settings) {
        this.key = key;
        this.rfCreateCost = settings.getCreateCost();
        this.rfMaintainCost = settings.getMaintainCost();
        this.tickCost = settings.getTickCost();
        this.rarity = settings.getRarity();
        this.worldgen = settings.getWorldgen();
        this.dimlet = settings.getDimlet();
    }


    public DimletKey getKey() {
        return key;
    }

    public int getRfCreateCost() {
        return rfCreateCost;
    }

    public int getBaseRfMaintainCost() {
        return rfMaintainCost;
    }

    public int getRfMaintainCost() {
        int cost = rfMaintainCost;
        if (cost > 0) {
            float factor = PowerConfiguration.maintenanceCostPercentage / 100.0f;
            if (factor < -0.9f) {
                factor = -0.9f;
            }
            cost += cost * factor;
        }

        return cost;
    }

    public int getTickCost() {
        return tickCost;
    }

    public int getRarity() {
        return rarity;
    }

    public boolean isWorldgen() {
        return worldgen;
    }

    public boolean isDimlet() {
        return dimlet;
    }

    @Override
    public String toString() {
        return "DimletEntry{ R:" + rarity + ", cost:" + rfCreateCost + "/" + rfMaintainCost + "/" + tickCost + ", W:" + worldgen + ", D:" + dimlet + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DimletEntry that = (DimletEntry) o;

        if (rfCreateCost != that.rfCreateCost) return false;
        if (rfMaintainCost != that.rfMaintainCost) return false;
        if (tickCost != that.tickCost) return false;
        if (rarity != that.rarity) return false;
        if (worldgen != that.worldgen) return false;
        if (dimlet != that.dimlet) return false;
        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + rfCreateCost;
        result = 31 * result + rfMaintainCost;
        result = 31 * result + tickCost;
        result = 31 * result + rarity;
        result = 31 * result + (worldgen ? 1 : 0);
        result = 31 * result + (dimlet ? 1 : 0);
        return result;
    }
}
