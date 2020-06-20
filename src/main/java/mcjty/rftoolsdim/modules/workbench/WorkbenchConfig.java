package mcjty.rftoolsdim.modules.workbench;

import net.minecraftforge.common.ForgeConfigSpec;

public class WorkbenchConfig {

    public static final String SUB_CATEGORY_WORKBENCH = "dimletworkbench";


    public static void setup(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Dimlet Workbench settings").push(SUB_CATEGORY_WORKBENCH);


        SERVER_BUILDER.pop();
    }

}
