package mcjty.rftoolsdim.modules.knowledge.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

/**
 * Data component for LostKnowledge item data.
 */
public record LostKnowledgeData(String pattern, String reason) {

    public static final LostKnowledgeData DEFAULT = new LostKnowledgeData("", "");

    public static final Codec<LostKnowledgeData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("pattern").forGetter(LostKnowledgeData::pattern),
            Codec.STRING.optionalFieldOf("reason").forGetter(s -> Optional.ofNullable(s.reason))
    ).apply(inst, (pattern, reason) -> new LostKnowledgeData(pattern, reason.orElse(""))));

    public static final StreamCodec<RegistryFriendlyByteBuf, LostKnowledgeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LostKnowledgeData::pattern,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), s -> Optional.ofNullable(s.reason),
            (pattern, reason) -> new LostKnowledgeData(pattern, reason.orElse(""))
    );
}
