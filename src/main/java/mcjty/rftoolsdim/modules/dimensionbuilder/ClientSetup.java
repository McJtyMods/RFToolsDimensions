package mcjty.rftoolsdim.modules.dimensionbuilder;

import mcjty.rftoolsdim.modules.dimensionbuilder.client.DimensionBuilderRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(DimensionBuilderRenderer.STAGES);
    }

}
