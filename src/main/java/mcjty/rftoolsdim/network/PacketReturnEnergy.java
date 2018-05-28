package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public PacketReturnEnergy(int id, long energy) {
        this.id = id;
        this.energy = energy;
    }

    public static class Handler implements IMessageHandler<PacketReturnEnergy, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnEnergy message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> ReturnEnergyHelper.setEnergyLevel(message));
            return null;
        }

    }
}