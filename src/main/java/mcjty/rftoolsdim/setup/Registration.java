package mcjty.rftoolsdim.setup;


import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.RFTModDimension;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.dimension.features.SpheresFeature;
import mcjty.rftoolsdim.entities.EntitySetup;
import mcjty.rftoolsdim.modules.dimlets.DimletSetup;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static mcjty.rftoolsdim.RFToolsDim.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);
    public static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        DIMENSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());

        DimletSetup.register();
        EntitySetup.register();
    }

    public static final Map<TerrainType, RegistryObject<RFTModDimension>> MOD_DIMENSIONS = Arrays.stream(TerrainType.values())
            .map(type -> Pair.of(type, DIMENSIONS.register("dimension_" + type.getName(), () -> new RFTModDimension(type))))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

    public static final RegistryObject<SpheresFeature> SPHERES_FEATURE = FEATURES.register("spheres", () -> new SpheresFeature(NoFeatureConfig::deserialize));

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(RFToolsDim.setup.getTab());
    }
}
