package mcjty.rftoolsdim.dimension.data;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class DimensionData {

    private final ResourceLocation id;
    private final DimensionDescriptor descriptor;
    private final DimensionDescriptor randomizedDescriptor;
    private final UUID owner;
    private final long skyDimletTypes;
    private long energy;
    private int activityProbes;

    public DimensionData(ResourceLocation id, DimensionDescriptor descriptor, DimensionDescriptor randomizedDescriptor,
                         final UUID owner,
                         long skyDimletTypes) {
        this.id = id;
        this.descriptor = descriptor;
        this.randomizedDescriptor = randomizedDescriptor;
        this.owner = owner;
        this.skyDimletTypes = skyDimletTypes;
        activityProbes = 0;
    }

    public DimensionData(CompoundTag tag) {
        id = new ResourceLocation(tag.getString("id"));
        descriptor = new DimensionDescriptor();
        descriptor.read(tag.getString("descriptor"));
        energy = tag.getLong("energy");
        if (tag.contains("randomized")) {
            randomizedDescriptor = new DimensionDescriptor();
            randomizedDescriptor.read(tag.getString("randomized"));
        } else {
            randomizedDescriptor = DimensionDescriptor.EMPTY;
        }
        skyDimletTypes = tag.getLong("skytypes");
        activityProbes = tag.getInt("probes");
        if (tag.contains("owner")) {
            owner = tag.getUUID("owner");
        } else {
            owner = null;
        }
    }

    public void write(CompoundTag tag) {
        tag.putString("id", id.toString());
        tag.putString("descriptor", descriptor.compact());
        tag.putString("randomized", randomizedDescriptor.compact());
        tag.putLong("energy", energy);
        tag.putLong("skytypes", skyDimletTypes);
        tag.putInt("probes", activityProbes);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
    }

    public ResourceLocation getId() {
        return id;
    }

    public DimensionDescriptor getDescriptor() {
        return descriptor;
    }

    public DimensionDescriptor getRandomizedDescriptor() {
        return randomizedDescriptor;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getSkyTypes() {
        return skyDimletTypes;
    }

    public long getEnergy() {
        return energy;
    }

    public int getActivityProbes() {
        return activityProbes;
    }

    public void setActivityProbes(int activityProbes) {
        this.activityProbes = activityProbes;
    }

    /// 'world' should be a valid world (or overworld). Can be null in case in which case the low power freeze will not happen
    public void setEnergy(Level overworld, long energy) {
        if (energy != this.energy) {
            long old = this.energy;
            this.energy = energy;
            if (overworld != null) {
//            if (PowerConfiguration.freezeUnpowered) { // @todo 1.16 config
                if (old == 0 && energy > 0) {
                    overworld = LevelTools.getLevel(overworld, id);
                    if (overworld != null) {
//                    RfToolsDimensionManager.unfreezeDimension(world);
                    }
                } else if (energy == 0) {
                    overworld = LevelTools.getLevel(overworld, id);
                    if (overworld != null) {
//                    RfToolsDimensionManager.freezeDimension(world);
                    }
                }
//            }
            }

        }
    }
}

