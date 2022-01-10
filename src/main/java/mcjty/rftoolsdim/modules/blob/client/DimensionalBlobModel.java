package mcjty.rftoolsdim.modules.blob.client;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class DimensionalBlobModel<T extends DimensionalBlobEntity> extends HierarchicalModel<T> {

    public static final String BODY = "body";

    public static ModelLayerLocation BLOB_LAYER = new ModelLayerLocation(new ResourceLocation(RFToolsDim.MODID, "dimensional_bloc"), BODY);

    private final ModelPart root;

    public DimensionalBlobModel(ModelPart pRoot) {
        this.root = pRoot;
    }

    @Override
    public void setupAnim(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public ModelPart root() {
        return root;
    }
}