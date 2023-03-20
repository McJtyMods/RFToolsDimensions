package mcjty.rftoolsdim.dimension.power;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.dimension.DimensionConfig;
import mcjty.rftoolsdim.dimension.data.ClientDimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionCreator;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.network.PackagePropageDataToClients;
import mcjty.rftoolsdim.modules.dimensionbuilder.items.PhasedFieldGenerator;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerHandler {

    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    public static long calculateMaxDimensionPower(ResourceLocation id, Level overworld) {
        CompiledDescriptor descriptor = DimensionCreator.get().getCompiledDescriptor(overworld, id);
        if (descriptor != null) {
            return calculateMaxDimensionPower(descriptor);
        } else {
            return DimensionConfig.MAX_DIMENSION_POWER_MAX.get();
        }
    }

    public static long calculateMaxDimensionPower(CompiledDescriptor descriptor) {
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
    }


    public void handlePower(Level overworld) {
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

    private void sendOutPower(Level overworld) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(overworld);
        Map<ResourceLocation, ClientDimensionData.ClientData> clientDataMap = new HashMap<>();
        for (var entry : mgr.getData().entrySet()) {
            long energy = entry.getValue().getEnergy();
            clientDataMap.put(entry.getKey(), new ClientDimensionData.ClientData(energy, PowerHandler.calculateMaxDimensionPower(entry.getKey(), overworld),
                    entry.getValue().getSkyTypes()));
        }
        RFToolsDimMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PackagePropageDataToClients(clientDataMap,
                ((ServerLevel) overworld).getSeed()));
    }

    private void handlePower(Level overworld, boolean doEffects) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(overworld);
        for (Map.Entry<ResourceLocation, DimensionData> entry : mgr.getData().entrySet()) {
            // Power handling.
            long power;
            ServerLevel world = LevelTools.getLevel(overworld, entry.getKey());
            CompiledDescriptor compiledDescriptor = DimensionCreator.get().getCompiledDescriptor(world);

            if (compiledDescriptor != null) {

                boolean doPower = true;
                if (entry.getValue().getActivityProbes() > 0) {
                    int chunks = world.getChunkSource().chunkMap.size();
                    if (chunks == 0) {
                        doPower = false;
                    }
                }

                if (doPower) {
                    power = handlePowerDimension(doEffects, world, entry.getValue(), compiledDescriptor);
                } else {
                    power = entry.getValue().getEnergy();
                }

                // Special effect handling.
                if (doEffects && power > 0) {
                    handleEffectsForDimension(power, world, compiledDescriptor);
                }
                if (world != null && !world.players().isEmpty()) {
                    handleRandomEffects(world, entry.getValue());
                }
            }
        }
        mgr.save();
    }

    private long handlePowerDimension(boolean doEffects, ServerLevel world, DimensionData data, CompiledDescriptor compiledDescriptor) {
        int cost = compiledDescriptor.getActualPowerCost();
//        if (PowerConfiguration.dimensionDifficulty != -1) {   // @todo 1.16 config
//        }

        long power = data.getEnergy();
        power -= (long) cost * MAXTICKS;
        if (power < 0) {
            power = 0;
        }

        handleLowPower(world, power, doEffects, cost);

        data.setEnergy(world, power);

        return power;
    }

    private void handleEffectsForDimension(long power, ServerLevel world, CompiledDescriptor compiledDescriptor) {
        if (world != null) {
            // @todo 1.16 handle dimension specific effects
//            Set<EffectType> effects = information.getEffectTypes();
            List<ServerPlayer> players = new ArrayList<>(world.players());
            for (ServerPlayer player : players) {
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
                long max = PowerHandler.calculateMaxDimensionPower(compiledDescriptor);
                int percentage = (int) (power * 100 / max);

                if (percentage < DimensionConfig.DIMPOWER_WARN3.get()) {
                    // We are VERY low on power. Start bad effects.
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECTS_MAX*MAXTICKS*2, 4, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECTS_MAX*MAXTICKS*2, 4, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                } else if (percentage < DimensionConfig.DIMPOWER_WARN2.get()) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECTS_MAX*MAXTICKS*2, 2, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, EFFECTS_MAX*MAXTICKS*2, 1, true, true));
                } else if (percentage < DimensionConfig.DIMPOWER_WARN1.get()) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECTS_MAX*MAXTICKS*2, 0, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECTS_MAX*MAXTICKS*2, 0, true, true));
                }
            }

        }
    }



    private void handleLowPower(ServerLevel world, long power, boolean doEffects, int phasedCost) {

        if (power <= 0) {
            // We ran out of power!
            if (world != null) {
                List<Player> players = new ArrayList<>(world.players());
                // @todo 1.16
//                if (PowerConfiguration.dimensionDifficulty >= 1) {
                for (Player player : players) {
                    if (!PhasedFieldGenerator.checkValidPhasedFieldGenerator(player, true, phasedCost)) {
                        // @todo 1.19.4
//                        player.hurt(new DamageSourcePowerLow("powerLow"), 1000000.0f);
                    } else {
                        if (doEffects && DimensionConfig.PHASED_FIELD_GENERATOR_DEBUF.get()) {
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECTS_MAX * MAXTICKS, 2, true, true));
                            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECTS_MAX * MAXTICKS, 2, true, true));
                            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, EFFECTS_MAX * MAXTICKS, 2, true, true));
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

    private void handleRandomEffects(ServerLevel world, DimensionData information) {
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
