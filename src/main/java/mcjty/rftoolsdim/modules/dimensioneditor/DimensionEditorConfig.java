package mcjty.rftoolsdim.modules.dimensioneditor;

import net.minecraftforge.common.ForgeConfigSpec;

public class DimensionEditorConfig {

    public static final String SUB_CATEGORY_DIMENSION_EDITOR = "dimensioneditor";

    public static ForgeConfigSpec.IntValue EDITOR_MAXENERGY;
    public static ForgeConfigSpec.IntValue EDITOR_RECEIVEPERTICK;


    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimension Editor settings").push(SUB_CATEGORY_DIMENSION_EDITOR);

        EDITOR_MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension editor can hold")
                .defineInRange("generatorMaxRF", 10000000, 0, Integer.MAX_VALUE);
        EDITOR_RECEIVEPERTICK = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension editor can receive per side")
                .defineInRange("generatorMaxRF", 20000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
