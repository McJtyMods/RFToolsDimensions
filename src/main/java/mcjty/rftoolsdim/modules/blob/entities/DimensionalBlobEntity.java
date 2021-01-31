package mcjty.rftoolsdim.modules.blob.entities;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionalBlobEntity extends MonsterEntity {

    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;

    private final DimletRarity rarity;
    private AxisAlignedBB targetBox = null;

    private final static EntityPredicate PREDICATE = new EntityPredicate()
            .setLineOfSiteRequired();

    public DimensionalBlobEntity(EntityType<? extends MonsterEntity> type, World worldIn, DimletRarity rarity) {
        super(type, worldIn);
        this.rarity = rarity;
        calculateTargetBox(getBoundingBox());
    }

    private static int getDefaultMaxHealth(DimletRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 20;
            case UNCOMMON:
                return 50;
            case RARE:
                return 200;
            case LEGENDARY:
                return 1000000;
        }
        return 20;
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(DimletRarity rarity) {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                .createMutableAttribute(Attributes.MAX_HEALTH, getDefaultMaxHealth(rarity));
    }

    @Override
    public float getRenderScale() {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                return 1.0f;
            case RARE:
                return 1.3f;
            case LEGENDARY:
                return 1.8f;
        }
        return 1.0f;
    }

    @Override
    public void setBoundingBox(AxisAlignedBB bb) {
        super.setBoundingBox(bb);
        calculateTargetBox(bb);
    }

    private void calculateTargetBox(AxisAlignedBB bb) {
        if (rarity != null) {
            double radius = 1.0;
            switch (rarity) {
                case COMMON:
                case UNCOMMON:
                    radius = 5.0;
                    break;
                case RARE:
                    radius = 9.0;
                    break;
                case LEGENDARY:
                    radius = 15.0;
                    break;
            }
            targetBox = bb.grow(radius);
        }
    }


    private void infectPlayer(PlayerEntity player) {
        player.addPotionEffect(new EffectInstance(Effects.HUNGER, 100));
        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100));
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                break;
            case RARE:
                player.addPotionEffect(new EffectInstance(Effects.POISON, 100));
                break;
            case LEGENDARY:
                player.addPotionEffect(new EffectInstance(Effects.POISON, 100));
                player.addPotionEffect(new EffectInstance(Effects.WITHER, 100));
                break;
        }
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
        } else {
            if (rand.nextFloat() < .05) {
                List<PlayerEntity> players = world.getTargettablePlayersWithinAABB(PREDICATE, this, targetBox);
                for (PlayerEntity player : players) {
                    infectPlayer(player);
                }

            }
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
