package mcjty.rftoolsdim.modules.blob;

import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.blob.client.DimensionalBlobRender;
import mcjty.rftoolsdim.modules.blob.entities.DimensionalBlobEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.ENTITIES;

public class BlobModule implements IModule {

    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB = ENTITIES.register("dimensional_blob", () -> EntityType.Builder.create(DimensionalBlobEntity::new, EntityClassification.MISC)
            .size(1.0F, 1.0F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dimensional_blob"));

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(DIMENSIONAL_BLOB.get(), DimensionalBlobRender.FACTORY);
    }

    @Override
    public void initConfig() {

    }
}
