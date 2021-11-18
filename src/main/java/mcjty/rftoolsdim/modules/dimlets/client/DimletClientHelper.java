package mcjty.rftoolsdim.modules.dimlets.client;

import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        public DimletWithInfo(DimletKey dimlet, boolean craftable) {
            this.dimlet = dimlet;
            this.craftable = craftable;
        }

        public static void toPacket(PacketBuffer buf, DimletWithInfo info) {
            DimletKey dimlet = info.getDimlet();
            buf.writeShort(dimlet.getType().ordinal());
            buf.writeUtf(dimlet.getKey());
            buf.writeBoolean(info.isCraftable());
        }

        public static DimletWithInfo fromPacket(PacketBuffer buf) {
            short idx = buf.readShort();
            DimletType type = DimletType.values()[idx];
            String key = buf.readUtf(32767);
            DimletKey dimlet = new DimletKey(type, key);
            boolean craftable = buf.readBoolean();
            return new DimletClientHelper.DimletWithInfo(dimlet, craftable);
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
