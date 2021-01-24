package mcjty.rftoolsdim.dimension.data;

import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class DimensionData {

    private final ResourceLocation id;
    private final DimensionDescriptor descriptor;

    public DimensionData(ResourceLocation id, DimensionDescriptor descriptor) {
        this.id = id;
        this.descriptor = descriptor;
    }

    public DimensionData(CompoundNBT tag) {
        id = new ResourceLocation(tag.getString("id"));
        descriptor = new DimensionDescriptor();
        descriptor.read(tag.getString("descriptor"));
    }

    public void write(CompoundNBT tag) {
        tag.putString("id", id.toString());
        tag.putString("descriptor", descriptor.compact());
    }

    public ResourceLocation getId() {
        return id;
    }

    public DimensionDescriptor getDescriptor() {
        return descriptor;
    }
}

