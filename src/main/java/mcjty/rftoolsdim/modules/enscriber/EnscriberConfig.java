package mcjty.rftoolsdim.modules.enscriber;

import net.neoforged.neoforge.common.ForgeConfigSpec;

public class EnscriberConfig {

    public static final String SUB_CATEGORY_ENSCRIBER = "enscriber";

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Enscriber settings").push(SUB_CATEGORY_ENSCRIBER);

        SERVER_BUILDER.pop();
    }

}
