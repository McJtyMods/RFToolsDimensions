package mcjty.rftoolsdim.modules.dimlets.client;

import mcjty.lib.blockcommands.ISerializer;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DimletClientHelper {

    public static long dimletListAge = 0;
    public static List<DimletWithInfo> dimlets = new ArrayList<>();

    public static void setDimletsOnGui(List<DimletWithInfo> dimlets) {
        DimletClientHelper.dimlets = dimlets;
        dimletListAge++;
    }

    public static class DimletWithInfo implements Comparable<DimletWithInfo> {
        private final DimletKey dimlet;
        private final boolean craftable;

        public static class Serializer implements ISerializer<DimletWithInfo> {
            @Override
            public Function<PacketBuffer, DimletWithInfo> getDeserializer() {
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
            public BiConsumer<PacketBuffer, DimletWithInfo> getSerializer() {
                return (buf, info) -> {
                    DimletKey dimlet1 = info.getDimlet();
                    buf.writeShort(dimlet1.getType().ordinal());
                    buf.writeUtf(dimlet1.getKey());
                    buf.writeBoolean(info.isCraftable());
                };
            }
        }

        public DimletWithInfo(DimletKey dimlet, boolean craftable) {
            this.dimlet = dimlet;
            this.craftable = craftable;
        }

        public DimletKey getDimlet() {
            return dimlet;
        }

        public boolean isCraftable() {
            return craftable;
        }

        @Override
        public int compareTo(DimletWithInfo o) {
            return getDimlet().compareTo(o.getDimlet());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DimletWithInfo that = (DimletWithInfo) o;
            return craftable == that.craftable && Objects.equals(dimlet, that.dimlet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimlet, craftable);
        }
    }
}
