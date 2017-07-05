package mcjty.rftoolsdim.network;

import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnEnergyHelper {
    public static void setEnergyLevel(PacketReturnEnergy message) {
        World world = Minecraft.getMinecraft().world;
        DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);
        dimensionStorage.setEnergyLevel(message.getId(), message.getEnergy());
    }

}
