package mcjty.rftoolsdim.modules.essences;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsdim.modules.essences.blocks.BiomeAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.BlockAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.blocks.FluidAbsorberTileEntity;
import mcjty.rftoolsdim.modules.essences.client.ClientSetup;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsdim.setup.Registration.*;

public class EssencesModule implements IModule {

    public static final RegistryObject<BaseBlock> BLOCK_ABSORBER = BLOCKS.register("block_absorber", BlockAbsorberTileEntity::createBlock);
    public static final RegistryObject<Item> BLOCK_ABSORBER_ITEM = ITEMS.register("block_absorber", () -> new BlockItem(BLOCK_ABSORBER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<BlockAbsorberTileEntity>> TYPE_BLOCK_ABSORBER = TILES.register("block_absorber", () -> TileEntityType.Builder.create(BlockAbsorberTileEntity::new, BLOCK_ABSORBER.get()).build(null));

    public static final RegistryObject<BaseBlock> FLUID_ABSORBER = BLOCKS.register("fluid_absorber", FluidAbsorberTileEntity::createBlock);
    public static final RegistryObject<Item> FLUID_ABSORBER_ITEM = ITEMS.register("fluid_absorber", () -> new BlockItem(FLUID_ABSORBER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<FluidAbsorberTileEntity>> TYPE_FLUID_ABSORBER = TILES.register("fluid_absorber", () -> TileEntityType.Builder.create(FluidAbsorberTileEntity::new, FLUID_ABSORBER.get()).build(null));

    public static final RegistryObject<BaseBlock> BIOME_ABSORBER = BLOCKS.register("biome_absorber", BiomeAbsorberTileEntity::createBlock);
    public static final RegistryObject<Item> BIOME_ABSORBER_ITEM = ITEMS.register("biome_absorber", () -> new BlockItem(BIOME_ABSORBER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<BiomeAbsorberTileEntity>> TYPE_BIOME_ABSORBER = TILES.register("biome_absorber", () -> TileEntityType.Builder.create(BiomeAbsorberTileEntity::new, BIOME_ABSORBER.get()).build(null));

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();
    }

    @Override
    public void initConfig() {
        EssencesConfig.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
