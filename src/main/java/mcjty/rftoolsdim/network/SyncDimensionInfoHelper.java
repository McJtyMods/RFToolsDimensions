package mcjty.rftoolsdim.network;

import mcjty.lib.tools.MinecraftTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SyncDimensionInfoHelper {

    public static void syncDimensionManagerFromServer(PacketSyncDimensionInfo message) {
        World world = MinecraftTools.getWorld(Minecraft.getMinecraft());
        Logging.log("Received dimension information from server");
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManagerClient();

        dimensionManager.syncFromServer(message.getDimensions(), message.getDimensionInformation());
//        dimensionManager.save(world);
    }

}
