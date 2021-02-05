package mcjty.rftoolsdim.compat;

import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletTools;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {


    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(RFToolsDim.MODID, "jeiplugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(DimletModule.ATTRIBUTE_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.TERRAIN_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.ATTRIBUTE_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.FEATURE_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.BIOME_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.BIOME_CONTROLLER_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.BLOCK_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.TIME_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.DIGIT_DIMLET.get(), DimletInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(DimletModule.SPECIAL_DIMLET.get(), DimletInterpreter.INSTANCE);
    }

    public static class DimletInterpreter implements ISubtypeInterpreter {

        public static final DimletInterpreter INSTANCE = new DimletInterpreter();

        @Override
        public String apply(ItemStack itemStack) {
            DimletKey key = DimletTools.getDimletKey(itemStack);
            return key == null ? "null" : key.getKey();
        }
    }
}
