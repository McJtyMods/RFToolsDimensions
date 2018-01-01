package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sync RfToolsDimensionManager data from server to client.
 */
public class PacketSyncDimensionInfo implements IMessage {
    private Map<Integer, DimensionDescriptor> dimensions;
    private Map<Integer, DimensionInformation> dimensionInformation;

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        dimensions = new HashMap<>();
        for (int i = 0 ; i < size ; i++) {
            int id = buf.readInt();
            PacketBuffer buffer = new PacketBuffer(buf);
            NBTTagCompound tagCompound;
            try {
                tagCompound = buffer.readCompoundTag();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            DimensionDescriptor descriptor = new DimensionDescriptor(tagCompound);
            dimensions.put(id, descriptor);
        }

        size = buf.readInt();
        dimensionInformation = new HashMap<>();
        for (int i = 0 ; i < size ; i++) {
            int id = buf.readInt();
            String name = NetworkTools.readString(buf);
            DimensionInformation dimInfo = new DimensionInformation(name, dimensions.get(id), buf);
            dimensionInformation.put(id, dimInfo);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensions.size());
        for (Map.Entry<Integer,DimensionDescriptor> me : dimensions.entrySet()) {
            buf.writeInt(me.getKey());
            NBTTagCompound tagCompound = new NBTTagCompound();
            me.getValue().writeToNBT(tagCompound);
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeCompoundTag(tagCompound);
        }

        buf.writeInt(dimensionInformation.size());
        for (Map.Entry<Integer, DimensionInformation> me : dimensionInformation.entrySet()) {
            buf.writeInt(me.getKey());
            DimensionInformation dimInfo = me.getValue();
            NetworkTools.writeString(buf, dimInfo.getName());
            dimInfo.toBytes(buf);
        }
    }

    public PacketSyncDimensionInfo() {
    }

    public PacketSyncDimensionInfo(Map<Integer, DimensionDescriptor> dimensions, Map<Integer, DimensionInformation> dimensionInformation) {
        this.dimensions = new HashMap<>(dimensions);
        this.dimensionInformation = new HashMap<>(dimensionInformation);
    }

    public static class Handler implements IMessageHandler<PacketSyncDimensionInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncDimensionInfo message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> SyncDimensionInfoHelper.syncDimensionManagerFromServer(message.dimensions, message.dimensionInformation));
            return null;
        }

    }
}
