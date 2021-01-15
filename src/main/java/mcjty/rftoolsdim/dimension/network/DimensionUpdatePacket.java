package mcjty.rftoolsdim.dimension.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DimensionUpdatePacket {

    private final RegistryKey<World> id;
    private final boolean add;

    public RegistryKey<World> getId() {
        return this.id;
    }

    public boolean getAdd() {
        return this.add;
    }

    public DimensionUpdatePacket(PacketBuffer buf) {
        id = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readResourceLocation());
        add = buf.readBoolean();
    }

    public DimensionUpdatePacket(RegistryKey<World> id, boolean add) {
        this.id = id;
        this.add = add;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(id.getLocation());
        buf.writeBoolean(add);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> DimensionUpdatePacketClient.handleUpdateDimensionsPacket(this));
    }

}