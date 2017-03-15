package mcjty.rftoolsdim.dimensions;

import mcjty.lib.tools.WorldTools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import mcjty.rftoolsdim.dimensions.dimlets.types.Patreons;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class DimensionTickEvent {
    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    private static Random random = new Random();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START) {
            return;
        }
        if (evt.world.provider.getDimension() != 0) {
            return;
        }
        counter--;
        if (counter <= 0) {
            counter = MAXTICKS;

            counterEffects--;
            boolean doEffects = false;
            if (counterEffects <= 0) {
                counterEffects = EFFECTS_MAX;
                doEffects = true;
            }
            serverTick(evt.world, doEffects);
        }
    }

    private void serverTick(World entityWorld, boolean doEffects) {
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(entityWorld);

        if (!dimensionManager.getDimensions().isEmpty()) {
            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(entityWorld);

            for (Map.Entry<Integer, DimensionDescriptor> entry : dimensionManager.getDimensions().entrySet()) {
                Integer id = entry.getKey();
                // If there is an activity probe we only drain power if the dimension is loaded (a player is there or a chunkloader)
                DimensionInformation information = dimensionManager.getDimensionInformation(id);
                if (!information.isCheater()) {
                    WorldServer world = DimensionManager.getWorld(id);

                    // Power handling.
                    if ((world != null && world.getChunkProvider().getLoadedChunkCount() > 0) || information.getProbeCounter() == 0) {
                        handlePower(doEffects, dimensionStorage, entry, id, information);
                    }

                    // Special effect handling.
                    if (world != null && !world.playerEntities.isEmpty()) {
                        handleRandomEffects(world, information);
                    }
                }
            }

            dimensionStorage.save(entityWorld);
        }
    }

    private void handlePower(boolean doEffects, DimensionStorage dimensionStorage, Map.Entry<Integer, DimensionDescriptor> entry, Integer id, DimensionInformation information) {
        int cost = 0;
        if (PowerConfiguration.dimensionDifficulty != -1) {
            cost = information.getActualRfCost();
            if (cost == 0) {
                cost = entry.getValue().getRfMaintainCost();
            }
        }

        int power = dimensionStorage.getEnergyLevel(id);
        power -= cost * MAXTICKS;
        if (power < 0) {
            power = 0;
        }

        handleLowPower(id, power, doEffects, cost);
        if (doEffects && power > 0) {
            handleEffectsForDimension(power, id, information);
        }

        dimensionStorage.setEnergyLevel(id, power);
    }

    private void handleRandomEffects(WorldServer world, DimensionInformation information) {
        // The world is loaded and there are players there.
        if (information.isPatreonBitSet(Patreons.PATREON_FIREWORKS)) {
            handleFireworks(world);
        }
        if (information.isPatreonBitSet(Patreons.PATREON_TOMWOLF)) {
            handleHowlingWolf(world, information);
        }
    }

    private static int t = 0;

    private void handleHowlingWolf(WorldServer world, DimensionInformation information) {
        if (information.getCelestialAngle() == null) {
            // We don't have fixed time.
            float a = world.getCelestialAngle(1.0f);
            t--;
            if (t <= 0) {
                t = 0;
            }
            if (Math.abs(a - 0.5f) < 0.05f) {
                if (t <= 0) {
                    playHowl(world);
                    t = 40;
                }
            }
        } else {
            // We have fixed time so just play the sound at random moments
            if (random.nextFloat() < 0.001) {
                playHowl(world);
            }
        }
    }

    private void playHowl(WorldServer world) {
        for (Object playerEntity : world.playerEntities) {
            EntityPlayer player = (EntityPlayer) playerEntity;
            //@todo
//            world.playSound(player.posX, player.posY, player.posZ, RFToolsDim.MODID+":wolfhowl", 1.0f, 1.0f);
        }
    }

    private void handleFireworks(WorldServer world) {
        if (random.nextFloat() < 0.05) {
            // Calculate a bounding box for all players in the world.
            double minPosX = 1000000000.0f;
            double minPosZ = 1000000000.0f;
            double maxPosX = -1000000000.0f;
            double maxPosZ = -1000000000.0f;
            for (Object playerEntity : world.playerEntities) {
                EntityPlayer player = (EntityPlayer) playerEntity;
                if (player.posX > maxPosX) {
                    maxPosX = player.posX;
                }
                if (player.posX < minPosX) {
                    minPosX = player.posX;
                }
                if (player.posZ > maxPosZ) {
                    maxPosZ = player.posZ;
                }
                if (player.posZ < minPosZ) {
                    minPosZ = player.posZ;
                }
            }
            double posX = random.nextFloat() * (maxPosX - minPosX + 60.0f) + minPosX - 30.0f;
            double posZ = random.nextFloat() * (maxPosZ - minPosZ + 60.0f) + minPosZ - 30.0f;

            ItemStack fireworkStack = new ItemStack(Items.FIREWORKS);
            NBTTagCompound tag = new NBTTagCompound();

            NBTTagCompound fireworks = new NBTTagCompound();
            fireworks.setByte("Flight", (byte) 2);

            NBTTagList explosions = new NBTTagList();
            explosions.appendTag(makeExplosion(tag));
            fireworks.setTag("Explosions", explosions);

            tag.setTag("Fireworks", fireworks);

            fireworkStack.setTagCompound(tag);

            BlockPos newpos = world.getTopSolidOrLiquidBlock(new BlockPos((int) posX, 0, (int) posZ));
            if (newpos.getY() == -1) {
                newpos = new BlockPos(newpos.getX(), 64, newpos.getZ());
            } else {
                newpos = new BlockPos(newpos.getX(), newpos.getY() + 3, newpos.getZ());
            }
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(world, newpos.getX(), newpos.getY(), newpos.getZ(), fireworkStack);
            WorldTools.spawnEntity(world, entityfireworkrocket);
        }
    }

    private static int[] colors = new int[] {
            0xff0000, 0x00ff00, 0x0000ff, 0xffff00, 0xff00ff, 0x00ffff, 0xffffff,
            0xff5555, 0x55ff55, 0x5555ff, 0xffff55, 0xff55ff, 0x55ffff, 0x555555
    };

    private NBTTagCompound makeExplosion(NBTTagCompound tag) {
        NBTTagCompound explosion = new NBTTagCompound();
        explosion.setBoolean("Flicker", true);
        explosion.setBoolean("Tail", true);
        explosion.setByte("Type", (byte) (random.nextInt(4)+1));
        explosion.setIntArray("Colors", new int[]{colors[random.nextInt(colors.length)], colors[random.nextInt(colors.length)], colors[random.nextInt(colors.length)]});
        tag.setTag("Explosion", explosion);
        return explosion;
    }

    public static Potion harm;
    public static Potion hunger;
    public static Potion digSlowdown;
    public static Potion digSpeed;
    public static Potion moveSlowdown;
    public static Potion moveSpeed;
    public static Potion weakness;
    public static Potion poison;
    public static Potion wither;
    public static Potion regeneration;
    public static Potion damageBoost;
    public static Potion heal;
    public static Potion jump;
    public static Potion confusion;
    public static Potion resistance;
    public static Potion fireResistance;
    public static Potion waterBreathing;
    public static Potion invisibility;
    public static Potion blindness;
    public static Potion nightVision;
    public static Potion healthBoost;
    public static Potion absorption;
    public static Potion saturation;

    static final Map<EffectType,Potion> effectsMap = new HashMap<>();
    static final Map<EffectType,Integer> effectAmplifierMap = new HashMap<>();

    private static void getPotions() {
        if (harm == null) {
            harm = Potion.REGISTRY.getObject(new ResourceLocation("instant_damage"));
            hunger = Potion.REGISTRY.getObject(new ResourceLocation("hunger"));
            digSpeed = Potion.REGISTRY.getObject(new ResourceLocation("haste"));
            digSlowdown = Potion.REGISTRY.getObject(new ResourceLocation("mining_fatigue"));
            moveSlowdown = Potion.REGISTRY.getObject(new ResourceLocation("slowness"));
            moveSpeed = Potion.REGISTRY.getObject(new ResourceLocation("speed"));
            weakness = Potion.REGISTRY.getObject(new ResourceLocation("weakness"));
            poison = Potion.REGISTRY.getObject(new ResourceLocation("poison"));
            wither = Potion.REGISTRY.getObject(new ResourceLocation("wither"));
            regeneration = Potion.REGISTRY.getObject(new ResourceLocation("regeneration"));
            damageBoost = Potion.REGISTRY.getObject(new ResourceLocation("strength"));
            heal = Potion.REGISTRY.getObject(new ResourceLocation("instant_health"));
            jump = Potion.REGISTRY.getObject(new ResourceLocation("jump_boost"));
            confusion = Potion.REGISTRY.getObject(new ResourceLocation("nausea"));
            resistance = Potion.REGISTRY.getObject(new ResourceLocation("resistance"));
            fireResistance = Potion.REGISTRY.getObject(new ResourceLocation("fire_resistance"));
            waterBreathing = Potion.REGISTRY.getObject(new ResourceLocation("water_breathing"));
            invisibility = Potion.REGISTRY.getObject(new ResourceLocation("invisibility"));
            blindness = Potion.REGISTRY.getObject(new ResourceLocation("blindness"));
            nightVision = Potion.REGISTRY.getObject(new ResourceLocation("night_vision"));
            healthBoost = Potion.REGISTRY.getObject(new ResourceLocation("health_boost"));
            absorption = Potion.REGISTRY.getObject(new ResourceLocation("absorption"));
            saturation = Potion.REGISTRY.getObject(new ResourceLocation("saturation"));

            effectsMap.put(EffectType.EFFECT_POISON, poison);
            effectsMap.put(EffectType.EFFECT_POISON2, poison);
            effectAmplifierMap.put(EffectType.EFFECT_POISON2, 1);
            effectsMap.put(EffectType.EFFECT_POISON3, poison);
            effectAmplifierMap.put(EffectType.EFFECT_POISON3, 2);

            effectsMap.put(EffectType.EFFECT_REGENERATION, regeneration);
            effectsMap.put(EffectType.EFFECT_REGENERATION2, regeneration);
            effectAmplifierMap.put(EffectType.EFFECT_REGENERATION2, 1);
            effectsMap.put(EffectType.EFFECT_REGENERATION3, regeneration);
            effectAmplifierMap.put(EffectType.EFFECT_REGENERATION3, 2);

            effectsMap.put(EffectType.EFFECT_MOVESLOWDOWN, moveSlowdown);
            effectsMap.put(EffectType.EFFECT_MOVESLOWDOWN2, moveSlowdown);
            effectAmplifierMap.put(EffectType.EFFECT_MOVESLOWDOWN2, 1);
            effectsMap.put(EffectType.EFFECT_MOVESLOWDOWN3, moveSlowdown);
            effectAmplifierMap.put(EffectType.EFFECT_MOVESLOWDOWN3, 2);
            effectsMap.put(EffectType.EFFECT_MOVESLOWDOWN4, moveSlowdown);
            effectAmplifierMap.put(EffectType.EFFECT_MOVESLOWDOWN4, 3);

            effectsMap.put(EffectType.EFFECT_MOVESPEED, moveSpeed);
            effectsMap.put(EffectType.EFFECT_MOVESPEED2, moveSpeed);
            effectAmplifierMap.put(EffectType.EFFECT_MOVESPEED2, 1);
            effectsMap.put(EffectType.EFFECT_MOVESPEED3, moveSpeed);
            effectAmplifierMap.put(EffectType.EFFECT_MOVESPEED3, 2);

            effectsMap.put(EffectType.EFFECT_DIGSLOWDOWN, digSlowdown);
            effectsMap.put(EffectType.EFFECT_DIGSLOWDOWN2, digSlowdown);
            effectAmplifierMap.put(EffectType.EFFECT_DIGSLOWDOWN2, 1);
            effectsMap.put(EffectType.EFFECT_DIGSLOWDOWN3, digSlowdown);
            effectAmplifierMap.put(EffectType.EFFECT_DIGSLOWDOWN3, 2);
            effectsMap.put(EffectType.EFFECT_DIGSLOWDOWN4, digSlowdown);
            effectAmplifierMap.put(EffectType.EFFECT_DIGSLOWDOWN4, 3);

            effectsMap.put(EffectType.EFFECT_DIGSPEED, digSpeed);
            effectsMap.put(EffectType.EFFECT_DIGSPEED2, digSpeed);
            effectAmplifierMap.put(EffectType.EFFECT_DIGSPEED2, 1);
            effectsMap.put(EffectType.EFFECT_DIGSPEED3, digSpeed);
            effectAmplifierMap.put(EffectType.EFFECT_DIGSPEED3, 2);

            effectsMap.put(EffectType.EFFECT_DAMAGEBOOST, damageBoost);
            effectsMap.put(EffectType.EFFECT_DAMAGEBOOST2, damageBoost);
            effectAmplifierMap.put(EffectType.EFFECT_DAMAGEBOOST2, 1);
            effectsMap.put(EffectType.EFFECT_DAMAGEBOOST3, damageBoost);
            effectAmplifierMap.put(EffectType.EFFECT_DAMAGEBOOST3, 2);

            effectsMap.put(EffectType.EFFECT_INSTANTHEALTH, heal);
            effectsMap.put(EffectType.EFFECT_HARM, harm);

            effectsMap.put(EffectType.EFFECT_JUMP, jump);
            effectsMap.put(EffectType.EFFECT_JUMP2, jump);
            effectAmplifierMap.put(EffectType.EFFECT_JUMP2, 1);
            effectsMap.put(EffectType.EFFECT_JUMP3, jump);
            effectAmplifierMap.put(EffectType.EFFECT_JUMP3, 2);

            effectsMap.put(EffectType.EFFECT_CONFUSION, confusion);

            effectsMap.put(EffectType.EFFECT_RESISTANCE, resistance);
            effectsMap.put(EffectType.EFFECT_RESISTANCE2, resistance);
            effectAmplifierMap.put(EffectType.EFFECT_RESISTANCE2, 1);
            effectsMap.put(EffectType.EFFECT_RESISTANCE3, resistance);
            effectAmplifierMap.put(EffectType.EFFECT_RESISTANCE3, 2);

            effectsMap.put(EffectType.EFFECT_FIRERESISTANCE, fireResistance);
            effectsMap.put(EffectType.EFFECT_WATERBREATHING, waterBreathing);
            effectsMap.put(EffectType.EFFECT_INVISIBILITY, invisibility);
            effectsMap.put(EffectType.EFFECT_BLINDNESS, blindness);
            effectsMap.put(EffectType.EFFECT_NIGHTVISION, nightVision);

            effectsMap.put(EffectType.EFFECT_HUNGER, hunger);
            effectsMap.put(EffectType.EFFECT_HUNGER2, hunger);
            effectAmplifierMap.put(EffectType.EFFECT_HUNGER2, 1);
            effectsMap.put(EffectType.EFFECT_HUNGER3, hunger);
            effectAmplifierMap.put(EffectType.EFFECT_HUNGER3, 2);

            effectsMap.put(EffectType.EFFECT_WEAKNESS, weakness);
            effectsMap.put(EffectType.EFFECT_WEAKNESS2, weakness);
            effectAmplifierMap.put(EffectType.EFFECT_WEAKNESS2, 1);
            effectsMap.put(EffectType.EFFECT_WEAKNESS3, weakness);
            effectAmplifierMap.put(EffectType.EFFECT_WEAKNESS3, 2);

            effectsMap.put(EffectType.EFFECT_WITHER, wither);
            effectsMap.put(EffectType.EFFECT_WITHER2, wither);
            effectAmplifierMap.put(EffectType.EFFECT_WITHER2, 1);
            effectsMap.put(EffectType.EFFECT_WITHER3, wither);
            effectAmplifierMap.put(EffectType.EFFECT_WITHER3, 2);

            effectsMap.put(EffectType.EFFECT_HEALTHBOOST, healthBoost);
            effectsMap.put(EffectType.EFFECT_HEALTHBOOST2, healthBoost);
            effectAmplifierMap.put(EffectType.EFFECT_HEALTHBOOST2, 1);
            effectsMap.put(EffectType.EFFECT_HEALTHBOOST3, healthBoost);
            effectAmplifierMap.put(EffectType.EFFECT_HEALTHBOOST3, 2);

            effectsMap.put(EffectType.EFFECT_ABSORPTION, absorption);
            effectsMap.put(EffectType.EFFECT_ABSORPTION2, absorption);
            effectAmplifierMap.put(EffectType.EFFECT_ABSORPTION2, 1);
            effectsMap.put(EffectType.EFFECT_ABSORPTION3, absorption);
            effectAmplifierMap.put(EffectType.EFFECT_ABSORPTION3, 2);

            effectsMap.put(EffectType.EFFECT_SATURATION, saturation);
            effectsMap.put(EffectType.EFFECT_SATURATION2, saturation);
            effectAmplifierMap.put(EffectType.EFFECT_SATURATION2, 1);
            effectsMap.put(EffectType.EFFECT_SATURATION3, saturation);
            effectAmplifierMap.put(EffectType.EFFECT_SATURATION3, 2);
        }
    }


    private void handleEffectsForDimension(int power, int id, DimensionInformation information) {
        getPotions();
        WorldServer world = DimensionManager.getWorld(id);
        if (world != null) {
            Set<EffectType> effects = information.getEffectTypes();
            List<EntityPlayer> players = new ArrayList<EntityPlayer>(world.playerEntities);
            for (EntityPlayer player : players) {
                for (EffectType effect : effects) {
                    Potion potionEffect = effectsMap.get(effect);
                    if (potionEffect != null) {
                        Integer amplifier = effectAmplifierMap.get(effect);
                        if (amplifier == null) {
                            amplifier = 0;
                        }
                        player.addPotionEffect(new PotionEffect(potionEffect, EFFECTS_MAX*MAXTICKS*3, amplifier, true, true));
                    } else if (effect == EffectType.EFFECT_FLIGHT) {
//                        BuffProperties.addBuff(player, PlayerBuff.BUFF_FLIGHT, EFFECTS_MAX * MAXTICKS * 2);
                        // @todo
                    }
                }
                if (power < PowerConfiguration.DIMPOWER_WARN3) {
                    // We are VERY low on power. Start bad effects.
                    player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX*MAXTICKS, 4, true, true));
                    player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX*MAXTICKS, 4, true, true));
                    player.addPotionEffect(new PotionEffect(poison, EFFECTS_MAX*MAXTICKS, 2, true, true));
                    player.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX*MAXTICKS, 2, true, true));
                } else if (power < PowerConfiguration.DIMPOWER_WARN2) {
                    player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX*MAXTICKS, 2, true, true));
                    player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX*MAXTICKS, 2, true, true));
                    player.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX*MAXTICKS, 1, true, true));
                } else if (power < PowerConfiguration.DIMPOWER_WARN1) {
                    player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX*MAXTICKS, 0, true, true));
                    player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX*MAXTICKS, 0, true, true));
                }
            }

        }
    }

    private void handleLowPower(Integer id, int power, boolean doEffects, int phasedCost) {
        getPotions();
        if (power <= 0) {
            // We ran out of power!
            WorldServer world = DimensionManager.getWorld(id);
            if (world != null) {
                List<EntityPlayer> players = new ArrayList<EntityPlayer>(world.playerEntities);
                if (PowerConfiguration.dimensionDifficulty >= 1) {
                    for (EntityPlayer player : players) {
                        if (!RfToolsDimensionManager.checkValidPhasedFieldGenerator(player, true, phasedCost)) {
                            player.attackEntityFrom(new DamageSourcePowerLow("powerLow"), 1000000.0f);
                        } else {
                            if (doEffects && PowerConfiguration.phasedFieldGeneratorDebuf) {
                                player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 4, true, true));
                                player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                                player.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                            }
                        }
                    }
                } else {
                    Random random = new Random();
                    for (EntityPlayer player : players) {
                        if (!RfToolsDimensionManager.checkValidPhasedFieldGenerator(player, true, phasedCost)) {
                            WorldServer worldServerForDimension = player.getEntityWorld().getMinecraftServer().worldServerForDimension(GeneralConfiguration.spawnDimension);
                            int x = random.nextInt(2000) - 1000;
                            int z = random.nextInt(2000) - 1000;
                            int y = worldServerForDimension.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
                            if (y == -1) {
                                y = 63;
                            }

                            RFToolsDim.teleportationManager.teleportPlayer(player, GeneralConfiguration.spawnDimension, new BlockPos(x, y, z));
                        } else {
                            if (doEffects) {
                                player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 4, true, true));
                                player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX * MAXTICKS, 4, true, true));
                                player.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                            }
                        }
                    }
                }
            }
        }
    }

}
