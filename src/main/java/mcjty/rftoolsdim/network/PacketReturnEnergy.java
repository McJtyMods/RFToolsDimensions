package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketReturnEnergy implements IMessage {
    private int id;
    private long energy;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        energy = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeLong(energy);
    }

    public int getId() {
        return id;
    }

    public long getEnergy() {
        return energy;
    }

    public PacketReturnEnergy() {
    }

    public PacketReturnEnergy(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnEnergy(int id, long energy) {
        this.id = id;
        this.energy = energy;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnEnergyHelper.setEnergyLevel(this);
        });
        ctx.setPacketHandled(true);
    }
}