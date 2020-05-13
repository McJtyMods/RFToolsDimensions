package mcjty.rftoolsdim;

import mcjty.lib.base.ModBase;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.ModSetup;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsDim.MODID)
public class RFToolsDim implements ModBase {
    public static final String MODID = "rftoolsdim";

    public static RFToolsDim instance;
    public static ModSetup setup = new ModSetup();

    public RFToolsDim() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> setup.init(event));
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> setup.initClient(event));
    }

    @Override
    public String getModId() {
        return MODID;
    }
}
