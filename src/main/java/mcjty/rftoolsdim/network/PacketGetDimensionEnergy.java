package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.rftoolsdim.dimensions.DimensionStorage;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public PacketGetDimensionEnergy(int dimension) {
        this.dimension = dimension;
    }

    public static class Handler implements IMessageHandler<PacketGetDimensionEnergy, PacketReturnEnergy> {
        @Override
        public PacketReturnEnergy onMessage(PacketGetDimensionEnergy message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetDimensionEnergy message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);

            PacketReturnEnergy returnMessage = new PacketReturnEnergy(message.dimension, dimensionStorage.getEnergyLevel(message.dimension));
            RFToolsDimMessages.INSTANCE.sendTo(returnMessage, ctx.getServerHandler().playerEntity);
        }

    }

}