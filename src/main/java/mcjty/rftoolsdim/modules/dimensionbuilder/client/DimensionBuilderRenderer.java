package mcjty.rftoolsdim.modules.dimensionbuilder.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class DimensionBuilderRenderer extends TileEntityRenderer<DimensionBuilderTileEntity> {

    public static final ResourceLocation STAGES = new ResourceLocation(RFToolsDim.MODID, "block/dimensionstages");

    public DimensionBuilderRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(DimensionBuilderTileEntity te, float v, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        int errorMode = te.getErrorMode();
        int r;
        int g;
        int b;
        if (errorMode != 0) {
            r = 255;
            g = b = 0;
        } else {
            r = g = b = 255;
        }

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            if (!h.getStackInSlot(DimensionBuilderTileEntity.SLOT_DIMENSION_TAB).isEmpty()) {
                matrixStack.pushPose();
                matrixStack.translate(0.1, 1.2, 0.1);
                matrixStack.scale(0.8f, 0.8f, 0.8f);
                RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, STAGES, RenderSettings.builder()
                        .color(r, g, b)
                        .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
//                        .renderType(RenderType.getTranslucent())
                        .alpha(128)
                        .build());
                matrixStack.popPose();
            }
        });
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(DimensionBuilderModule.TYPE_DIMENSION_BUILDER.get(), DimensionBuilderRenderer::new);
    }

}
