package mcjty.rftoolsdim.modules.workbench;

import net.neoforged.neoforge.common.ModConfigSpec;

public class WorkbenchConfig {

    public static final String SUB_CATEGORY_WORKBENCH = "dimletworkbench";

    public static ModConfigSpec.IntValue RESEARCHER_MAXENERGY;
    public static ModConfigSpec.IntValue RESEARCHER_ENERGY_INPUT_PERTICK;
    public static ModConfigSpec.IntValue RESEARCHER_USE_PER_TICK;

    public static ModConfigSpec.IntValue RESEARCH_TIME;

    public static void init(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Dimlet Workbench settings").push(SUB_CATEGORY_WORKBENCH);

        RESEARCHER_MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the researcher can store")
                .defineInRange("researcherMaxPower", 100000, 0, Integer.MAX_VALUE);
        RESEARCHER_ENERGY_INPUT_PERTICK = SERVER_BUILDER
                .comment("Amount of RF per tick input (per side) for the researcher")
                .defineInRange("researcherRFPerTick", 10000, 0, Integer.MAX_VALUE);
        RESEARCHER_USE_PER_TICK = SERVER_BUILDER
                .comment("Amount of RF per tick the researcher uses while operating")
                .defineInRange("researcherUsePerTick", 200, 0, Integer.MAX_VALUE);
        RESEARCH_TIME = SERVER_BUILDER
                .comment("How many ticks are needed to research one item")
                .defineInRange("researcheTime", 20*20, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
