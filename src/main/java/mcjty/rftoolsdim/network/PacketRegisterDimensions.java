package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketRegisterDimensions implements IMessage {
    private int id;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public int getId() {
        return id;
    }

    public PacketRegisterDimensions() {
    }

    public PacketRegisterDimensions(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketRegisterDimensions(int id) {
        this.id = id;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (DimensionManager.isDimensionRegistered(id)) {
                Logging.log("Client side, already registered dimension: " + id);
            } else {
                Logging.log("Client side, register dimension: " + id);
                DimensionManager.registerDimension(id, ModDimensions.rftoolsType);
            }
        });
        ctx.setPacketHandled(true);
    }
}
