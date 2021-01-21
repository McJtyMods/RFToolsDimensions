package mcjty.rftoolsdim.modules.blob.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DimensionalBlobEntity extends MonsterEntity {

    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;

    public DimensionalBlobEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D);
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
