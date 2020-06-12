package mcjty.rftoolsdim.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;

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

        if (rand.nextFloat() < 0.05f) {
            this.squishAmount = -0.5F;
        } else if (rand.nextFloat() < 0.05f) {
            this.squishAmount = 1.0f;
        }

        this.squishAmount *= 0.6F;
    }
}
