package mcjty.rftoolsdim.modules.dimlets.client;

import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;

import java.util.ArrayList;
import java.util.List;

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
    }
}