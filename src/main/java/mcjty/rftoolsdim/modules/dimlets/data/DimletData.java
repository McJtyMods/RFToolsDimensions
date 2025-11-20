package mcjty.rftoolsdim.modules.dimlets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DimletData(String name) {

    public static final DimletData DEFAULT = new DimletData("");

    public static final Codec<DimletData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(DimletData::name)
    ).apply(inst, DimletData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DimletData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DimletData::name,
            DimletData::new);
}
