package mcjty.rftoolsdim.modules.dimensionbuilder.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.blocks.DimensionBuilderTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;

public class DimensionBuilderRenderer implements BlockEntityRenderer<DimensionBuilderTileEntity> {

    public static final ResourceLocation STAGES = new ResourceLocation(RFToolsDim.MODID, "block/dimensionstages");

    public DimensionBuilderRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(DimensionBuilderTileEntity te, float v, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

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

        te.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            if (!h.getStackInSlot(DimensionBuilderTileEntity.SLOT_DIMENSION_TAB).isEmpty()) {
                matrixStack.pushPose();
                matrixStack.translate(0.1, 1.2, 0.1);
                matrixStack.scale(0.8f, 0.8f, 0.8f);
                RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, STAGES, RenderSettings.builder()
                        .color(r, g, b)
                        .renderType(RenderType.translucent())
                        .alpha(128)
                        .build());
                matrixStack.popPose();
            }
        });
    }

    public static void register() {
        BlockEntityRenderers.register(DimensionBuilderModule.TYPE_DIMENSION_BUILDER.get(), DimensionBuilderRenderer::new);
    }

}
