package mcjty.rftoolsdim.modules.blob.client;

import com.google.common.collect.ImmutableList;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nonnull;

public class DimensionalBlobModel<T extends DimensionalBlobEntity> extends ListModel<T> {
    private final ModelPart bodies;
    private final ModelPart rightEye;
    private final ModelPart leftEye;
    private final ModelPart mouth;

    public DimensionalBlobModel() {
        int slimeBodyTexOffY = 16;
        this.bodies = new ModelPart(this, 0, slimeBodyTexOffY);
        this.rightEye = new ModelPart(this, 32, 0);
        this.leftEye = new ModelPart(this, 32, 4);
        this.mouth = new ModelPart(this, 32, 8);
        float offset = 0;
        this.bodies.addBox(-3.0F, 17.0F + offset, -3.0F, 6.0F, 6.0F, 6.0F);
        this.rightEye.addBox(-3.25F, 18.0F + offset, -3.5F, 2.0F, 2.0F, 2.0F);
        this.leftEye.addBox(1.25F, 18.0F + offset, -3.5F, 2.0F, 2.0F, 2.0F);
        this.mouth.addBox(0.0F, 21.0F + offset, -3.5F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setupAnim(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    @Nonnull
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(this.bodies, this.rightEye, this.leftEye, this.mouth);
    }
}