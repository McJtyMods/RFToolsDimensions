package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import mcjty.rftoolsdim.dimensions.dimlets.DimletKey;
import mcjty.rftoolsdim.dimensions.dimlets.DimletObjectMapping;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;

import java.util.Random;

public class TimeAbsorberTileEntity extends GenericTileEntity implements ITickable {

    private int absorbing = 0;
    private float angle = -1.0f;
    // For pulse detection.
    private boolean prevIn = false;
    private int registerTimeout = 0;

    @Override
    public void update() {
        if (getWorld().isRemote) {
            checkStateClient();
        } else {
            checkStateServer();
        }
    }


    private void checkStateClient() {
        if (absorbing > 0) {
            Random rand = getWorld().rand;

            double u = rand.nextFloat() * 2.0f - 1.0f;
            double v = (float) (rand.nextFloat() * 2.0f * Math.PI);
            double x = Math.sqrt(1 - u * u) * Math.cos(v);
            double y = Math.sqrt(1 - u * u) * Math.sin(v);
            double z = u;
            double r = 1.0f;

            getWorld().spawnParticle(EnumParticleTypes.PORTAL, getPos().getX() + 0.5f + x * r, getPos().getY() + 0.5f + y * r, getPos().getZ() + 0.5f + z * r, -x, -y, -z);
        }
    }

    private void checkStateServer() {
        boolean newvalue = powerLevel > 0;
        boolean pulse = newvalue && !prevIn;
        prevIn = newvalue;
        markDirty();

        if (registerTimeout > 0) {
            registerTimeout--;
            return;
        }

        if (pulse) {
            registerTime();
        }
    }

    public int getAbsorbing() {
        return absorbing;
    }

    public float getAngle() {
        return angle;
    }

    public int getRegisterTimeout() {
        return registerTimeout;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setBoolean("prevIn", prevIn);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("absorbing", absorbing);
        tagCompound.setFloat("angle", angle);
        tagCompound.setInteger("registerTimeout", registerTimeout);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        prevIn = tagCompound.getBoolean("prevIn");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        absorbing = tagCompound.getInteger("absorbing");
        if (tagCompound.hasKey("angle")) {
            angle = tagCompound.getFloat("angle");
        } else {
            angle = -1.0f;
        }
        registerTimeout = tagCompound.getInteger("registerTimeout");
    }

    private void registerTime() {
        if (getWorld().canBlockSeeSky(getPos())) {
            float a = getWorld().getCelestialAngle(1.0f);
            DimletKey bestDimlet = findBestTimeDimlet(a);
            float besta = DimletObjectMapping.getCelestialAngle(bestDimlet);

            if (angle < -0.001f) {
                angle = besta;
                absorbing = DimletConstructionConfiguration.maxTimeAbsorbtion-1;
            } else if (Math.abs(besta-angle) < 0.1f) {
                absorbing--;
                if (absorbing < 0) {
                    absorbing = 0;
                }
                registerTimeout = 3000;
            } else {
                absorbing++;
                if (absorbing >= DimletConstructionConfiguration.maxTimeAbsorbtion) {
                    absorbing = DimletConstructionConfiguration.maxTimeAbsorbtion-1;
                }
            }
        }
    }

    public static DimletKey findBestTimeDimlet(float a) {
        float bestDiff = 10000.0f;
        DimletKey bestDimlet = null;
        for (DimletKey dimlet : DimletObjectMapping.getCelestialAngles()) {
            Float celangle = DimletObjectMapping.getCelestialAngle(dimlet);
            if (celangle != null) {
                float diff = Math.abs(a - celangle);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestDimlet = dimlet;
                }
                diff = Math.abs((a-1.0f) - celangle);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestDimlet = dimlet;
                }
                diff = Math.abs((a+1.0f) - celangle);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestDimlet = dimlet;
                }
            }
        }
        return bestDimlet;
    }


}

