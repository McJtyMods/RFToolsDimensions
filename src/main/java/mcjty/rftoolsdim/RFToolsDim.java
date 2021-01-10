package mcjty.rftoolsdim;

import mcjty.rftoolsdim.setup.ClientSetup;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.ModSetup;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsDim.MODID)
public class RFToolsDim {
    public static final String MODID = "rftoolsdim";

    public static RFToolsDim instance;
    public static ModSetup setup = new ModSetup();

    public RFToolsDim() {
        instance = this;

//        SharedConstants.developmentMode = true;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
        });
    }
}
