package mcjty.rftoolsdim;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.network.DimensionSyncPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Logging.log("SMP: Player logged in: Sync diminfo to clients");
        EntityPlayer player = event.player;
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(player.getEntityWorld());
        manager.syncDimInfoToClients(player.getEntityWorld());
        manager.syncDimletRules(player);
    }

    @SubscribeEvent
    public void onConnectionCreated(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        Logging.log("SMP: Sync dimensions to client");
        DimensionSyncPacket packet = new DimensionSyncPacket();

        EntityPlayer player = ((NetHandlerPlayServer) event.getHandler()).playerEntity;
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(player.getEntityWorld());
        for (Integer id : manager.getDimensions().keySet()) {
            Logging.log("Sending over dimension " + id + " to the client");
            packet.addDimension(id);
        }

        RFToolsDim.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
        RFToolsDim.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.getManager().channel().attr(NetworkDispatcher.FML_DISPATCHER).get());
        RFToolsDim.channels.get(Side.SERVER).writeOutbound(packet);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        int id = world.provider.getDimension();

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation dimensionInformation = dimensionManager.getDimensionInformation(id);

        if (dimensionInformation != null && dimensionInformation.isNoanimals()) {
            if (event.getEntity() instanceof IAnimals && !(event.getEntity() instanceof IMob)) {
                event.setCanceled(true);
                Logging.logDebug("Noanimals dimension: Prevented a spawn of " + event.getEntity().getClass().getName());
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld();
        int id = world.provider.getDimension();

        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation dimensionInformation = dimensionManager.getDimensionInformation(id);

        if (PowerConfiguration.preventSpawnUnpowered) {
            if (dimensionInformation != null) {
                // RFTools dimension.
                DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
                int energy = storage.getEnergyLevel(id);
                if (energy <= 0) {
                    event.setResult(Event.Result.DENY);
                    Logging.logDebug("Dimension power low: Prevented a spawn of " + event.getEntity().getClass().getName());
                }
            }
        }

        if (dimensionInformation != null) {
            if (dimensionInformation.hasEffectType(EffectType.EFFECT_STRONGMOBS) || dimensionInformation.hasEffectType(EffectType.EFFECT_BRUTALMOBS)) {
                if (event.getEntity() instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase) event.getEntity();
                    IAttributeInstance entityAttribute = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
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

        if (event.getEntity() instanceof IMob) {
            BlockPos coordinate = new BlockPos((int) event.getEntity().posX, (int) event.getEntity().posY, (int) event.getEntity().posZ);
            /* if (PeacefulAreaManager.isPeaceful(new GlobalCoordinate(coordinate, id))) {
                event.setResult(Event.Result.DENY);
                Logging.logDebug("Peaceful manager: Prevented a spawn of " + event.entity.getClass().getName());
            } else */ if (dimensionInformation != null && dimensionInformation.isPeaceful()) {
                // RFTools dimension.
                event.setResult(Event.Result.DENY);
                Logging.logDebug("Peaceful dimension: Prevented a spawn of " + event.getEntity().getClass().getName());
            }
        } else if (event.getEntity() instanceof IAnimals) {
            if (dimensionInformation != null && dimensionInformation.isNoanimals()) {
                // RFTools dimension.
                event.setResult(Event.Result.DENY);
                Logging.logDebug("Noanimals dimension: Prevented a spawn of " + event.getEntity().getClass().getName());
            }
        }
        // @todo
    }



    @SubscribeEvent
    public void onReplaceBiomeBlocks(ChunkGeneratorEvent.ReplaceBiomeBlocks event) {
        World world = event.getWorld();
        if (world == null) {
            return;
        }
        int id = world.provider.getDimension();
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(id);
        if (information != null && information.hasFeatureType(FeatureType.FEATURE_CLEAN)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private Random random = new Random();

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof EntityEnderman) {
            if (random.nextFloat() < GeneralConfiguration.endermanDimletPartDrop) {
                event.getDrops().add(new EntityItem((event.getEntityLiving()).world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, new ItemStack(ModItems.dimletParcelItem)));
            }
        }
    }


    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) ||
                event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST) ||
                event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID) ||
                event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE) ||
                event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) ||
                event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) ||
                event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
            LootPool main = event.getTable().getPool("main");
            // Safety, check if the main lootpool is still present
            if (main != null) {
                main.addEntry(new LootEntryItem(ModItems.dimletParcelItem, WorldgenConfiguration.dimletParcelRarity, 0, new LootFunction[0], new LootCondition[0], RFToolsDim.MODID + ":parcel"));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            Block block = world.getBlockState(event.getPos()).getBlock();
            if (block instanceof BlockBed) {
                RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
                if (dimensionManager.getDimensionInformation(world.provider.getDimension()) != null) {
                    // We are in an RFTools dimension.
                    switch (GeneralConfiguration.bedBehaviour) {
                        case 0:
                            event.setCanceled(true);
                            Logging.message(event.getEntityPlayer(), "You cannot sleep in this dimension!");
                            break;
                        case 1:
                            // Just do the usual thing (this typically mean explosion).
                            break;
                        case 2:
                            event.setCanceled(true);
                            event.getEntityPlayer().setSpawnChunk(event.getPos(), true, event.getWorld().provider.getDimension());
                            Logging.message(event.getEntityPlayer(), "Spawn point set!");
                            break;
                    }
                }
            }
        }
    }

}
