package mcjty.rftoolsdim.modules.dimensionbuilder;

import mcjty.rftoolsdim.modules.dimensionbuilder.client.DimensionBuilderRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(DimensionBuilderRenderer.STAGES);
    }

}
