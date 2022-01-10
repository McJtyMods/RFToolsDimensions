package mcjty.rftoolsdim.modules.blob.client;


import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;


public class DimensionalBlobRender extends LivingEntityRenderer<DimensionalBlobEntity, DimensionalBlobModel<DimensionalBlobEntity>> {

    private static final ResourceLocation TEXTURE_COMMON = new ResourceLocation(RFToolsDim.MODID, "textures/entity/dimensional_blob_common.png");
    private static final ResourceLocation TEXTURE_RARE = new ResourceLocation(RFToolsDim.MODID, "textures/entity/dimensional_blob_rare.png");
    private static final ResourceLocation TEXTURE_LEGENDARY = new ResourceLocation(RFToolsDim.MODID, "textures/entity/dimensional_blob_legendary.png");

    public DimensionalBlobRender(EntityRendererProvider.Context context) {
        super(context, new DimensionalBlobModel<>(context.getModelSet().bakeLayer(DimensionalBlobModel.BLOB_LAYER)), 0.8f);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(DimensionalBlobEntity entity) {
        return switch (entity.getRarity()) {
            case COMMON -> TEXTURE_COMMON;
            case UNCOMMON -> TEXTURE_COMMON;  // Cannot happen
            case RARE -> TEXTURE_RARE;
            case LEGENDARY -> TEXTURE_LEGENDARY;
        };
    }


    @Override
    protected boolean shouldShowName(DimensionalBlobEntity entity) {
        return entity.hasCustomName() && super.shouldShowName(entity);
    }

    @Override
    protected void scale(DimensionalBlobEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.999F;
        matrixStackIn.scale(0.999F, 0.999F, 0.999F);
        matrixStackIn.translate(0.0D, 0.301F, 0.0D);
        float f1 = 2.0f * entitylivingbaseIn.getScale();
        float f2 = Mth.lerp(partialTickTime, entitylivingbaseIn.prevSquishFactor, entitylivingbaseIn.squishFactor) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        matrixStackIn.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }
}