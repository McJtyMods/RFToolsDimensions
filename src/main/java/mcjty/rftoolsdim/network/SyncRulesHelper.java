package mcjty.rftoolsdim.network;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.DimletRules;

public class SyncRulesHelper {

    public static void syncRulesFromServer(PacketSyncRules message) {
        Logging.log("Received dimlet rules from server");
        DimletRules.syncRulesFromServer(message.getRules());
    }

}
