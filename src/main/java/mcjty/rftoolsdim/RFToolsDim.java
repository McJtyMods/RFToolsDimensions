package mcjty.rftoolsdim;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
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
import mcjty.rftoolsdim.setup.RFToolsDimMessages;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(RFToolsDim.MODID)
public class RFToolsDim {
    public static final String MODID = "rftoolsdim";

    public static RFToolsDim instance;
    private final Modules modules = new Modules();
    public static final ModSetup setup = new ModSetup();

    public RFToolsDim(ModContainer mod, IEventBus bus, Dist dist) {
        instance = this;
        setupModules(bus, dist);

        Config.register(mod, bus, modules);
        Registration.register(bus);
        bus.addListener(setup.getBlockCapabilityRegistrar(Registration.RBLOCKS));

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::processIMC);
        bus.addListener(this::onDataGen);
        bus.addListener(RFToolsDimMessages::registerMessages);
        NeoForge.EVENT_BUS.addListener(this::onJoinWorld);

        if (dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(ClientSetup::onPlayerLogin);
            NeoForge.EVENT_BUS.addListener(ClientSetup::onDimensionChange);
            NeoForge.EVENT_BUS.addListener(OverlayRenderer::render);
            bus.addListener(ClientSetup::init);
            bus.addListener(modules::initClient);
//            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onClientTick);
        }
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
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen, event.getLookupProvider());
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new VariousModule());
        modules.register(new DimensionBuilderModule(bus));
        modules.register(new DimensionEditorModule(bus));
        modules.register(new DimletModule());
        modules.register(new EnscriberModule(bus));
        modules.register(new WorkbenchModule(bus));
        modules.register(new BlobModule(bus, dist));
        modules.register(new KnowledgeModule());
        modules.register(new EssencesModule());
        modules.register(new DecorativeModule());
    }

    private void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        DimensionCreator.get().clear();
    }
}
