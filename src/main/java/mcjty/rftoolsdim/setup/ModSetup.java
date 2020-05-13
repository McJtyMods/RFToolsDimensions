package mcjty.rftoolsdim.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }
}
