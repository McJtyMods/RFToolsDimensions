package mcjty.rftoolsdim.modules.essences;

import net.minecraftforge.common.ForgeConfigSpec;

public class EssencesConfig {

    public static final String SUB_CATEGORY_ESSENCES = "essences";

    public static ForgeConfigSpec.IntValue maxBlockAbsorption;
    public static ForgeConfigSpec.IntValue maxBiomeAbsorption;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Essence settings").push(SUB_CATEGORY_ESSENCES);

        maxBlockAbsorption = SERVER_BUILDER
                .comment("Amount of blocks needed for a single block dimlet (for the block absorber)")
                .defineInRange("maxBlockAbsorption", 256, 1, Integer.MAX_VALUE);
        maxBiomeAbsorption = SERVER_BUILDER
                .comment("Amount of ticks needed for a single biome dimlet (for the biome absorber)")
                .defineInRange("maxBiomeAbsorption", 5000, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
