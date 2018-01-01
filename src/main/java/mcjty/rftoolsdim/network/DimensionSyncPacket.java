package mcjty.rftoolsdim.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsdim.dimensions.ModDimensions;
import net.minecraftforge.common.DimensionManager;

public class DimensionSyncPacket {

    private List<Integer> dimensions = new ArrayList<>();

    public void addDimension(int id) {
        dimensions.add(id);
    }

    public void consumePacket(ByteBuf data) {
        for (int i = data.readInt(); i > 0; --i) {
            dimensions.add(data.readInt());
        }
    }

    public ByteBuf getData() {
        ByteBuf data = Unpooled.buffer();
        data.writeInt(dimensions.size());
        dimensions.forEach(data::writeInt);
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
