package mcjty.rftoolsdim.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import net.minecraftforge.common.DimensionManager;

public class DimensionSyncPacket {

    private ByteBuf data = Unpooled.buffer();

    private int[] dimensions;

    public void addDimension(int id) {
        data.writeInt(id);
    }

    public void consumePacket(ByteBuf data) {
        int cnt = data.readableBytes() / 4;
        dimensions = new int[cnt];
        for (int i = 0 ; i < cnt ; i++) {
            dimensions[i] = data.readInt();
        }
    }

    public ByteBuf getData() {
        return data;
    }

    public void execute() {
        // Only do this on client side.
        StringBuilder builder = new StringBuilder();
        for (int id : dimensions) {
            builder.append(id);
            builder.append(' ');
            if (!DimensionManager.isDimensionRegistered(id)) {
                DimensionManager.registerDimension(id, ModDimensions.rftoolsType);
            }
        }
        Logging.log("DimensionSyncPacket: Registering: " + builder.toString());
    }
}
