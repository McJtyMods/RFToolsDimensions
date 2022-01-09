package mcjty.rftoolsdim.dimension.data;

import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

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

    public PersistantDimensionManager() {
    }

    public PersistantDimensionManager(CompoundTag tag) {
        ListTag dimensions = tag.getList("dimensions", Tag.TAG_COMPOUND);
        data.clear();
        dataByDescriptor.clear();
        for (Tag inbt : dimensions) {
            CompoundTag dtag = (CompoundTag) inbt;
            DimensionData dd = new DimensionData(dtag);
            data.put(dd.getId(), dd);
            dataByDescriptor.put(dd.getDescriptor(), dd);
        }
    }

    @Nonnull
    public static PersistantDimensionManager get(Level world) {
        return getData(world, PersistantDimensionManager::new, PersistantDimensionManager::new, NAME);
    }

    public DimensionData getData(ResourceLocation id) {
        return data.get(id);
    }

    public DimensionData getData(DimensionDescriptor descriptor) {
        return dataByDescriptor.get(descriptor);
    }

    public Map<ResourceLocation, DimensionData> getData() {
        return data;
    }

    // No error checking! It is assumed the caller checks before!
    public void register(DimensionData dd) {
        data.put(dd.getId(), dd);
        dataByDescriptor.put(dd.getDescriptor(), dd);
        setDirty();
    }

    public void forget(ResourceLocation key) {
        DimensionData dd = data.get(key);
        data.remove(key);
        if (dd != null) {
            dataByDescriptor.remove(dd.getDescriptor());
        }
        setDirty();
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        CompoundTag tag = new CompoundTag();

        ListTag list = new ListTag();
        for (Map.Entry<ResourceLocation, DimensionData> entry : data.entrySet()) {
            CompoundTag dtag = new CompoundTag();
            entry.getValue().write(dtag);
            list.add(dtag);
        }

        tag.put("dimensions", list);
        return tag;
    }
}
