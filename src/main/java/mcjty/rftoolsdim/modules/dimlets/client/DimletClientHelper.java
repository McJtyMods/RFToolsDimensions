package mcjty.rftoolsdim.modules.dimlets.client;

import mcjty.lib.blockcommands.ISerializer;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DimletClientHelper {

    public static long dimletListAge = 0;
    public static List<DimletWithInfo> dimlets = new ArrayList<>();

    public static void setDimletsOnGui(List<DimletWithInfo> dimlets) {
        DimletClientHelper.dimlets = dimlets;
        dimletListAge++;
    }

    public record DimletWithInfo(DimletKey dimlet, boolean craftable) implements Comparable<DimletWithInfo> {

        public static class Serializer implements ISerializer<DimletWithInfo> {
            @Override
            public Function<FriendlyByteBuf, DimletWithInfo> getDeserializer() {
                return buf -> {
                    short idx = buf.readShort();
                    DimletType type = DimletType.values()[idx];
                    String key = buf.readUtf(32767);
                    DimletKey dimlet1 = new DimletKey(type, key);
                    boolean craftable1 = buf.readBoolean();
                    return new DimletWithInfo(dimlet1, craftable1);
                };
            }

            @Override
            public BiConsumer<FriendlyByteBuf, DimletWithInfo> getSerializer() {
                return (buf, info) -> {
                    DimletKey dimlet1 = info.dimlet();
                    buf.writeShort(dimlet1.type().ordinal());
                    buf.writeUtf(dimlet1.key());
                    buf.writeBoolean(info.craftable());
                };
            }
        }

        @Override
        public int compareTo(DimletWithInfo o) {
            return dimlet().compareTo(o.dimlet());
        }
    }
}
