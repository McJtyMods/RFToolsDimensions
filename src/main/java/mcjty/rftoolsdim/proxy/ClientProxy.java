package mcjty.rftoolsdim.proxy;

import mcjty.lib.setup.DefaultClientProxy;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.blocks.ModBlocks;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy {

    private static final ResourceLocation VILLAGER_TEXTURE = new ResourceLocation(RFToolsDim.MODID, "textures/entities/rftoolsvillager.png");

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModItems.initClient();
        ModBlocks.initClient();
    }


    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
