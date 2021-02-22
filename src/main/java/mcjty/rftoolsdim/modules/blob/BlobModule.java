package mcjty.rftoolsdim.modules.blob;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.blob.client.DimensionalBlobRender;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.setup.Config;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolsdim.setup.Registration.ENTITIES;

public class BlobModule implements IModule {

    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_COMMON = ENTITIES.register("dimensional_blob_common",
            () -> EntityType.Builder.create((EntityType<DimensionalBlobEntity> type, World world) -> new DimensionalBlobEntity(type, world, DimletRarity.COMMON),
                    EntityClassification.MONSTER)
                    .size(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_common"));
    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_RARE = ENTITIES.register("dimensional_blob_rare",
            () -> EntityType.Builder.create((EntityType<DimensionalBlobEntity> type, World world) -> new DimensionalBlobEntity(type, world, DimletRarity.RARE),
                    EntityClassification.MONSTER)
                    .size(1.3F, 1.3F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_rare"));
    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_LEGENDARY = ENTITIES.register("dimensional_blob_legendary",
            () -> EntityType.Builder.create((EntityType<DimensionalBlobEntity> type, World world) -> new DimensionalBlobEntity(type, world, DimletRarity.LEGENDARY),
                    EntityClassification.MONSTER)
                    .size(1.8F, 1.8F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_legendary"));

    public BlobModule() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityAttributes);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        EntitySpawnPlacementRegistry.register(DIMENSIONAL_BLOB_COMMON.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawnInLight);
        EntitySpawnPlacementRegistry.register(DIMENSIONAL_BLOB_RARE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawnInLight);
        EntitySpawnPlacementRegistry.register(DIMENSIONAL_BLOB_LEGENDARY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawnInLight);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DIMENSIONAL_BLOB_COMMON.get(), DimensionalBlobEntity.registerAttributes(DimletRarity.COMMON).create());
        event.put(DIMENSIONAL_BLOB_RARE.get(), DimensionalBlobEntity.registerAttributes(DimletRarity.RARE).create());
        event.put(DIMENSIONAL_BLOB_LEGENDARY.get(), DimensionalBlobEntity.registerAttributes(DimletRarity.LEGENDARY).create());
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(DIMENSIONAL_BLOB_COMMON.get(), DimensionalBlobRender.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(DIMENSIONAL_BLOB_RARE.get(), DimensionalBlobRender.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(DIMENSIONAL_BLOB_LEGENDARY.get(), DimensionalBlobRender.FACTORY);
    }

    @Override
    public void initConfig() {
        BlobConfig.init(Config.SERVER_BUILDER, Config.COMMON_BUILDER, Config.CLIENT_BUILDER);
    }
}
