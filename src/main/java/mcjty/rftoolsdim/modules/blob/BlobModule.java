package mcjty.rftoolsdim.modules.blob;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.setup.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.rftoolsdim.setup.Registration.ENTITIES;

public class BlobModule implements IModule {

    public static final Supplier<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_COMMON = ENTITIES.register("dimensional_blob_common",
            () -> EntityType.Builder.of((EntityType<DimensionalBlobEntity> type, Level world) -> new DimensionalBlobEntity(type, world, DimletRarity.COMMON),
                    MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_common"));
    public static final Supplier<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_RARE = ENTITIES.register("dimensional_blob_rare",
            () -> EntityType.Builder.of((EntityType<DimensionalBlobEntity> type, Level world) -> new DimensionalBlobEntity(type, world, DimletRarity.RARE),
                    MobCategory.MONSTER)
                    .sized(1.3F, 1.3F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_rare"));
    public static final Supplier<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB_LEGENDARY = ENTITIES.register("dimensional_blob_legendary",
            () -> EntityType.Builder.of((EntityType<DimensionalBlobEntity> type, Level world) -> new DimensionalBlobEntity(type, world, DimletRarity.LEGENDARY),
                    MobCategory.MONSTER)
                    .sized(1.8F, 1.8F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("dimensional_blob_legendary"));

    public BlobModule(IEventBus bus, Dist dist) {
        bus.addListener(this::registerEntityAttributes);
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


    @Override
    public void initConfig(IEventBus bus) {
        BlobConfig.init(Config.SERVER_BUILDER, Config.COMMON_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.entityBuilder(DIMENSIONAL_BLOB_COMMON)
                        .loot(p -> p.addItemDropTable(DIMENSIONAL_BLOB_COMMON.get(), DimletModule.COMMON_ESSENCE.get(), 3, 5, 0, 1)),
                Dob.entityBuilder(DIMENSIONAL_BLOB_RARE)
                        .loot(p -> p.addItemDropTable(DIMENSIONAL_BLOB_RARE.get(), DimletModule.RARE_ESSENCE.get(), 3, 5, 0, 1)),
                Dob.entityBuilder(DIMENSIONAL_BLOB_LEGENDARY)
                        .loot(p -> p.addItemDropTable(DIMENSIONAL_BLOB_LEGENDARY.get(), DimletModule.LEGENDARY_ESSENCE.get(), 4, 6, 0, 1))
        );
    }
}
