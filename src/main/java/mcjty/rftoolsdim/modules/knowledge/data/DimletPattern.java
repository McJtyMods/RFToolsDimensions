package mcjty.rftoolsdim.modules.knowledge.data;

public record DimletPattern(String[] pattern) {

    public static final int PATTERN_DIM = 6;

    public DimletPattern(String[] pattern) {
        this.pattern = new String[PATTERN_DIM];
        System.arraycopy(pattern, 0, this.pattern, 0, PATTERN_DIM);
    }

    public int count(char s) {
        int cnt = 0;
        for (String p : pattern) {
            for (int i = 0; i < p.length(); i++) {
                char c = p.charAt(i);
                if (c == s) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
