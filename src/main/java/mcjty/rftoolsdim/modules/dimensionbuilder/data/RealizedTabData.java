package mcjty.rftoolsdim.modules.dimensionbuilder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.CompositeStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Data component for realized dimension tab metadata that used to live in NBT.
 */
public record RealizedTabData(Optional<ResourceLocation> dimension,
                              Optional<String> name,
                              String descriptor,
                              String randomized,
                              long forcedSeed,
                              int ticksLeft,
                              int rfCreateCost,
                              int rfMaintainCost,
                              int tickCost) {

    public static final RealizedTabData DEFAULT = new RealizedTabData(Optional.empty(), Optional.empty(), "", "", 0L, 0, 0, 0, 0);

    public static final Codec<RealizedTabData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("dimension").forGetter(RealizedTabData::dimension),
            Codec.STRING.optionalFieldOf("name").forGetter(RealizedTabData::name),
            Codec.STRING.fieldOf("descriptor").forGetter(RealizedTabData::descriptor),
            Codec.STRING.fieldOf("randomized").forGetter(RealizedTabData::randomized),
            Codec.LONG.fieldOf("forcedSeed").forGetter(RealizedTabData::forcedSeed),
            Codec.INT.fieldOf("ticksLeft").forGetter(RealizedTabData::ticksLeft),
            Codec.INT.fieldOf("rfCreateCost").forGetter(RealizedTabData::rfCreateCost),
            Codec.INT.fieldOf("rfMaintainCost").forGetter(RealizedTabData::rfMaintainCost),
            Codec.INT.fieldOf("tickCost").forGetter(RealizedTabData::tickCost)
    ).apply(inst, RealizedTabData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RealizedTabData> STREAM_CODEC = CompositeStreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), RealizedTabData::dimension,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), RealizedTabData::name,
            ByteBufCodecs.STRING_UTF8, RealizedTabData::descriptor,
            ByteBufCodecs.STRING_UTF8, RealizedTabData::randomized,
            ByteBufCodecs.VAR_LONG, RealizedTabData::forcedSeed,
            ByteBufCodecs.INT, RealizedTabData::ticksLeft,
            ByteBufCodecs.INT, RealizedTabData::rfCreateCost,
            ByteBufCodecs.INT, RealizedTabData::rfMaintainCost,
            ByteBufCodecs.INT, RealizedTabData::tickCost,
            (dimension, name, descriptor, randomized, ticksLeft, rfCreateCost, rfMaintainCost, tickCost) -> new RealizedTabData(dimension, name, descriptor, randomized, ticksLeft, rfCreateCost, rfMaintainCost, tickCost)
    );

    public RealizedTabData withTicksLeft(int ticksLeft) {
        return new RealizedTabData(dimension, name, descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }

    public RealizedTabData withDimension(ResourceLocation dimension) {
        return new RealizedTabData(Optional.ofNullable(dimension), name, descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }

    public RealizedTabData withRfMaintainCost(int rfMaintainCost) {
        return new RealizedTabData(dimension, name, descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }

    public RealizedTabData withName(String name) {
        return new RealizedTabData(dimension, Optional.ofNullable(name), descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }

    public RealizedTabData withDescriptor(String descriptor) {
        return new RealizedTabData(dimension, name, descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }

    public RealizedTabData withTickCost(int tickCost) {
        return new RealizedTabData(dimension, name, descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }

    public RealizedTabData withRfCreateCost(int rfCreateCost) {
        return new RealizedTabData(dimension, name, descriptor, randomized, forcedSeed, ticksLeft, rfCreateCost, rfMaintainCost, tickCost);
    }
}
