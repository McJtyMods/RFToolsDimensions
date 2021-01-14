package mcjty.rftoolsdim.modules.blob.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DimensionalBlobEntity extends MobEntity {

    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;

    public DimensionalBlobEntity(EntityType<? extends MobEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;

        if (rand.nextFloat() < 0.03f) {
            this.squishAmount = -0.5F;
        } else if (rand.nextFloat() < 0.03f) {
            this.squishAmount = 1.0f;
        }

        this.squishAmount *= 0.6F;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
