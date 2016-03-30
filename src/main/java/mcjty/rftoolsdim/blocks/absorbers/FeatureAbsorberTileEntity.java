package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FeatureAbsorberTileEntity extends GenericTileEntity implements ITickable {

    private int absorbing = 0;
    private String featureName = null;

    @Override
    public void update() {
        if (worldObj.isRemote) {
            checkStateClient();
        } else {
            checkStateServer();
        }
    }


    protected void checkStateClient() {
        if (absorbing > 0) {
            Random rand = worldObj.rand;

            double u = rand.nextFloat() * 2.0f - 1.0f;
            double v = (float) (rand.nextFloat() * 2.0f * Math.PI);
            double x = Math.sqrt(1 - u * u) * Math.cos(v);
            double y = Math.sqrt(1 - u * u) * Math.sin(v);
            double z = u;
            double r = 1.0f;

            worldObj.spawnParticle(EnumParticleTypes.PORTAL, getPos().getX() + 0.5f + x * r, getPos().getY() + 0.5f + y * r, getPos().getZ() + 0.5f + z * r, -x, -y, -z);
        }
    }

    protected void checkStateServer() {
        if (absorbing > 0) {
            int dim = worldObj.provider.getDimension();
            DimensionInformation information = RfToolsDimensionManager.getDimensionManager(worldObj).getDimensionInformation(dim);
            if (information == null || !information.hasFeatureType(FeatureType.getFeatureById(featureName))) {
                return;
            }

            absorbing--;
            markDirtyClient();
        }
    }

    private String getRandomFeature(int dim) {
        DimensionInformation information = RfToolsDimensionManager.getDimensionManager(worldObj).getDimensionInformation(dim);
        if (information == null) {
            return null;
        }
        Set<FeatureType> featureTypes = information.getFeatureTypes();
        if (featureTypes.isEmpty()) {
            return null;
        }
        List<FeatureType> list = new ArrayList<FeatureType>(featureTypes);
        return list.get(worldObj.rand.nextInt(list.size())).getId();
    }

    public int getAbsorbing() {
        return absorbing;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void placeDown() {
        if (featureName == null) {
            int dim = worldObj.provider.getDimension();
            String feature = getRandomFeature(dim);
            if (feature == null) {
                featureName = null;
                absorbing = 0;
            } else if (!feature.equals(featureName)) {
                featureName = feature;
                absorbing = DimletConstructionConfiguration.maxFeatureAbsorbtion;
            }
            markDirty();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("absorbing", absorbing);
        if (featureName != null) {
            tagCompound.setString("feature", featureName);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        absorbing = tagCompound.getInteger("absorbing");
        if (tagCompound.hasKey("feature")) {
            featureName = tagCompound.getString("feature");
        } else {
            featureName = null;
        }
    }


}

