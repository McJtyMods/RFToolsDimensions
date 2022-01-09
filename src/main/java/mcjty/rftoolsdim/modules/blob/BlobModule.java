package mcjty.rftoolsdim.modules.blob;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.blob.client.DimensionalBlobRender;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.setup.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.rftoolsdim.setup.Registration.ENTITIES;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlobModule implements IModule {

    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_COMMON = ENTITIES.register("dimensional_blob_common",
            () -> EntityType.Builder.of((EntityType<DimensionalBlobEntity> type, Level world) -> new DimensionalBlobEntity(type, world, DimletRarity.COMMON),
                    MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_common"));
    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_RARE = ENTITIES.register("dimensional_blob_rare",
            () -> EntityType.Builder.of((EntityType<DimensionalBlobEntity> type, Level world) -> new DimensionalBlobEntity(type, world, DimletRarity.RARE),
                    MobCategory.MONSTER)
                    .sized(1.3F, 1.3F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_rare"));
    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_LEGENDARY = ENTITIES.register("dimensional_blob_legendary",
            () -> EntityType.Builder.of((EntityType<DimensionalBlobEntity> type, Level world) -> new DimensionalBlobEntity(type, world, DimletRarity.LEGENDARY),
                    MobCategory.MONSTER)
                    .sized(1.8F, 1.8F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_legendary"));

    public BlobModule() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityAttributes);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        SpawnPlacements.register(DIMENSIONAL_BLOB_COMMON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(DIMENSIONAL_BLOB_RARE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(DIMENSIONAL_BLOB_LEGENDARY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DIMENSIONAL_BLOB_COMMON.get(), DimensionalBlobEntity.registerAttributes(DimletRarity.COMMON).build());
        event.put(DIMENSIONAL_BLOB_RARE.get(), DimensionalBlobEntity.registerAttributes(DimletRarity.RARE).build());
        event.put(DIMENSIONAL_BLOB_LEGENDARY.get(), DimensionalBlobEntity.registerAttributes(DimletRarity.LEGENDARY).build());
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DIMENSIONAL_BLOB_COMMON.get(), DimensionalBlobRender.FACTORY);
        event.registerEntityRenderer(DIMENSIONAL_BLOB_RARE.get(), DimensionalBlobRender.FACTORY);
        event.registerEntityRenderer(DIMENSIONAL_BLOB_LEGENDARY.get(), DimensionalBlobRender.FACTORY);
    }

    @Override
    public void initConfig() {
        BlobConfig.init(Config.SERVER_BUILDER, Config.COMMON_BUILDER, Config.CLIENT_BUILDER);
    }
}
