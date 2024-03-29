package mcjty.rftoolsdim.dimension.data;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DimensionData {

    private final ResourceLocation id;
    private final DimensionDescriptor descriptor;
    private final DimensionDescriptor randomizedDescriptor;
    private long energy;

    public DimensionData(ResourceLocation id, DimensionDescriptor descriptor, DimensionDescriptor randomizedDescriptor) {
        this.id = id;
        this.descriptor = descriptor;
        this.randomizedDescriptor = randomizedDescriptor;
    }

    public DimensionData(CompoundNBT tag) {
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
    }

    public void write(CompoundNBT tag) {
        tag.putString("id", id.toString());
        tag.putString("descriptor", descriptor.compact());
        tag.putString("randomized", randomizedDescriptor.compact());
        tag.putLong("energy", energy);
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

    public long getEnergy() {
        return energy;
    }

    /// 'world' should be a valid world (or overworld). Can be null in case in which case the low power freeze will not happen
    public void setEnergy(World overworld, long energy) {
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

