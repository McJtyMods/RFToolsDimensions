package mcjty.rftoolsdim.dimension.network;

import mcjty.lib.varia.LevelTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDimensionUpdate {

    private final ResourceKey<Level> id;
    private final boolean add;

    public ResourceKey<Level> getId() {
        return this.id;
    }

    public boolean getAdd() {
        return this.add;
    }

    public PacketDimensionUpdate(FriendlyByteBuf buf) {
        id = LevelTools.getId(buf.readResourceLocation());
        add = buf.readBoolean();
    }

    public PacketDimensionUpdate(ResourceKey<Level> id, boolean add) {
        this.id = id;
        this.add = add;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id.location());
        buf.writeBoolean(add);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> PacketDimensionUpdateClient.handleUpdateDimensionsPacket(this));
        ctx.setPacketHandled(true);
    }

}