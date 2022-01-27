package mcjty.rftoolsdim.setup;

import mcjty.rftoolsdim.dimension.DimensionRegistry;
import mcjty.rftoolsdim.dimension.client.RFToolsDimensionSpecialEffects;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
        RFToolsDimensionSpecialEffects effects = new RFToolsDimensionSpecialEffects();
        DimensionSpecialEffects.EFFECTS.put(DimensionRegistry.RFTOOLS_EFFECTS_ID, effects);
    }

    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientDimensionData.get().clear();
    }

    public static void onDimensionChange(ClientPlayerNetworkEvent.RespawnEvent event) {
        RFToolsDimensionSpecialEffects.clearCache();
    }
}
