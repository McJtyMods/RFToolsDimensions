package mcjty.rftoolsdim.setup;


import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.entities.DimensionalBlobRender;
import mcjty.rftoolsdim.entities.EntitySetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
//        GenericGuiContainer.register(ProgrammerSetup.PROGRAMMER_CONTAINER.get(), GuiProgrammer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySetup.DIMENSIONAL_BLOB.get(), DimensionalBlobRender.FACTORY);
    }
}
