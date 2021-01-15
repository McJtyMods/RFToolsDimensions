package mcjty.rftoolsdim.modules.knowledge.data;

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
}
