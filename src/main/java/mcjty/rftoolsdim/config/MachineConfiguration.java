package mcjty.rftoolsdim.config;

import net.minecraftforge.common.config.Configuration;

public class MachineConfiguration {
    public static final String CATEGORY_MACHINES = "machines";

    public static int EXTRACTOR_MAXENERGY = 50000;
    public static int EXTRACTOR_SENDPERTICK = 1000;
    public static int BUILDER_MAXENERGY = 10000000;
    public static int BUILDER_RECEIVEPERTICK = 50000;
    public static int EDITOR_MAXENERGY = 5000000;
    public static int EDITOR_RECEIVEPERTICK = 50000;
    public static int WORKBENCH_MAXENERGY = 32000;
    public static int WORKBENCH_RECEIVEPERTICK = 80;
    public static int workbenchRfPerDimlet = 200;


    public static void init(Configuration cfg) {
        EXTRACTOR_MAXENERGY = cfg.get(CATEGORY_MACHINES, "energyExtractorMaxRF", MachineConfiguration.EXTRACTOR_MAXENERGY,
                                                           "Maximum RF storage that the energy extractor can hold").getInt();
        EXTRACTOR_SENDPERTICK = cfg.get(CATEGORY_MACHINES, "energyExtractorRFPerTick", MachineConfiguration.EXTRACTOR_SENDPERTICK,
                                                             "RF per tick that the energy extractor can send").getInt();

        BUILDER_MAXENERGY = cfg.get(CATEGORY_MACHINES, "dimensionBuilderMaxRF", MachineConfiguration.BUILDER_MAXENERGY,
                                                         "Maximum RF storage that the dimension builder can hold").getInt();
        BUILDER_RECEIVEPERTICK = cfg.get(CATEGORY_MACHINES, "dimensionBuilderRFPerTick", MachineConfiguration.BUILDER_RECEIVEPERTICK,
                                                              "RF per tick that the dimension builder can receive").getInt();
        EDITOR_MAXENERGY = cfg.get(CATEGORY_MACHINES, "dimensionEditorMaxRF", MachineConfiguration.EDITOR_MAXENERGY,
                                                        "Maximum RF storage that the dimension editor can hold").getInt();
        EDITOR_RECEIVEPERTICK = cfg.get(CATEGORY_MACHINES, "dimensionEditorRFPerTick", MachineConfiguration.EDITOR_RECEIVEPERTICK,
                                                             "RF per tick that the dimension editor can receive").getInt();
        WORKBENCH_MAXENERGY = cfg.get(CATEGORY_MACHINES, "dimletWorkbenchMaxRF", WORKBENCH_MAXENERGY,
                                      "Maximum RF storage that the dimlet workbench can hold").getInt();
        WORKBENCH_RECEIVEPERTICK = cfg.get(CATEGORY_MACHINES, "dimletWorkbenchRFPerTick", WORKBENCH_RECEIVEPERTICK,
                                           "RF per tick that the the dimlet workbench can receive").getInt();
        workbenchRfPerDimlet = cfg.get(CATEGORY_MACHINES, "dimletWorkbenchRFPerOperation", workbenchRfPerDimlet,
                                     "RF that the dimlet workbench needs for extracting one dimlet").getInt();
    }

}
