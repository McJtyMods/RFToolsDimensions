package mcjty.rftoolsdim.config;

import net.minecraftforge.common.config.Configuration;

public class OresAPlentyConfiguration {
    public static final String CATEGORY_ORESAPLENTY = "oresaplenty";

    public static Settings coal = new Settings(40, 22, 0, 128);
    public static Settings iron = new Settings(20, 20, 0, 64);
    public static Settings gold = new Settings(20, 3, 0, 32);
    public static Settings lapis = new Settings(20, 2, 0, 16);
    public static Settings diamond = new Settings(20, 2, 0, 16);
    public static Settings redstone = new Settings(20, 10, 0, 16);
    public static Settings emerald = new Settings(16, 1, 0, 32);

    public static void init(Configuration cfg) {
        coal = new Settings(cfg, "extraCoal", coal);
        iron = new Settings(cfg, "extraIron", iron);
        gold = new Settings(cfg, "extraGold", gold);
        lapis = new Settings(cfg, "extraLapis", lapis);
        diamond = new Settings(cfg, "extraDiamond", diamond);
        redstone = new Settings(cfg, "extraRedstone", redstone);
        emerald = new Settings(cfg, "extraEmerald", emerald);
    }

    public static class Settings {
        private final int size;
        private final int count;
        private final int min;
        private final int max;

        public Settings(int size, int count, int min, int max) {
            this.size = size;
            this.count = count;
            this.min = min;
            this.max = max;
        }

        public Settings(Configuration cfg, String prefix, Settings def) {
            size = cfg.get(CATEGORY_ORESAPLENTY, prefix + "Size", def.getSize()).getInt();
            count = cfg.get(CATEGORY_ORESAPLENTY, prefix + "Count", def.getCount()).getInt();
            min = cfg.get(CATEGORY_ORESAPLENTY, prefix + "MinHeight", def.getMin()).getInt();
            max = cfg.get(CATEGORY_ORESAPLENTY, prefix + "MaxHeight", def.getMax()).getInt();
        }

        public int getSize() {
            return size;
        }

        public int getCount() {
            return count;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

}
