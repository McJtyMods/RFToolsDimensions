package mcjty.rftoolsdim.dimension.network;

import mcjty.rftoolsdim.dimension.power.ClientPowerManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketPropagatePowerToClients {

    private Map<ResourceLocation, Long> powerMap;

    public PacketPropagatePowerToClients(PacketBuffer buf) {
        int size = buf.readInt();
        powerMap = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            ResourceLocation id = buf.readResourceLocation();
            long power = buf.readLong();
            powerMap.put(id, power);
        }
    }

    public PacketPropagatePowerToClients(Map<ResourceLocation, Long> powerMap) {
        this.powerMap = new HashMap<>(powerMap);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(powerMap.size());
        for (Map.Entry<ResourceLocation, Long> entry : powerMap.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeLong(entry.getValue());
        }
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> ClientPowerManager.get().updatePowerFromServer(powerMap));
        ctx.setPacketHandled(true);
    }

}