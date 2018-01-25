package mcjty.rftoolsdim;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds {
    public static final SoundEvent deep_rumble = new SoundEvent(new ResourceLocation(RFToolsDim.MODID, "deep_rumble")).setRegistryName(new ResourceLocation(RFToolsDim.MODID, "deep_rumble"));
    public static final SoundEvent wolfhowl = new SoundEvent(new ResourceLocation(RFToolsDim.MODID, "wolfhowl")).setRegistryName(new ResourceLocation(RFToolsDim.MODID, "wolfhowl"));

    public static void init(IForgeRegistry<SoundEvent> registry) {
        registry.register(deep_rumble);
        registry.register(wolfhowl);
    }

}
