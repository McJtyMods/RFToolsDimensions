package mcjty.rftoolsdim;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import mcjty.lib.varia.ClientTools;
import mcjty.rftoolsbase.api.dimension.IDimensionManager;
import mcjty.rftoolsdim.apiimpl.DimensionManager;
import mcjty.rftoolsdim.dimension.client.OverlayRenderer;
import mcjty.rftoolsdim.dimension.data.DimensionCreator;
import mcjty.rftoolsdim.modules.blob.BlobModule;
import mcjty.rftoolsdim.modules.decorative.DecorativeModule;
import mcjty.rftoolsdim.modules.dimensionbuilder.DimensionBuilderModule;
import mcjty.rftoolsdim.modules.dimensioneditor.DimensionEditorModule;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.enscriber.EnscriberModule;
import mcjty.rftoolsdim.modules.essences.EssencesModule;
import mcjty.rftoolsdim.modules.knowledge.KnowledgeModule;
import mcjty.rftoolsdim.modules.various.VariousModule;
import mcjty.rftoolsdim.modules.workbench.WorkbenchModule;
import mcjty.rftoolsdim.setup.ClientSetup;
import mcjty.rftoolsdim.setup.Config;
import mcjty.rftoolsdim.setup.ModSetup;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(RFToolsDim.MODID)
public class RFToolsDim {
    public static final String MODID = "rftoolsdim";

    public static RFToolsDim instance;
    private final Modules modules = new Modules();
    public static final ModSetup setup = new ModSetup();

    public RFToolsDim() {
        instance = this;
        setupModules();

        Config.register(modules);

        Registration.register();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::processIMC);
        bus.addListener(this::onDataGen);
        MinecraftForge.EVENT_BUS.addListener(this::onJoinWorld);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(ClientSetup::onPlayerLogin);
            MinecraftForge.EVENT_BUS.addListener(ClientSetup::onDimensionChange);
            MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::render);
            bus.addListener(ClientSetup::init);
            bus.addListener(modules::initClient);
            ClientTools.onTextureStitch(bus, WorkbenchModule::onTextureStitch);
//            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onClientTick);
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        event.getIMCStream().forEach(message -> {
            if ("getDimensionManager".equals(message.getMethod())) {
                Supplier<Function<IDimensionManager, Void>> supplier = message.getMessageSupplier();
                supplier.get().apply(new DimensionManager());
            }
        });
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        instance.setup.tab(supplier);
        return supplier;
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
    }

    private void setupModules() {
        modules.register(new VariousModule());
        modules.register(new DimensionBuilderModule());
        modules.register(new DimensionEditorModule());
        modules.register(new DimletModule());
        modules.register(new EnscriberModule());
        modules.register(new WorkbenchModule());
        modules.register(new BlobModule());
        modules.register(new KnowledgeModule());
        modules.register(new EssencesModule());
        modules.register(new DecorativeModule());
    }

    private void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        DimensionCreator.get().clear();
    }
}
