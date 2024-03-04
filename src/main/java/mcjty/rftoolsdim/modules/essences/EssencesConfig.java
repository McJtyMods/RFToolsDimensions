package mcjty.rftoolsdim.modules.essences;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EssencesConfig {

    public static final String SUB_CATEGORY_ESSENCES = "essences";

    public static ModConfigSpec.IntValue maxBlockAbsorption;
    public static ModConfigSpec.IntValue maxFluidAbsorption;
    public static ModConfigSpec.IntValue maxBiomeAbsorption;
    public static ModConfigSpec.IntValue maxStructureAbsorption;

    public static void init(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Essence settings").push(SUB_CATEGORY_ESSENCES);

        maxBlockAbsorption = SERVER_BUILDER
                .comment("Amount of blocks needed for a single block dimlet (for the block absorber)")
                .defineInRange("maxBlockAbsorption", 256, 1, Integer.MAX_VALUE);
        maxFluidAbsorption = SERVER_BUILDER
                .comment("Amount of fluid blocks needed for a single fluid dimlet (for the fluid absorber)")
                .defineInRange("maxFluidAbsorption", 256, 1, Integer.MAX_VALUE);
        maxBiomeAbsorption = SERVER_BUILDER
                .comment("Amount of ticks needed for a single biome dimlet (for the biome absorber)")
                .defineInRange("maxBiomeAbsorption", 5000, 1, Integer.MAX_VALUE);
        maxStructureAbsorption = SERVER_BUILDER
                .comment("Amount of ticks needed for a single structure dimlet (for the structure absorber)")
                .defineInRange("maxStructureAbsorption", 5000, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
