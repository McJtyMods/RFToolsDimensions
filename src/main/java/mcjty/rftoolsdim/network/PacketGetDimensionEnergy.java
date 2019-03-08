package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketGetDimensionEnergy implements IMessage {
    private int dimension;

    @Override
    public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
    }

    public PacketGetDimensionEnergy() {
    }

    public PacketGetDimensionEnergy(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetDimensionEnergy(int dimension) {
        this.dimension = dimension;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getEntityWorld();
            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);

            PacketReturnEnergy returnMessage = new PacketReturnEnergy(dimension, dimensionStorage.getEnergyLevel(dimension));
            RFToolsDimMessages.INSTANCE.sendTo(returnMessage, ctx.getSender());
        });
        ctx.setPacketHandled(true);
    }
}