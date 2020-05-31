package mcjty.rftoolsdim.dimension;

import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class DimensionManager extends AbstractWorldData<DimensionManager> {

    private static final String NAME = "RFToolsDimensionManager";

    private long id = 0;

    public DimensionManager() {
        super(NAME);
    }

    public static DimensionManager get(World world) {
        return getData(world, DimensionManager::new, NAME);
    }

    @Override
    public void read(CompoundNBT nbt) {
        id = nbt.getLong("dimId");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putLong("dimId", id);
        return compound;
    }
}
