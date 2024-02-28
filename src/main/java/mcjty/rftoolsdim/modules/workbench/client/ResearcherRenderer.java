package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.ResearcherTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;

public class ResearcherRenderer implements BlockEntityRenderer<ResearcherTileEntity> {

    public static final ResourceLocation LIGHT = new ResourceLocation(RFToolsDim.MODID, "block/light");

    public ResearcherRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ResearcherTileEntity te, float v, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        te.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            matrixStack.pushPose();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            long millis = System.currentTimeMillis();
            ItemStack stack = h.getStackInSlot(ResearcherTileEntity.SLOT_IN);
            if (!stack.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.scale(.5f, .5f, .5f);
                matrixStack.translate(1f, 2.1f, 1f);
                float angle = ((millis / 45) % 360);
                RenderHelper.rotateYP(matrixStack, angle);
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, RenderHelper.MAX_BRIGHTNESS, combinedOverlay, matrixStack, buffer, Minecraft.getInstance().level, 0);  // @todo 1.18 last parameter?
                matrixStack.popPose();

                matrixStack.translate(0, 0.5f, 0);
                RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, LIGHT, RenderSettings.builder()
                        .color(255, 255, 255)
                        .renderType(CustomRenderTypes.TRANSLUCENT_ADD)
                        .alpha(128)
                        .build());
            }

            matrixStack.popPose();
        });
    }

    public static void register() {
        BlockEntityRenderers.register(WorkbenchModule.TYPE_RESEARCHER.get(), ResearcherRenderer::new);
    }

}
