package mcjty.rftoolsdim.modules.blob;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.blob.client.DimensionalBlobModel;
import mcjty.rftoolsdim.modules.blob.client.DimensionalBlobRender;
import net.minecraft.client.model.SlimeModel;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import net.neoforged.neoforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // @todo 1.18 we also need the inner layer
        event.registerLayerDefinition(DimensionalBlobModel.BLOB_LAYER, SlimeModel::createOuterBodyLayer);

    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BlobModule.DIMENSIONAL_BLOB_COMMON.get(), DimensionalBlobRender::new);
        event.registerEntityRenderer(BlobModule.DIMENSIONAL_BLOB_RARE.get(), DimensionalBlobRender::new);
        event.registerEntityRenderer(BlobModule.DIMENSIONAL_BLOB_LEGENDARY.get(), DimensionalBlobRender::new);
    }

}

