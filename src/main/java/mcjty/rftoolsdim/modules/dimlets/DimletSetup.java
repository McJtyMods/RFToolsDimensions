package mcjty.rftoolsdim.modules.dimlets;

import mcjty.rftoolsdim.modules.dimlets.items.BiomeModifierDimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.DimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.FeatureDimletItem;
import mcjty.rftoolsdim.modules.dimlets.items.TerrainDimletItem;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraftforge.fml.RegistryObject;

public class DimletSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<DimletItem> EMPTY_DIMLET = Registration.ITEMS.register("empty_dimlet", DimletItem::new);
    public static final RegistryObject<TerrainDimletItem> TERRAIN_DIMLET = Registration.ITEMS.register("terrain_dimlet", TerrainDimletItem::new);
    public static final RegistryObject<FeatureDimletItem> FEATURE_DIMLET = Registration.ITEMS.register("feature_dimlet", FeatureDimletItem::new);
    public static final RegistryObject<BiomeModifierDimletItem> BIOME_MODIFIER_DIMLET = Registration.ITEMS.register("biome_modifier_dimlet", BiomeModifierDimletItem::new);

}
