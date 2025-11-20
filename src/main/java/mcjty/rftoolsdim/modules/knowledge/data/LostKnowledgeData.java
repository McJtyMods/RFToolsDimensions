package mcjty.rftoolsdim.modules.knowledge.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for LostKnowledge item data.
 */
public record LostKnowledgeData(String pattern, String reason) {

    public static final LostKnowledgeData DEFAULT = new LostKnowledgeData("", "");

    public static final Codec<LostKnowledgeData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("pattern").forGetter(LostKnowledgeData::pattern),
            Codec.STRING.fieldOf("reason").forGetter(LostKnowledgeData::reason)
    ).apply(inst, LostKnowledgeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LostKnowledgeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LostKnowledgeData::pattern,
            ByteBufCodecs.STRING_UTF8, LostKnowledgeData::reason,
            LostKnowledgeData::new
    );
}
