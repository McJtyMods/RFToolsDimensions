package mcjty.rftoolsdim.dimension.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.descriptor.CompiledDescriptor;
import mcjty.rftoolsdim.dimension.descriptor.DescriptorError;
import mcjty.rftoolsdim.dimension.descriptor.DimensionDescriptor;

/**
 * This class stores the actual settings as used by a runtime dimension (chunk generator, biome provider, ...)
 */
public class DimensionSettings {

    public static final Codec<DimensionSettings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("dimlets").forGetter(DimensionSettings::getDimlets),
                    Codec.STRING.fieldOf("randomized").forGetter(DimensionSettings::getRandomized)
            ).apply(instance, DimensionSettings::new));

    private final String dimlets;
    private final String randomized;
    private CompiledDescriptor compiledDescriptor;

    public DimensionSettings(String dimlets, String randomized) {
        this.dimlets = dimlets;
        this.randomized = randomized;
    }

    public String getDimlets() {
        return dimlets;
    }

    public String getRandomized() {
        return randomized;
    }

    public CompiledDescriptor getCompiledDescriptor() {
        if (compiledDescriptor == null) {
            DimensionDescriptor descriptor = new DimensionDescriptor();
            descriptor.read(getDimlets());
            DimensionDescriptor randomizedDescriptor = new DimensionDescriptor();
            randomizedDescriptor.read(getRandomized());
            compiledDescriptor = new CompiledDescriptor();
//randomizedDescriptor = DimensionDescriptor.EMPTY;//@todo
            DescriptorError error = compiledDescriptor.compile(descriptor, randomizedDescriptor);
            if (!error.isOk()) {
                RFToolsDim.setup.getLogger().error("Error compiling dimension descriptor: " + error.getMessage());
//                throw new RuntimeException("Error compiling dimension descriptor: " + error.getMessage());
            }
        }
        return compiledDescriptor;
    }
}
