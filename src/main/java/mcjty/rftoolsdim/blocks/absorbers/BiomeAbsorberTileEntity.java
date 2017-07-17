package mcjty.rftoolsdim.blocks.absorbers;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.rftoolsdim.config.DimletConstructionConfiguration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class BiomeAbsorberTileEntity extends GenericTileEntity implements ITickable {

    private int absorbing = 0;
    private String biomeId = null;

    @Override
    public void update() {
        if (getWorld().isRemote) {
            checkStateClient();
        } else {
            checkStateServer();
        }
    }


    protected void checkStateClient() {
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

    protected void checkStateServer() {
        if (absorbing > 0) {
            Biome Biome = getWorld().getBiomeForCoordsBody(getPos());
            if (Biome == null || !Biome.getRegistryName().toString().equals(biomeId)) {
                return;
            }

            absorbing--;
            markDirtyClient();
        }
    }

    public int getAbsorbing() {
        return absorbing;
    }

    public String getBiomeName() {
        return getBiomeName(biomeId);
    }

    public static String getBiomeName(String biomeId) {
        if (biomeId == null) {
            return null;
        }
        Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(biomeId));
        return biome == null ? null : biome.biomeName;
    }

    public void placeDown() {
        if (biomeId == null) {
            Biome Biome = getWorld().getBiomeForCoordsBody(getPos());
            if (Biome == null) {
                biomeId = null;
                absorbing = 0;
            } else if (!Biome.getRegistryName().toString().equals(biomeId)) {
                biomeId = Biome.getRegistryName().toString();
                absorbing = DimletConstructionConfiguration.maxBiomeAbsorbtion;
            }
            markDirty();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("absorbing", absorbing);
        if (biomeId != null) {
            tagCompound.setString("biome", biomeId);
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
        if (tagCompound.hasKey("biome")) {
            biomeId = tagCompound.getString("biome");
        } else {
            biomeId = null;
        }
    }


}

