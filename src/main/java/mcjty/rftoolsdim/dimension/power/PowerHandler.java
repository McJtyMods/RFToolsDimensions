package mcjty.rftoolsdim.dimension.power;

import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.dimension.data.DimensionData;
import mcjty.rftoolsdim.dimension.data.DimensionManager;
import mcjty.rftoolsdim.dimension.data.PersistantDimensionManager;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.network.PacketPropagatePowerToClients;
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import net.minecraft.entity.player.PlayerEntity;
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
        Map<ResourceLocation, Long> powerMap = new HashMap<>();
        for (Map.Entry<ResourceLocation, DimensionData> entry : mgr.getData().entrySet()) {
            powerMap.put(entry.getKey(), entry.getValue().getEnergy());
        }
        RFToolsDimMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketPropagatePowerToClients(powerMap));
    }

    private void handlePower(World overworld, boolean doEffects) {
        PersistantDimensionManager mgr = PersistantDimensionManager.get(overworld);
        for (Map.Entry<ResourceLocation, DimensionData> entry : mgr.getData().entrySet()) {
            // Power handling.
            long power;
            ServerWorld world = DimensionId.fromResourceLocation(entry.getKey()).loadWorld(overworld);
            CompiledDescriptor compiledDescriptor = DimensionManager.get().getDimensionInformation(world);

            // If there is an activity probe we only drain power if the dimension is loaded (a player is there or a chunkloader)
            // @todo 1.16
//            if (!information.isCheater() && ((world != null && world.getChunkProvider().getLoadedChunkCount() > 0) || information.getProbeCounter() == 0)) {
            power = handlePowerDimension(doEffects, world, entry.getValue(), compiledDescriptor);
//            } else {
//                power = dimensionStorage.getEnergyLevel(id);
//            }

            // Special effect handling.
            if (doEffects && power > 0) {
                // @todo 1.16
//                handleEffectsForDimension(power, id, information);
            }
            if (world != null && !world.getPlayers().isEmpty()) {
                handleRandomEffects(world, entry.getValue());
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

    private void handleLowPower(ServerWorld world, long power, boolean doEffects, int phasedCost) {
//        getPotions();
        if (power <= 0) {
            // We ran out of power!
            if (world != null) {
                List<PlayerEntity> players = new ArrayList<>(world.getPlayers());
                // @todo 1.16
//                if (PowerConfiguration.dimensionDifficulty >= 1) {
                for (PlayerEntity player : players) {
//                        if (!RfToolsDimensionManager.checkValidPhasedFieldGenerator(player, true, phasedCost)) {
                    player.attackEntityFrom(new DamageSourcePowerLow("powerLow"), 1000000.0f);
//                        } else {
//                            if (doEffects && PowerConfiguration.phasedFieldGeneratorDebuf) {
//                                player.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 4, true, true));
//                                player.addPotionEffect(new PotionEffect(digSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
//                                player.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
//                            }
//                        }
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
