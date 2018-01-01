package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.DimensionInformation;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import mcjty.rftoolsdim.dimensions.description.DimensionDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.DimensionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sync dimension IDs and RfToolsDimensionManager data from server to client.
 */
public class DimensionSyncPacket {
    private final Map<Integer, DimensionDescriptor> dimensions;
    private final Map<Integer, DimensionInformation> dimensionInformation;

    public DimensionSyncPacket() {
        this.dimensions = new HashMap<>();
        this.dimensionInformation = new HashMap<>();
    }

    public DimensionSyncPacket(Map<Integer, DimensionDescriptor> dimensions, Map<Integer, DimensionInformation> dimensionInformation) {
        this.dimensions = dimensions;
        this.dimensionInformation = dimensionInformation;
    }

    public void consumePacket(ByteBuf data) {
        int size = data.readInt();
        for (int i = 0 ; i < size ; i++) {
            int id = data.readInt();
            PacketBuffer buffer = new PacketBuffer(data);
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

        size = data.readInt();
        for (int i = 0 ; i < size ; i++) {
            int id = data.readInt();
            String name = NetworkTools.readString(data);
            DimensionInformation dimInfo = new DimensionInformation(name, dimensions.get(id), data);
            dimensionInformation.put(id, dimInfo);
        }
    }

    public ByteBuf getData() {
        ByteBuf data = Unpooled.buffer();
        data.writeInt(dimensions.size());
        for (Map.Entry<Integer,DimensionDescriptor> me : dimensions.entrySet()) {
            data.writeInt(me.getKey());
            NBTTagCompound tagCompound = new NBTTagCompound();
            me.getValue().writeToNBT(tagCompound);
            PacketBuffer buffer = new PacketBuffer(data);
            buffer.writeCompoundTag(tagCompound);
        }

        data.writeInt(dimensionInformation.size());
        for (Map.Entry<Integer, DimensionInformation> me : dimensionInformation.entrySet()) {
            data.writeInt(me.getKey());
            DimensionInformation dimInfo = me.getValue();
            NetworkTools.writeString(data, dimInfo.getName());
            dimInfo.toBytes(data);
        }
        return data;
    }

    public void execute() {
        // Only do this on client side.
        StringBuilder builder = new StringBuilder();
        for (int id : dimensions.keySet()) {
            builder.append(id);
            builder.append(' ');
            if (!DimensionManager.isDimensionRegistered(id)) {
                DimensionManager.registerDimension(id, ModDimensions.rftoolsType);
            }
        }
        Logging.log("DimensionSyncPacket: Registering: " + builder.toString());
        Minecraft.getMinecraft().addScheduledTask(() -> SyncDimensionInfoHelper.syncDimensionManagerFromServer(dimensions, dimensionInformation));
    }
}
