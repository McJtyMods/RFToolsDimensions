package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.commands.ModCommands;
import mcjty.rftoolsdim.dimension.ModDimensions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("rftoolsdim", () -> new ItemStack(Items.DIAMOND));
    }   // @todo 1.15

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsDimMessage.registerMessages("rftoolsdim");
    }

    public void initClient(FMLClientSetupEvent e) {
    }

    @SubscribeEvent
    public static void serverLoad(FMLServerStartingEvent event) {
        ModCommands.register(event.getCommandDispatcher());
    }

    @SubscribeEvent
    public static void onDimensionRegistry(RegisterDimensionsEvent event) {
        // This should happen on dimension create only
        ModDimensions.DIMENSION_TYPE = DimensionManager.registerOrGetDimension(ModDimensions.DIMENSION_ID, Registration.DIMENSION.get(), null, true);
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }
}
