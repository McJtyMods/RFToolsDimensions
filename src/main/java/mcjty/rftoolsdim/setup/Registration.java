package mcjty.rftoolsdim.setup;


import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.features.SpheresFeature;
import mcjty.rftoolsdim.entities.EntitySetup;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderSetup;
import mcjty.rftoolsdim.modules.dimlets.DimletSetup;
import mcjty.rftoolsdim.modules.enscriber.EnscriberSetup;
import mcjty.rftoolsdim.modules.workbench.WorkbenchSetup;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static mcjty.rftoolsdim.RFToolsDim.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());

        DimletSetup.register();
        EntitySetup.register();
        DimensionBuilderSetup.register();
        WorkbenchSetup.register();
        EnscriberSetup.register();
    }

    public static final RegistryObject<SpheresFeature> SPHERES_FEATURE = FEATURES.register("spheres", () -> new SpheresFeature(NoFeatureConfig.field_236558_a_));

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(RFToolsDim.setup.getTab());
    }
}
