package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.world.GenericWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public PacketRegisterDimensions(int id) {
        this.id = id;
    }

    public static class Handler implements IMessageHandler<PacketRegisterDimensions, IMessage> {
        @Override
        public IMessage onMessage(PacketRegisterDimensions message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message));
            return null;
        }

        private void handle(PacketRegisterDimensions message) {
            int id = message.getId();
            if (DimensionManager.isDimensionRegistered(id)) {
                Logging.log("Client side, already registered dimension: " + id);
            } else {
                Logging.log("Client side, register dimension: " + id);
                DimensionType type = DimensionType.register("rftools dimension" + id, "_rftools", id, GenericWorldProvider.class, false);
                DimensionManager.registerDimension(id, type);
            }
        }

    }

}
