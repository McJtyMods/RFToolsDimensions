package mcjty.rftoolsdim.modules.dimensionbuilder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.CompositeStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for the Phased Field Generator energy.
 */
public record PhasedFieldGeneratorData(long energy) {

    public static final PhasedFieldGeneratorData DEFAULT = new PhasedFieldGeneratorData(0L);

    public static final Codec<PhasedFieldGeneratorData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.LONG.fieldOf("energy").forGetter(PhasedFieldGeneratorData::energy)
    ).apply(inst, PhasedFieldGeneratorData::new));

    // Network stream codec (RegistryFriendlyByteBuf)
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, PhasedFieldGeneratorData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, PhasedFieldGeneratorData::energy,
                    PhasedFieldGeneratorData::new
            );
}
