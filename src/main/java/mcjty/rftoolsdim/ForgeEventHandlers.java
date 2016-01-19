package mcjty.rftoolsdim;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.network.DimensionSyncPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Random;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Logging.log("SMP: Player logged in: Sync diminfo to clients");
        EntityPlayer player = event.player;
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(player.getEntityWorld());
        manager.syncDimInfoToClients(player.getEntityWorld());
        manager.checkDimletConfig(player);
    }

    @SubscribeEvent
    public void onConnectionCreated(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        Logging.log("SMP: Sync dimensions to client");
        DimensionSyncPacket packet = new DimensionSyncPacket();

        EntityPlayer player = ((NetHandlerPlayServer) event.handler).playerEntity;
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(player.getEntityWorld());
        for (Integer id : manager.getDimensions().keySet()) {
            Logging.log("Sending over dimension " + id + " to the client");
            packet.addDimension(id);
        }

        RFToolsDim.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
        RFToolsDim.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.manager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get());
        RFToolsDim.channels.get(Side.SERVER).writeOutbound(packet);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        World world = event.world;
        if (world.isRemote) {
            return;
        }
        int id = world.provider.getDimensionId();

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation dimensionInformation = dimensionManager.getDimensionInformation(id);

        if (dimensionInformation != null && dimensionInformation.isNoanimals()) {
            if (event.entity instanceof IAnimals && !(event.entity instanceof IMob)) {
                event.setCanceled(true);
                Logging.logDebug("Noanimals dimension: Prevented a spawn of " + event.entity.getClass().getName());
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        World world = event.world;
        int id = world.provider.getDimensionId();

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation dimensionInformation = dimensionManager.getDimensionInformation(id);

        if (PowerConfiguration.preventSpawnUnpowered) {
            if (dimensionInformation != null) {
                // RFTools dimension.
                DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
                int energy = storage.getEnergyLevel(id);
                if (energy <= 0) {
                    event.setResult(Event.Result.DENY);
                    Logging.logDebug("Dimension power low: Prevented a spawn of " + event.entity.getClass().getName());
                }
            }
        }

        if (dimensionInformation != null) {
            if (dimensionInformation.hasEffectType(EffectType.EFFECT_STRONGMOBS) || dimensionInformation.hasEffectType(EffectType.EFFECT_BRUTALMOBS)) {
                if (event.entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase) event.entity;
                    IAttributeInstance entityAttribute = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.maxHealth);
                    double newMax;
                    if (dimensionInformation.hasEffectType(EffectType.EFFECT_BRUTALMOBS)) {
                        newMax = entityAttribute.getBaseValue() * GeneralConfiguration.brutalMobsFactor;
                    } else {
                        newMax = entityAttribute.getBaseValue() * GeneralConfiguration.strongMobsFactor;
                    }
                    entityAttribute.setBaseValue(newMax);
                    entityLivingBase.setHealth((float) newMax);
                }
            }
        }

//        if (event.entity instanceof IMob) {
//            BlockPos coordinate = new BlockPos((int) event.entity.posX, (int) event.entity.posY, (int) event.entity.posZ);
//            if (PeacefulAreaManager.isPeaceful(new GlobalCoordinate(coordinate, id))) {
//                event.setResult(Event.Result.DENY);
//                Logging.logDebug("Peaceful manager: Prevented a spawn of " + event.entity.getClass().getName());
//            } else if (dimensionInformation != null && dimensionInformation.isPeaceful()) {
//                // RFTools dimension.
//                event.setResult(Event.Result.DENY);
//                Logging.logDebug("Peaceful dimension: Prevented a spawn of " + event.entity.getClass().getName());
//            }
//        } else if (event.entity instanceof IAnimals) {
//            if (dimensionInformation != null && dimensionInformation.isNoanimals()) {
//                // RFTools dimension.
//                event.setResult(Event.Result.DENY);
//                Logging.logDebug("Noanimals dimension: Prevented a spawn of " + event.entity.getClass().getName());
//            }
//        }
        // @todo
    }

    @SubscribeEvent
    public void onReplaceBiomeBlocks(ChunkProviderEvent.ReplaceBiomeBlocks event) {
        World world = event.world;
        if (world == null) {
            return;
        }
        int id = world.provider.getDimensionId();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(id);
        if (information != null && information.hasFeatureType(FeatureType.FEATURE_CLEAN)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private Random random = new Random();

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (event.entityLiving instanceof EntityEnderman) {
            if (random.nextFloat() < GeneralConfiguration.endermanDimletPartDrop) {
                List<List<ItemStack>> list = KnownDimletConfiguration.getRandomPartLists();
//                event.entityLiving.dropItem(DimletSetup.unknownDimlet, random.nextInt(2)+1);
            }
        }
    }
}
