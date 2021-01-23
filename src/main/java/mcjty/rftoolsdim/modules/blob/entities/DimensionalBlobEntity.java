package mcjty.rftoolsdim.modules.blob.entities;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
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

    private final DimletRarity rarity;

    public DimensionalBlobEntity(EntityType<? extends MonsterEntity> type, World worldIn, DimletRarity rarity) {
        super(type, worldIn);
        this.rarity = rarity;
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote) {
            this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
            this.prevSquishFactor = this.squishFactor;

            if (rand.nextFloat() < 0.03f) {
                this.squishAmount = -0.5F;
            } else if (rand.nextFloat() < 0.03f) {
                this.squishAmount = 1.0f;
            }

            this.squishAmount *= 0.6F;
        }
    }

    public DimletRarity getRarity() {
        return rarity;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


}
