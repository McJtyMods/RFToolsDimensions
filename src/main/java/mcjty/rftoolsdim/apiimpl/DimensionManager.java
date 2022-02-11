package mcjty.rftoolsdim.apiimpl;

import mcjty.rftoolsbase.api.dimension.IDimensionInformation;
import mcjty.rftoolsbase.api.dimension.IDimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class DimensionManager implements IDimensionManager {

    @Override
    public IDimensionInformation getDimensionInformation(Level world, ResourceLocation id) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(world);
        return mgr.getData(id);
    }
}
