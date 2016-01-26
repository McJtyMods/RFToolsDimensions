package mcjty.rftoolsdim.network;

import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SyncRulesHelper {

    public static void syncRulesFromServer(PacketSyncRules message) {
        World world = Minecraft.getMinecraft().theWorld;
        Logging.log("Received dimlet rules from server");
//        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
//
//        dimensionManager.syncFromServer(message.getDimensions(), message.getDimensionInformation());
//        dimensionManager.save(world);
    }

}
