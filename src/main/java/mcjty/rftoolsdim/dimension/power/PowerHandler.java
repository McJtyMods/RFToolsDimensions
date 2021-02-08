package mcjty.rftoolsdim.dimension.power;

import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.network.PackagePropageDataToClients;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.PhasedFieldGenerator;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerHandler {

    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    public static long calculateDimensionPower(ResourceLocation id, ServerWorld world) {
        CompiledDescriptor descriptor = DimensionManager.get().getCompiledDescriptor(world, id);
        if (descriptor != null) {
            int cost = descriptor.getActualPowerCost();
            if (cost <= DimensionConfig.MIN_POWER_THRESSHOLD.get()) {
                return DimensionConfig.MAX_DIMENSION_POWER_MIN.get();
            } else if (cost >= DimensionConfig.MAX_POWER_THRESSHOLD.get()) {
                return DimensionConfig.MAX_DIMENSION_POWER_MAX.get();
            } else {
                long power = DimensionConfig.MAX_DIMENSION_POWER_MIN.get() + (cost - DimensionConfig.MIN_POWER_THRESSHOLD.get())
                        * (DimensionConfig.MAX_DIMENSION_POWER_MAX.get() - DimensionConfig.MAX_DIMENSION_POWER_MIN.get())
                        / (DimensionConfig.MAX_POWER_THRESSHOLD.get() - DimensionConfig.MIN_POWER_THRESSHOLD.get());
                power = power / DimensionConfig.POWER_MULTIPLES.get();
                power = power * DimensionConfig.POWER_MULTIPLES.get();
                return power;
            }
        } else {
            return DimensionConfig.MAX_DIMENSION_POWER_MAX.get();
        }
    }


    public void handlePower(World overworld) {
        counter--;
        if (counter <= 0) {
            counter = MAXTICKS;

            counterEffects--;
            boolean doEffects = false;
            if (counterEffects <= 0) {
                counterEffects = EFFECTS_MAX;
                doEffects = true;
            }
            handlePower(overworld, doEffects);

            sendOutPower(overworld);
        }
    }

    private void sendOutPower(World overworld) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(overworld);
        Map<ResourceLocation, ClientDimensionData.Power> powerMap = new HashMap<>();
        for (Map.Entry<ResourceLocation, DimensionData> entry : mgr.getData().entrySet()) {
            long energy = entry.getValue().getEnergy();
            powerMap.put(entry.getKey(), new ClientDimensionData.Power(energy, PowerHandler.calculateDimensionPower(entry.getKey(), (ServerWorld)  overworld)));
        }
        RFToolsDimMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PackagePropageDataToClients(powerMap,
                ((ServerWorld) overworld).getSeed()));
    }

    private void handlePower(World overworld, boolean doEffects) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(overworld);
        for (Map.Entry<ResourceLocation, DimensionData> entry : mgr.getData().entrySet()) {
            // Power handling.
            long power;
            ServerWorld world = DimensionId.fromResourceLocation(entry.getKey()).loadWorld(overworld);
            CompiledDescriptor compiledDescriptor = DimensionManager.get().getCompiledDescriptor(world);

            if (compiledDescriptor != null) {

                // If there is an activity probe we only drain power if the dimension is loaded (a player is there or a chunkloader)
                // @todo 1.16
//            if (!information.isCheater() && ((world != null && world.getChunkProvider().getLoadedChunkCount() > 0) || information.getProbeCounter() == 0)) {
                power = handlePowerDimension(doEffects, world, entry.getValue(), compiledDescriptor);
//            } else {
//                power = dimensionStorage.getEnergyLevel(id);
//            }

                // Special effect handling.
                if (doEffects && power > 0) {
                    handleEffectsForDimension(power, world, entry.getValue());
                }
                if (world != null && !world.getPlayers().isEmpty()) {
                    handleRandomEffects(world, entry.getValue());
                }
            }
        }
        mgr.save();
    }

    private long handlePowerDimension(boolean doEffects, ServerWorld world, DimensionData data, CompiledDescriptor compiledDescriptor) {
        int cost = compiledDescriptor.getActualPowerCost();
//        if (PowerConfiguration.dimensionDifficulty != -1) {   // @todo 1.16 config
//        }

        long power = data.getEnergy();
        power -= cost * MAXTICKS;
        if (power < 0) {
            power = 0;
        }

        handleLowPower(world, power, doEffects, cost);

        data.setEnergy(world, power);

        return power;
    }

    private void handleEffectsForDimension(long power, ServerWorld world, DimensionData information) {
        if (world != null) {
            // @todo 1.16 handle dimension specific effects
//            Set<EffectType> effects = information.getEffectTypes();
            List<ServerPlayerEntity> players = new ArrayList<>(world.getPlayers());
            for (ServerPlayerEntity player : players) {
                // @todo 1.16
//                for (EffectType effect : effects) {
//                    Potion potionEffect = effectsMap.get(effect);
//                    if (potionEffect != null) {
//                        Integer amplifier = effectAmplifierMap.get(effect);
//                        if (amplifier == null) {
//                            amplifier = 0;
//                        }
//                        player.addPotionEffect(new PotionEffect(potionEffect, EFFECTS_MAX*MAXTICKS*3, amplifier, true, true));
//                    } else if (effect == EffectType.EFFECT_FLIGHT) {
////                        BuffProperties.addBuff(player, PlayerBuff.BUFF_FLIGHT, EFFECTS_MAX * MAXTICKS * 2);
//                        // @todo
//                    }
//                }
                if (power < DimensionConfig.DIMPOWER_WARN3.get()) {
                    // We are VERY low on power. Start bad effects.
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, EFFECTS_MAX*MAXTICKS*2, 4, true, true));
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, EFFECTS_MAX*MAXTICKS*2, 4, true, true));
                    player.addPotionEffect(new EffectInstance(Effects.POISON, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                    player.addPotionEffect(new EffectInstance(Effects.HUNGER, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                } else if (power < DimensionConfig.DIMPOWER_WARN2.get()) {
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                    player.addPotionEffect(new EffectInstance(Effects.HUNGER, EFFECTS_MAX*MAXTICKS*2, 1, true, true));
                } else if (power < DimensionConfig.DIMPOWER_WARN1.get()) {
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, EFFECTS_MAX*MAXTICKS*2, 0, true, true));
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, EFFECTS_MAX*MAXTICKS*2, 0, true, true));
                }
            }

        }
    }



    private void handleLowPower(ServerWorld world, long power, boolean doEffects, int phasedCost) {

        if (power <= 0) {
            // We ran out of power!
            if (world != null) {
                List<PlayerEntity> players = new ArrayList<>(world.getPlayers());
                // @todo 1.16
//                if (PowerConfiguration.dimensionDifficulty >= 1) {
                for (PlayerEntity player : players) {
                    if (!PhasedFieldGenerator.checkValidPhasedFieldGenerator(player, true, phasedCost)) {
                        player.attackEntityFrom(new DamageSourcePowerLow("powerLow"), 1000000.0f);
                    } else {
                        if (doEffects && DimensionConfig.PHASED_FIELD_GENERATOR_DEBUF.get()) {
                            player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, EFFECTS_MAX * MAXTICKS, 2, true, true));
                            player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, EFFECTS_MAX * MAXTICKS, 2, true, true));
                            player.addPotionEffect(new EffectInstance(Effects.HUNGER, EFFECTS_MAX * MAXTICKS, 2, true, true));
                        }
                    }
                }
//                } else {
//                    Random random = new Random();
//                    for (EntityPlayer player : players) {
//                        if (!RfToolsDimensionManager.checkValidPhasedFieldGenerator(player, true, phasedCost)) {
//                            WorldServer worldServerForDimension = player.getEntityWorld().getMinecraftServer().getWorld(GeneralConfiguration.spawnDimension);
//                            int x = random.nextInt(2000) - 1000;
//                            int z = random.nextInt(2000) - 1000;
//                            int y = worldServerForDimension.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
//                            if (y == -1) {
//                                y = 63;
//                            }
//
//                            RFToolsDim.teleportationManager.teleportPlayer(player, GeneralConfiguration.spawnDimension, new BlockPos(x, y, z));
//                        } else {
//                            if (doEffects) {
//                                player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 4, true, true));
//                                player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX * MAXTICKS, 4, true, true));
//                                player.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
//                            }
//                        }
//                    }
//                }
            }
        }
    }

    private void handleRandomEffects(ServerWorld world, DimensionData information) {
        // The world is loaded and there are players there.
        // @todo 1.16
//        if (information.isPatreonBitSet(PatreonType.PATREON_FIREWORKS)) {
//            handleFireworks(world);
//        }
//        if (information.isPatreonBitSet(PatreonType.PATREON_TOMWOLF)) {
//            handleHowlingWolf(world, information);
//        }
    }


}
