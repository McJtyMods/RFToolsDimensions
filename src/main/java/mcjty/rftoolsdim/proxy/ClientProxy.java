package mcjty.rftoolsdim.proxy;

import mcjty.blocks.ModBlocks;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.items.ModItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    private static final ResourceLocation VILLAGER_TEXTURE = new ResourceLocation(RFToolsDim.MODID, "textures/entities/rftoolsvillager.png");

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.initClient();
        ModBlocks.initClient();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
