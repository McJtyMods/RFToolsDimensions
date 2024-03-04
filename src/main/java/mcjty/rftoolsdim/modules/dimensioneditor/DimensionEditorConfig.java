package mcjty.rftoolsdim.modules.dimensioneditor;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DimensionEditorConfig {

    public static final String SUB_CATEGORY_DIMENSION_EDITOR = "dimensioneditor";

    public static ModConfigSpec.IntValue EDITOR_MAXENERGY;
    public static ModConfigSpec.IntValue EDITOR_RECEIVEPERTICK;
    public static ModConfigSpec.BooleanValue TNT_CAN_DESTROY_DIMENSION;


    public static void init(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimension Editor settings").push(SUB_CATEGORY_DIMENSION_EDITOR);

        EDITOR_MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension editor can hold")
                .defineInRange("generatorMaxRF", 10000000, 0, Integer.MAX_VALUE);
        EDITOR_RECEIVEPERTICK = SERVER_BUILDER
                .comment("Maximum RF storage that the dimension editor can receive per side")
                .defineInRange("generatorMaxRF", 20000, 0, Integer.MAX_VALUE);
        TNT_CAN_DESTROY_DIMENSION = SERVER_BUILDER
                .comment("Set to true to allow the dimension editor to destroy dimensions using tnt")
                .define("tntCanDestroyDimension", true);

        SERVER_BUILDER.pop();
    }

}
