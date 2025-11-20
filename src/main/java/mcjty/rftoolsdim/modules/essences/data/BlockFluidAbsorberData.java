package mcjty.rftoolsdim.modules.essences.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Data component for the Phased Field Generator energy.
 */
public record BlockFluidAbsorberData(ResourceLocation block, int absorbing) {

    public static final BlockFluidAbsorberData DEFAULT = new BlockFluidAbsorberData(null, 0);

    public static final Codec<BlockFluidAbsorberData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("block").forGetter(d -> Optional.ofNullable(d.block)),
            Codec.INT.fieldOf("absorbing").forGetter(BlockFluidAbsorberData::absorbing)
    ).apply(inst, (block, absorbing) -> new BlockFluidAbsorberData(block.orElse(null), absorbing)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockFluidAbsorberData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), d -> Optional.ofNullable(d.block),
            ByteBufCodecs.INT, BlockFluidAbsorberData::absorbing,
            (block, absorbing) -> new BlockFluidAbsorberData(block.orElse(null), absorbing));
}
