package mcjty.rftoolsdim.network;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.config.DimletRules;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SyncRulesHelper {

    public static void syncRulesFromServer(PacketSyncRules message) {
        Logging.log("Received dimlet rules from server");
        DimletRules.syncRulesFromServer(message.getRules());
    }

}
