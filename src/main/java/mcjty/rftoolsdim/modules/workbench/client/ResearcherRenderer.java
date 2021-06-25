package mcjty.rftoolsdim.modules.workbench.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.modules.workbench.blocks.ResearcherTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class ResearcherRenderer extends TileEntityRenderer<ResearcherTileEntity> {

    public static final ResourceLocation LIGHT = new ResourceLocation(RFToolsDim.MODID, "block/light");

    public ResearcherRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ResearcherTileEntity te, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            matrixStack.pushPose();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            long millis = System.currentTimeMillis();
            ItemStack stack = h.getStackInSlot(ResearcherTileEntity.SLOT_IN);
            if (!stack.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.scale(.5f, .5f, .5f);
                matrixStack.translate(1f, 2.1f, 1f);
                float angle = (float) ((millis / 45) % 360);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
                itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, 0xf000f0, combinedOverlay, matrixStack, buffer);
                matrixStack.popPose();

                matrixStack.translate(0, 0.5f, 0);
                RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, LIGHT, RenderSettings.builder()
                        .color(255, 255, 255)
                        .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
                        .alpha(128)
                        .build());
            }

            matrixStack.popPose();
        });
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(WorkbenchModule.TYPE_RESEARCHER.get(), ResearcherRenderer::new);
    }

}
