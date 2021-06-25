package mcjty.rftoolsdim.modules.blob.entities;

import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.modules.blob.BlobConfig;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
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

    private float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;

    private final DimletRarity rarity;
    private AxisAlignedBB targetBox = null;

    private int tickCounter = 5;

    private final static EntityPredicate PREDICATE = new EntityPredicate()
            .allowUnseeable();

    public DimensionalBlobEntity(EntityType<? extends MonsterEntity> type, World worldIn, DimletRarity rarity) {
        super(type, worldIn);
        this.rarity = rarity;
        calculateTargetBox(getBoundingBox());
    }

    private static int getDefaultMaxHealth(DimletRarity rarity) {
        // There is no UNCOMMON mob
        switch (rarity) {
            case COMMON:
                return BlobConfig.BLOB_COMMON_HEALTH.get();
            case RARE:
                return BlobConfig.BLOB_RARE_HEALTH.get();
            case LEGENDARY:
                return BlobConfig.BLOB_LEGENDARY_HEALTH.get();
            case UNCOMMON:
                throw new IllegalStateException("There is no uncommon blob!");
        }
        throw new IllegalStateException("Unknown blob type!");
    }

    private int getRegenLevel() {
        switch (rarity) {
            case COMMON:
                return BlobConfig.BLOB_COMMON_REGEN.get();
            case UNCOMMON:
                throw new IllegalStateException("There is no uncommon blob!");
            case RARE:
                return BlobConfig.BLOB_RARE_REGEN.get();
            case LEGENDARY:
                return BlobConfig.BLOB_LEGENDARY_REGEN.get();
        }
        throw new IllegalStateException("Unknown blob type!");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level.isClientSide) {
            DimensionData data = PersistantDimensionManager.get(level).getData(level.dimension().location());
            if (data != null) {
                if (data.getEnergy() >= BlobConfig.BLOB_REGENERATION_LEVEL.get()) {
                    tickCounter--;
                    if (tickCounter <= 0) {
                        tickCounter = 5;
                        addEffect(new EffectInstance(Effects.REGENERATION, 20, getRegenLevel()));
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ModifiableAttributeInstance attr = getAttributes().getInstance(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(getDefaultMaxHealth(rarity));
            setHealth(getDefaultMaxHealth(rarity));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(DimletRarity rarity) {
        // Note, since this is called before config is init the default max health here will be
        // the default from the config. In onInitialSpawn() this is later corrected
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.MAX_HEALTH, getDefaultMaxHealth(rarity));
    }

    @Override
    public float getScale() {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                return 1.5f;
            case RARE:
                return 2.2f;
            case LEGENDARY:
                return 4.7f;
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
            targetBox = bb.inflate(radius);
        }
    }


    private void infectPlayer(PlayerEntity player) {
        player.addEffect(new EffectInstance(Effects.HUNGER, 100));
        player.addEffect(new EffectInstance(Effects.WEAKNESS, 100));
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                break;
            case RARE:
                player.addEffect(new EffectInstance(Effects.POISON, 100));
                break;
            case LEGENDARY:
                player.addEffect(new EffectInstance(Effects.POISON, 100));
                player.addEffect(new EffectInstance(Effects.WITHER, 100));
                break;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
            this.prevSquishFactor = this.squishFactor;

            if (random.nextFloat() < 0.03f) {
                this.squishAmount = -0.5F;
            } else if (random.nextFloat() < 0.03f) {
                this.squishAmount = 1.0f;
            }

            this.squishAmount *= 0.6F;
        } else {
            if (random.nextFloat() < .05) {
                List<PlayerEntity> players = level.getNearbyPlayers(PREDICATE, this, targetBox);
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


}
