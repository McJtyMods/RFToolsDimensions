package mcjty.rftoolsdim.modules.essences.client;

import mcjty.rftoolsdim.modules.essences.EssencesModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(EssencesModule.BLOCK_ABSORBER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(EssencesModule.FLUID_ABSORBER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(EssencesModule.BIOME_ABSORBER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(EssencesModule.STRUCTURE_ABSORBER.get(), RenderType.cutout());
    }
}
