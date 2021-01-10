package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.commands.ModCommands;
import mcjty.rftoolsdim.dimlets.DimletDictionary;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = RFToolsDim.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup extends DefaultModSetup {

    private DimletDictionary dimletDictionary;

    public ModSetup() {
        createTab("rftoolsdim", () -> new ItemStack(Items.DIAMOND));
    }   // @todo 1.15

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsDimMessage.registerMessages("rftoolsdim");
        dimletDictionary = new DimletDictionary();
    }

    public DimletDictionary getDimletDictionary() {
        return dimletDictionary;
    }

    @SubscribeEvent
    public static void serverLoad(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }
}
