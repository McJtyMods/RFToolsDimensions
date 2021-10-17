package mcjty.rftoolsdim.dimension.network;

import mcjty.lib.varia.WorldTools;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDimensionUpdate {

    private final RegistryKey<World> id;
    private final boolean add;

    public RegistryKey<World> getId() {
        return this.id;
    }

    public boolean getAdd() {
        return this.add;
    }

    public PacketDimensionUpdate(PacketBuffer buf) {
        id = WorldTools.getId(buf.readResourceLocation());
        add = buf.readBoolean();
    }

    public PacketDimensionUpdate(RegistryKey<World> id, boolean add) {
        this.id = id;
        this.add = add;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(id.location());
        buf.writeBoolean(add);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> PacketDimensionUpdateClient.handleUpdateDimensionsPacket(this));
        ctx.setPacketHandled(true);
    }

}