package mcjty.rftoolsdim.modules.enscriber;

import net.minecraftforge.common.ForgeConfigSpec;

public class EnscriberConfig {

    public static final String SUB_CATEGORY_ENSCRIBER = "enscriber";

    public static void setup(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Enscriber settings").push(SUB_CATEGORY_ENSCRIBER);

        SERVER_BUILDER.pop();
    }

}
