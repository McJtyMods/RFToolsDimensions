package mcjty.rftoolsdim.modules.blob;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BlobConfig {

    public static final String SUB_CATEGORY_BLOBS = "blobs";

    public static ModConfigSpec.IntValue BLOB_COMMON_HEALTH;
    public static ModConfigSpec.IntValue BLOB_RARE_HEALTH;
    public static ModConfigSpec.IntValue BLOB_LEGENDARY_HEALTH;

    public static ModConfigSpec.IntValue BLOB_COMMON_REGEN;
    public static ModConfigSpec.IntValue BLOB_RARE_REGEN;
    public static ModConfigSpec.IntValue BLOB_LEGENDARY_REGEN;

    public static ModConfigSpec.LongValue BLOB_REGENERATION_LEVEL;

    public static void init(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder COMMON_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimension Builder settings").push(SUB_CATEGORY_BLOBS);

        BLOB_COMMON_HEALTH = COMMON_BUILDER
                .comment("Maximum health of the common blob")
                .defineInRange("commonBlobMaxHealth", 30, 0, Integer.MAX_VALUE);
        BLOB_RARE_HEALTH = COMMON_BUILDER
                .comment("Maximum health of the rare blob")
                .defineInRange("rareBlobMaxHealth", 250, 0, Integer.MAX_VALUE);
        BLOB_LEGENDARY_HEALTH = COMMON_BUILDER
                .comment("Maximum health of the legendary blob")
                .defineInRange("legendaryBlobMaxHealth", 5000, 0, Integer.MAX_VALUE);

        BLOB_COMMON_REGEN = SERVER_BUILDER
                .comment("Regeneration level of the common blob in case the dimension has power")
                .defineInRange("commonBlobRegen", 2, 0, Integer.MAX_VALUE);
        BLOB_RARE_REGEN = SERVER_BUILDER
                .comment("Regeneration level of the rare blob in case the dimension has power")
                .defineInRange("rareBlobRegen", 3, 0, Integer.MAX_VALUE);
        BLOB_LEGENDARY_REGEN = SERVER_BUILDER
                .comment("Regeneration level of the legendary blob in case the dimension has power")
                .defineInRange("legendaryBlobRegen", 4, 0, Integer.MAX_VALUE);

        BLOB_REGENERATION_LEVEL = SERVER_BUILDER
                .comment("Below this dimension power the regeneration of the blobs stop")
                .defineInRange("blobRegenerationLevel", 1000L, 0, Long.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
