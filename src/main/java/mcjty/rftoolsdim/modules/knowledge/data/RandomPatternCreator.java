package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;

import java.util.*;
import java.util.function.Supplier;

public class RandomPatternCreator {

    private static final List<String[]> PATTERNS = new ArrayList<>();

    private static List<String[]> getPatterns() {
        if (PATTERNS.isEmpty()) {
            generatePattern(
                    "##",
                    "##");
            generatePattern(
                    "# ",
                    " #");
            generatePattern(
                    " #",
                    "# ");
            generatePattern(
                    "##",
                    " #");
            generatePattern(
                    "##",
                    "# ");
            generatePattern(
                    " #",
                    "##");
            generatePattern(
                    "# ",
                    "##");
            generatePattern(
                    "###",
                    "###");
            generatePattern(
                    "##",
                    "##",
                    "##");
            generatePattern(
                    "####",
                    "####");
            generatePattern(
                    "##",
                    "##",
                    "##",
                    "##");
            generatePattern(
                    "# #",
                    "   ",
                    "# #");
            generatePattern(
                    "###",
                    "   ",
                    "###");
            generatePattern(
                    "# #",
                    "# #",
                    "# #");
            generatePattern(
                    "###",
                    "#  ",
                    "###");
            generatePattern(
                    "###",
                    "# #",
                    "# #");
            generatePattern(
                    "###",
                    "  #",
                    "###");
            generatePattern(
                    "# #",
                    "# #",
                    "###");
            generatePattern(
                    "###",
                    "# #",
                    "###");
            generatePattern(
                    "###",
                    " # ",
                    "###");
            generatePattern(
                    "# #",
                    "###",
                    "# #");
            generatePattern(
                    "#  ",
                    " # ",
                    "  #");
            generatePattern(
                    "  #",
                    " # ",
                    "#  ");
            generatePattern(
                    "###",
                    "#  ",
                    "#  ");
            generatePattern(
                    "###",
                    "  #",
                    "  #");
            generatePattern(
                    "#  ",
                    "#  ",
                    "###");
            generatePattern(
                    "  #",
                    "  #",
                    "###");
            generatePattern(
                    " ##",
                    "#  ",
                    "#  ");
            generatePattern(
                    "## ",
                    "  #",
                    "  #");
            generatePattern(
                    "#  ",
                    "#  ",
                    " ##");
            generatePattern(
                    "  #",
                    "  #",
                    "## ");
            generatePattern(
                    "# #",
                    " # ",
                    "# #");
            generatePattern(
                    " # ",
                    "###",
                    " # ");
            generatePattern(
                    "###",
                    "###",
                    "###");
            generatePattern(
                    "#  #",
                    "    ",
                    "    ",
                    "#  #");
            generatePattern(
                    "####",
                    "#   ",
                    "#   ",
                    "#   ");
            generatePattern(
                    "####",
                    "   #",
                    "   #",
                    "   #");
            generatePattern(
                    "#   ",
                    "#   ",
                    "#   ",
                    "####");
            generatePattern(
                    "   #",
                    "   #",
                    "   #",
                    "####");
            generatePattern(
                    " ###",
                    "#   ",
                    "#   ",
                    "#   ");
            generatePattern(
                    "### ",
                    "   #",
                    "   #",
                    "   #");
            generatePattern(
                    "#   ",
                    "#   ",
                    "#   ",
                    " ###");
            generatePattern(
                    "   #",
                    "   #",
                    "   #",
                    "### ");
            generatePattern(
                    "####",
                    "#  #",
                    "#  #",
                    "####");
            generatePattern(
                    " ## ",
                    "#  #",
                    "#  #",
                    " ## ");
            generatePattern(
                    "#  #",
                    " ## ",
                    " ## ",
                    "#  #");
            generatePattern(
                    "#   #",
                    "     ",
                    "     ",
                    "     ",
                    "#   #");
            generatePattern(
                    "#   #",
                    " # # ",
                    "  #  ",
                    " # # ",
                    "#   #");
            generatePattern(
                    "#   #",
                    " ### ",
                    " ### ",
                    " ### ",
                    "#   #");
            generatePattern(
                    "## ##",
                    "#   #",
                    "     ",
                    "#   #",
                    "## ##");
            generatePattern(
                    "#####",
                    "#   #",
                    "#   #",
                    "#   #",
                    "#####");
            generatePattern(
                    "#    #",
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "#    #");
            generatePattern(
                    "#    #",
                    " #  # ",
                    "  ##  ",
                    "  ##  ",
                    " #  # ",
                    "#    #");
            generatePattern(
                    "##  ##",
                    "#    #",
                    "      ",
                    "      ",
                    "#    #",
                    "##  ##");
            generatePattern(
                    "##  ##",
                    "##  ##",
                    "      ",
                    "      ",
                    "##  ##",
                    "##  ##");
            generatePattern(
                    "######",
                    "#    #",
                    "#    #",
                    "#    #",
                    "#    #",
                    "######");

            System.out.println("Generated patterns: " + PATTERNS.size());
        }
        return PATTERNS;
    }

    private static void generatePattern(String... pattern) {
        for (int y = 0; y <= 6 - pattern.length; y++) {
            for (int x = 0; x <= 6 - pattern[0].length(); x++) {
                String[] ppp = new String[6];
                for (int py = 0; py < 6; py++) {
                    StringBuilder pat = new StringBuilder();
                    if (py < y) {
                        pat = new StringBuilder("      ");
                    } else if (py >= y + pattern.length) {
                        pat = new StringBuilder("      ");
                    } else for (int px = 0; px < 6; px++) {
                        if (px < x) {
                            pat.append(" ");
                        } else if (px >= x + pattern[0].length()) {
                            pat.append(" ");
                        } else {
                            pat.append(pattern[py - y].charAt(px - x));
                        }
                    }
                    ppp[py] = pat.toString();
                }
                RandomPatternCreator.PATTERNS.add(ppp);
            }
        }
    }

    // Find an unused pattern and add it to the set of already used patterns
    private static SelectedPattern findUnusedPattern(Set<SelectedPattern> alreadyUsed, Supplier<SelectedPattern> generator) {
        SelectedPattern pattern = generator.get();
        while (alreadyUsed.contains(pattern)) {
            pattern = generator.get();
        }
        alreadyUsed.add(pattern);
        return pattern;
    }

    private static void applyPattern(PatternBuilder builder, String[] pattern, char c) {
        for (int y = 0; y < pattern.length; y++) {
            for (int x = 0; x < pattern[y].length(); x++) {
                char p = pattern[y].charAt(x);
                if (p == '#') {
                    builder.set(x, y, c);
                }
            }
        }
    }

    private static DimletPattern buildPattern(SelectedPattern selectedPattern) {
        PatternBuilder builder = new PatternBuilder();
        List<String[]> patterns = getPatterns();
        int i1 = selectedPattern.i1();
        applyPattern(builder, patterns.get(i1), PatternBuilder.SHARD);
        int i2 = selectedPattern.i2();
        if (i2 != -1) {
            applyPattern(builder, patterns.get(i2), PatternBuilder.LEV0);
        }
        int i3 = selectedPattern.i3();
        if (i3 != -1) {
            applyPattern(builder, patterns.get(i3), PatternBuilder.LEV1);
        }
        int i4 = selectedPattern.i4();
        if (i4 != -1) {
            applyPattern(builder, patterns.get(i4), PatternBuilder.LEV2);
        }

        return builder.build();
    }


    /**
     * Create a set of random patterns based on seed (so it's constant for any given world)
     */
    public static Map<KnowledgeKey, DimletPattern> createRandomPatterns(long seed) {
        Set<SelectedPattern> selectedPatterns = new HashSet<>();
        Map<KnowledgeKey, SelectedPattern> patternMap = new HashMap<>();

        Random random = new Random(seed);
        random.nextInt();
        random.nextInt();
        for (KnowledgeSet set : KnowledgeSet.values()) {
            for (DimletType type : DimletType.values()) {
                SelectedPattern pattern0 = findUnusedPattern(selectedPatterns, () -> new SelectedPattern(r(random), -1, -1, -1));
                patternMap.put(new KnowledgeKey(type, DimletRarity.COMMON, set), pattern0);
                SelectedPattern pattern1 = findUnusedPattern(selectedPatterns, () -> new SelectedPattern(r(random), r(random), -1, -1));
                patternMap.put(new KnowledgeKey(type, DimletRarity.UNCOMMON, set), pattern1);
                SelectedPattern pattern2 = findUnusedPattern(selectedPatterns, () -> new SelectedPattern(r(random), r(random), r(random), -1));
                patternMap.put(new KnowledgeKey(type, DimletRarity.RARE, set), pattern2);
                SelectedPattern pattern3 = findUnusedPattern(selectedPatterns, () -> new SelectedPattern(r(random), r(random), r(random), r(random)));
                patternMap.put(new KnowledgeKey(type, DimletRarity.LEGENDARY, set), pattern3);
            }
        }

        Map<KnowledgeKey, DimletPattern> patterns = new HashMap<>();
        for (Map.Entry<KnowledgeKey, SelectedPattern> entry : patternMap.entrySet()) {
            SelectedPattern selectedPattern = entry.getValue();
            patterns.put(entry.getKey(), buildPattern(selectedPattern));
        }
        return patterns;
    }

    private static int r(Random random) {
        return random.nextInt(getPatterns().size());
    }

    private record SelectedPattern(int i1, int i2, int i3, int i4) {
    }

    public static void main(String[] args) {
        int idx = 1;
        for (String[] pattern : getPatterns()) {
            System.out.println("Pattern " + idx);
            for (String s : pattern) {
                System.out.println("    " + s.replace(' ', '.'));
            }

            idx++;
        }
    }
}
