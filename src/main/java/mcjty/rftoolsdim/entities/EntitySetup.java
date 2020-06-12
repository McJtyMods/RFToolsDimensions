package mcjty.rftoolsdim.entities;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsdim.setup.Registration.ENTITIES;

public class EntitySetup {

    public static void register() {
        // Needed to force class loading
    }


    public static final RegistryObject<EntityType<DimensionalBlobEntity>> DIMENSIONAL_BLOB = ENTITIES.register("dimensional_blob", () -> EntityType.Builder.create(DimensionalBlobEntity::new, EntityClassification.MISC)
            .size(1.0F, 1.0F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dimensional_blob"));

}
