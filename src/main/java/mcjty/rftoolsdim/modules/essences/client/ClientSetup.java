package mcjty.rftoolsdim.modules.essences.client;

import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(EssencesModule.BLOCK_ABSORBER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(EssencesModule.BIOME_ABSORBER.get(), RenderType.getCutout());
    }
}
