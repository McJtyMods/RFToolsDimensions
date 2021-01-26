package mcjty.rftoolsdim.modules.dimlets;

import net.minecraftforge.common.ForgeConfigSpec;

public class DimletConfig {

    public static final String SUB_CATEGORY_DIMLETS = "dimlets";

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimlets settings").push(SUB_CATEGORY_DIMLETS);

        SERVER_BUILDER.pop();
    }

}
