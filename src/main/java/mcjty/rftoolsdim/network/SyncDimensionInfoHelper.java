package mcjty.rftoolsdim.network;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.RfToolsDimensionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SyncDimensionInfoHelper {

    public static void syncDimensionManagerFromServer(PacketSyncDimensionInfo message) {
        World world = Minecraft.getMinecraft().theWorld;
        Logging.log("Received dimension information from server");
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);

        dimensionManager.syncFromServer(message.getDimensions(), message.getDimensionInformation());
        dimensionManager.save(world);
    }

}
