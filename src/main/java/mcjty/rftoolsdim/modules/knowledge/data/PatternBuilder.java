package mcjty.rftoolsdim.modules.knowledge.data;

import static mcjty.rftoolsdim.modules.knowledge.data.DimletPattern.PATTERN_DIM;

public class PatternBuilder {

    public static final char EMPTY = ' ';
    public static final char SHARD = '*';
    public static final char LEV0 = '0';
    public static final char LEV1 = '1';
    public static final char LEV2 = '2';

    private char[][] pattern = new char[][] {
            { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY }
    };

    public void set(int x, int y, char s) {
        pattern[x][y] = s;
    }

    public DimletPattern build() {
        String[] p = new String[PATTERN_DIM];
        for (int y = 0 ; y < PATTERN_DIM ; y++) {
            String s = "";
            for (int x = 0 ; x < PATTERN_DIM ; x++) {
                s += pattern[x][y];
            }
            p[y] = s;
        }
        return new DimletPattern(p);
    }

}
