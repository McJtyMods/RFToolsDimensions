package mcjty.rftoolsdim.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;

/**
 * This class stores the actual settings as used by a runtime dimension (chunk generator, biome provider, ...)
 */
public class DimensionSettings {

    public static final Codec<DimensionSettings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("dimlets").forGetter(DimensionSettings::getDimlets)
            ).apply(instance, DimensionSettings::new));

    private final String dimlets;
    private CompiledDescriptor compiledDescriptor;

    public DimensionSettings(String dimlets) {
        this.dimlets = dimlets;
    }

    public String getDimlets() {
        return dimlets;
    }

    public CompiledDescriptor getCompiledDescriptor() {
        if (compiledDescriptor == null) {
            DimensionDescriptor descriptor = new DimensionDescriptor();
            descriptor.read(getDimlets());
            compiledDescriptor = new CompiledDescriptor();
            String error = compiledDescriptor.compile(descriptor);
            if (error != null) {
                RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error);
                throw new RuntimeException("Error compiling dimension descriptor: " + error);
            }
        }
        return compiledDescriptor;
    }
}
