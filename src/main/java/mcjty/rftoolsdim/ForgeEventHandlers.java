package mcjty.rftoolsdim;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.GeneralConfiguration;
import mcjty.rftoolsdim.config.PowerConfiguration;
import mcjty.rftoolsdim.config.WorldgenConfiguration;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import mcjty.rftoolsdim.dimensions.dimlets.DimletRandomizer;
import mcjty.rftoolsdim.dimensions.dimlets.KnownDimletConfiguration;
import mcjty.rftoolsdim.dimensions.types.EffectType;
import mcjty.rftoolsdim.dimensions.types.FeatureType;
import mcjty.rftoolsdim.dimensions.world.GenericWorldProvider;
import mcjty.rftoolsdim.items.ModItems;
import mcjty.rftoolsdim.setup.ModSetup;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
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
        manager.syncDimletRules(player);
    }

    @SubscribeEvent
    public void onConnectionCreated(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        Logging.log("SMP: Sync dimensions to client");

        EntityPlayer player = ((NetHandlerPlayServer) event.getHandler()).player;
        RfToolsDimensionManager manager = RfToolsDimensionManager.getDimensionManager(player.getEntityWorld());

        ModSetup.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
        ModSetup.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.getManager().channel().attr(NetworkDispatcher.FML_DISPATCHER).get());
        ModSetup.channels.get(Side.SERVER).writeOutbound(manager.makeDimensionSyncPacket());
    }

    @SubscribeEvent
    public void clientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Logging.log("Client disconnected from server");
        RfToolsDimensionManager.cleanupDimensionInformation();
        RfToolsDimensionManager.clearInstance();
        KnownDimletConfiguration.init();
        DimletRandomizer.init();
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        WorldProvider provider = world.provider;
        if(!(provider instanceof GenericWorldProvider)) return;
        DimensionInformation dimensionInformation = ((GenericWorldProvider)provider).getDimensionInformation();

        if (dimensionInformation.isNoanimals() && event.getEntity() instanceof IAnimals && !(event.getEntity() instanceof IMob)) {
            event.setCanceled(true);
            Logging.logDebug("Noanimals dimension: Prevented a spawn of " + event.getEntity().getClass().getName());
        }
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld();

        WorldProvider provider = world.provider;
        if(!(provider instanceof GenericWorldProvider)) return;
        DimensionInformation dimensionInformation = ((GenericWorldProvider)provider).getDimensionInformation();

        if (PowerConfiguration.preventSpawnUnpowered) {
            DimensionStorage storage = DimensionStorage.getDimensionStorage(world);
            if (storage.getEnergyLevel(provider.getDimension()) <= 0) {
                event.setResult(Event.Result.DENY);
                Logging.logDebug("Dimension power low: Prevented a spawn of " + event.getEntity().getClass().getName());
            }
        }

        if (dimensionInformation.hasEffectType(EffectType.EFFECT_STRONGMOBS) || dimensionInformation.hasEffectType(EffectType.EFFECT_BRUTALMOBS)) {
            if (event.getEntity() instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) event.getEntity();
                IAttributeInstance entityAttribute = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                double newMax = entityAttribute.getBaseValue() * (dimensionInformation.hasEffectType(EffectType.EFFECT_BRUTALMOBS) ? GeneralConfiguration.brutalMobsFactor : GeneralConfiguration.strongMobsFactor);
                entityAttribute.setBaseValue(newMax);
                entityLivingBase.setHealth((float) newMax);
            }
        }

        if (event.getEntity() instanceof IMob) {
            /* BlockPos coordinate = new BlockPos((int) event.getEntity().posX, (int) event.getEntity().posY, (int) event.getEntity().posZ);
            if (PeacefulAreaManager.isPeaceful(new GlobalCoordinate(coordinate, id))) {
                event.setResult(Event.Result.DENY);
                Logging.logDebug("Peaceful manager: Prevented a spawn of " + event.entity.getClass().getName());
            } else */ if (dimensionInformation.isPeaceful()) {
                // RFTools dimension.
                event.setResult(Event.Result.DENY);
                Logging.logDebug("Peaceful dimension: Prevented a spawn of " + event.getEntity().getClass().getName());
            }
        } else if (event.getEntity() instanceof IAnimals && dimensionInformation.isNoanimals()) {
            // RFTools dimension.
            event.setResult(Event.Result.DENY);
            Logging.logDebug("Noanimals dimension: Prevented a spawn of " + event.getEntity().getClass().getName());
        }
        // @todo
    }

    @SubscribeEvent
    public void onReplaceBiomeBlocks(ChunkGeneratorEvent.ReplaceBiomeBlocks event) {
        World world = event.getWorld();
        if (world == null) {
            return;
        }
        WorldProvider provider = world.provider;
        if(!(provider instanceof GenericWorldProvider)) return;
        DimensionInformation information = ((GenericWorldProvider)provider).getDimensionInformation();
        if (information.hasFeatureType(FeatureType.FEATURE_CLEAN)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onOreGenEvent(OreGenEvent.GenerateMinable event) {
        World world = event.getWorld();
        if (world == null) {
            return;
        }
        WorldProvider provider = world.provider;
        if(!(provider instanceof GenericWorldProvider)) return;
        DimensionInformation information = ((GenericWorldProvider)provider).getDimensionInformation();
        if (information != null && information.hasFeatureType(FeatureType.FEATURE_CLEAN)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private Random random = new Random();

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof EntityEnderman && random.nextFloat() < GeneralConfiguration.endermanDimletPartDrop) {
            event.getDrops().add(new EntityItem((event.getEntityLiving()).world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, new ItemStack(ModItems.dimletParcelItem)));
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
    public void onEntitySpawn(LivingSpawnEvent.SpecialSpawn event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        EntityLivingBase entityLiving = event.getEntityLiving();
        if (entityLiving instanceof EntityDragon && world.provider instanceof GenericWorldProvider) {
            // Ender dragon needs to be spawned with an additional NBT key set
            NBTTagCompound dragonTag = new NBTTagCompound();
            entityLiving.writeEntityToNBT(dragonTag);
            dragonTag.setShort("DragonPhase", (short) 0);
            entityLiving.readEntityFromNBT(dragonTag);
        }
    }
}
