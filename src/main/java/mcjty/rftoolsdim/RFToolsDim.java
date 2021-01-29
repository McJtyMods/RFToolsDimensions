package mcjty.rftoolsdim;

import mcjty.lib.modules.Modules;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.ModSetup;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(RFToolsDim.MODID)
public class RFToolsDim {
    public static final String MODID = "rftoolsdim";

    public static RFToolsDim instance;
    private Modules modules = new Modules();
    public static ModSetup setup = new ModSetup();

    public RFToolsDim() {
        instance = this;
        setupModules();

        Config.register(modules);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::init);
        MinecraftForge.EVENT_BUS.addListener(KnowledgeModule::onWorldLoad);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::initClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(WorkbenchModule::onTextureStitch);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(DimensionBuilderModule::onTextureStitch);
        });
    }

    private void setupModules() {
        modules.register(new DimensionBuilderModule());
        modules.register(new DimletModule());
        modules.register(new EnscriberModule());
        modules.register(new WorkbenchModule());
        modules.register(new BlobModule());
        modules.register(new KnowledgeModule());
        modules.register(new EssencesModule());
        modules.register(new DecorativeModule());
    }
}
