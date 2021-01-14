package mcjty.rftoolsdim.modules.blob.client;

import com.google.common.collect.ImmutableList;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DimensionalBlobModel<T extends DimensionalBlobEntity> extends SegmentedModel<T> {
    private final ModelRenderer bodies;
    private final ModelRenderer rightEye;
    private final ModelRenderer leftEye;
    private final ModelRenderer mouth;

    public DimensionalBlobModel() {
        int slimeBodyTexOffY = 16;
        this.bodies = new ModelRenderer(this, 0, slimeBodyTexOffY);
        this.rightEye = new ModelRenderer(this, 32, 0);
        this.leftEye = new ModelRenderer(this, 32, 4);
        this.mouth = new ModelRenderer(this, 32, 8);
        float offset = 0;
        if (slimeBodyTexOffY > 0) {
            this.bodies.addBox(-3.0F, 17.0F + offset, -3.0F, 6.0F, 6.0F, 6.0F);
            this.rightEye.addBox(-3.25F, 18.0F + offset, -3.5F, 2.0F, 2.0F, 2.0F);
            this.leftEye.addBox(1.25F, 18.0F + offset, -3.5F, 2.0F, 2.0F, 2.0F);
            this.mouth.addBox(0.0F, 21.0F + offset, -3.5F, 1.0F, 1.0F, 1.0F);
        } else {
            this.bodies.addBox(-4.0F, 16.0F + offset, -4.0F, 8.0F, 8.0F, 8.0F);
        }
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.bodies, this.rightEye, this.leftEye, this.mouth);
    }
}