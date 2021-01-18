package mcjty.rftoolsdim.modules.knowledge.data;

import java.util.Arrays;

public class DimletPattern {

    public static final int PATTERN_DIM = 7;

    private final String[] pattern;

    public DimletPattern(String[] pattern) {
        this.pattern = new String[PATTERN_DIM];
        System.arraycopy(pattern, 0, this.pattern, 0, PATTERN_DIM);
    }

    public String[] getPattern() {
        return pattern;
    }

    public int count(char s) {
        int cnt = 0;
        for (String p : pattern) {
            for (int i = 0 ; i < p.length() ; i++) {
                char c = p.charAt(i);
                if (c == s) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimletPattern that = (DimletPattern) o;
        return Arrays.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pattern);
    }
}
