package mcjty.rftoolsdim.dimension.data;

import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains data that needs to be saved with the world. Like the name of the dimension, power, descriptor, ...
 */
public class PersistantDimensionManager extends AbstractWorldData<PersistantDimensionManager> {

    private static final String NAME = "RFToolsDimensions";

    private final Map<ResourceLocation, DimensionData> data = new HashMap<>();
    private final Map<DimensionDescriptor, DimensionData> dataByDescriptor = new HashMap<>();

    public PersistantDimensionManager(String name) {
        super(name);
    }

    @Nonnull
    public static PersistantDimensionManager get(World world) {
        return getData(world, () -> new PersistantDimensionManager(NAME), NAME);
    }

    public DimensionData getData(ResourceLocation id) {
        return data.get(id);
    }

    public DimensionData getData(DimensionDescriptor descriptor) {
        return dataByDescriptor.get(descriptor);
    }

    // No error checking! It is assumed the caller checks before!
    public void register(DimensionData dd) {
        data.put(dd.getId(), dd);
        dataByDescriptor.put(dd.getDescriptor(), dd);
        markDirty();
    }

    @Override
    public void read(CompoundNBT tag) {
        ListNBT dimensions = tag.getList("dimensions", Constants.NBT.TAG_COMPOUND);
        data.clear();
        dataByDescriptor.clear();
        for (INBT inbt : dimensions) {
            CompoundNBT dtag = (CompoundNBT) inbt;
            DimensionData dd = new DimensionData(dtag);
            data.put(dd.getId(), dd);
            dataByDescriptor.put(dd.getDescriptor(), dd);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT tag = new CompoundNBT();

        ListNBT list = new ListNBT();
        for (Map.Entry<ResourceLocation, DimensionData> entry : data.entrySet()) {
            CompoundNBT dtag = new CompoundNBT();
            entry.getValue().write(dtag);
            list.add(dtag);
        }

        tag.put("dimensions", list);
        return tag;
    }
}
