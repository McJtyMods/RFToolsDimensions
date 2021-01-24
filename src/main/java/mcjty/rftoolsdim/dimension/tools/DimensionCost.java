package mcjty.rftoolsdim.dimension.tools;

import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;

public class DimensionCost {

    public static int calculateCreateCost(DimensionDescriptor descriptor) {
        int cost = 0;
        for (DimletKey dimlet : descriptor.getDimlets()) {
            DimletSettings settings = DimletDictionary.get().getSettings(dimlet);
            cost += settings.getCreateCost();
        }
        return cost;
    }

}
