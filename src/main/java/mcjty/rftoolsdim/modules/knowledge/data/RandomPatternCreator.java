package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;

import java.util.*;
import java.util.function.Supplier;

public class RandomPatternCreator {

    private static final String[][] PATTERNS = {

            {
                    "      ",
                    "      ",
                    "  #   ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "   #  ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "  #   ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "   #  ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "  #   ",
                    " ###  ",
                    "  #   ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "  #   ",
                    " ###  ",
                    "  #   ",
                    "      "
            },

            {
                    "      ",
                    "   #  ",
                    "  ### ",
                    "   #  ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "   #  ",
                    "  ### ",
                    "   #  ",
                    "      "
            },

            {
                    "      ",
                    " # #  ",
                    "  #   ",
                    " # #  ",
                    "      ",
                    "      "
            },

            {
                    "       ",
                    "  # #  ",
                    "   #   ",
                    "  # #  ",
                    "       ",
                    "       "
            },

            {
                    "      ",
                    "      ",
                    " # #  ",
                    "  #   ",
                    " # #  ",
                    "      "
            },

            {
                    "       ",
                    "       ",
                    "  # #  ",
                    "   #   ",
                    "  # #  ",
                    "       "
            },

            {
                    "  #   ",
                    "  #   ",
                    "##### ",
                    "  #   ",
                    "  #   ",
                    "      "
            },

            {
                    "   #  ",
                    "   #  ",
                    " #####",
                    "   #  ",
                    "   #  ",
                    "      "
            },

            {
                    "      ",
                    "  #   ",
                    "  #   ",
                    "##### ",
                    "  #   ",
                    "  #   "
            },

            {
                    "      ",
                    "   #  ",
                    "   #  ",
                    " #####",
                    "   #  ",
                    "   #  "
            },

            {
                    "      ",
                    " #### ",
                    "    # ",
                    "    # ",
                    " #### ",
                    "      "
            },

            {
                    "      ",
                    " #### ",
                    " #    ",
                    " #    ",
                    " #### ",
                    "      "
            },

            {
                    "      ",
                    " #  # ",
                    " #  # ",
                    " #  # ",
                    " #### ",
                    "      "
            },

            {
                    "      ",
                    " #### ",
                    " #  # ",
                    " #  # ",
                    " #  # ",
                    "      "
            },

            {
                    "#   # ",
                    " # #  ",
                    "  #   ",
                    " # #  ",
                    "#   # ",
                    "      "
            },

            {
                    "#    #",
                    " #  # ",
                    "  ##  ",
                    "  ##  ",
                    " #  # ",
                    "#    #"
            },

            {
                    "      ",
                    " ###  ",
                    " # #  ",
                    " ###  ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    " ###  ",
                    " # #  ",
                    " ###  ",
                    "      "
            },

            {
                    "      ",
                    "  ### ",
                    "  # # ",
                    "  ### ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "  ### ",
                    "  # # ",
                    "  ### ",
                    "      "
            },

            {
                    "##### ",
                    "#   # ",
                    "#   # ",
                    "#   # ",
                    "##### ",
                    "      "
            },

            {
                    " #####",
                    " #   #",
                    " #   #",
                    " #   #",
                    " #####",
                    "      "
            },

            {
                    "      ",
                    "##### ",
                    "#   # ",
                    "#   # ",
                    "#   # ",
                    "##### "
            },

            {
                    "      ",
                    " #####",
                    " #   #",
                    " #   #",
                    " #   #",
                    " #####"
            },

            {
                    "##### ",
                    "##### ",
                    "## ## ",
                    "##### ",
                    "##### ",
                    "      "
            },

            {
                    "# # # ",
                    " # # #",
                    "# # # ",
                    " # # #",
                    "# # # ",
                    " # # #"
            },

            {
                    " # # #",
                    "# # # ",
                    " # # #",
                    "# # # ",
                    " # # #",
                    "# # # "
            },

            {
                    "#   # ",
                    "      ",
                    "      ",
                    "      ",
                    "#   # ",
                    "      "
            },

            {
                    "      ",
                    " # #  ",
                    "      ",
                    " # #  ",
                    "      ",
                    "      "
            },

            {
                    "##  ##",
                    "##  ##",
                    "      ",
                    "      ",
                    "##  ##",
                    "##  ##"
            },

            {
                    "#     ",
                    " #    ",
                    "  #   ",
                    "   #  ",
                    "    # ",
                    "     #"
            },

            {
                    "     #",
                    "    # ",
                    "   #  ",
                    "  #   ",
                    " #    ",
                    "#     "
            },

            {
                    "      ",
                    " ##   ",
                    " ##   ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "   ## ",
                    "   ## ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    " ##   ",
                    " ##   ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "   ## ",
                    "   ## ",
                    "      "
            },

            {
                    "      ",
                    " ###  ",
                    " # #  ",
                    " ###  ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "  ### ",
                    "  # # ",
                    "  ### ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    " ###  ",
                    " # #  ",
                    " ###  ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "  ### ",
                    "  # # ",
                    "  ### ",
                    "      "
            },

            {
                    "###   ",
                    "###   ",
                    "###   ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "   ###",
                    "   ###",
                    "   ###",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "###   ",
                    "###   ",
                    "###   "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "   ###",
                    "   ###",
                    "   ###"
            },

            {
                    "      ",
                    "   ## ",
                    "  #   ",
                    "   ## ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "  ##  ",
                    "    # ",
                    "  ##  ",
                    "      ",
                    "      "
            },

            {
                    "  #   ",
                    " #    ",
                    "#     ",
                    " #    ",
                    "  #   ",
                    "      "
            },

            {
                    "   #  ",
                    "  #   ",
                    " #    ",
                    "  #   ",
                    "   #  ",
                    "      "
            },

            {
                    "    # ",
                    "   #  ",
                    "  #   ",
                    "   #  ",
                    "    # ",
                    "      "
            },

            {
                    "     #",
                    "    # ",
                    "   #  ",
                    "    # ",
                    "     #",
                    "      "
            },

            {
                    "   #  ",
                    "    # ",
                    "     #",
                    "    # ",
                    "   #  ",
                    "      "
            },

            {
                    "  #   ",
                    "   #  ",
                    "    # ",
                    "   #  ",
                    "  #   ",
                    "      "
            },

            {
                    " #    ",
                    "  #   ",
                    "   #  ",
                    "  #   ",
                    " #    ",
                    "      "
            },

            {
                    "#     ",
                    " #    ",
                    "  #   ",
                    " #    ",
                    "#     ",
                    "      "
            },

            {
                    "  #   ",
                    " # #  ",
                    "#   # ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "  #   ",
                    " # #  ",
                    "#   # ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "  #   ",
                    " # #  ",
                    "#   # ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "  #   ",
                    " # #  ",
                    "#   # "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "#   # ",
                    " # #  ",
                    "  #   "
            },

            {
                    "      ",
                    "      ",
                    "#   # ",
                    " # #  ",
                    "  #   ",
                    "      "
            },

            {
                    "      ",
                    "#   # ",
                    " # #  ",
                    "  #   ",
                    "      ",
                    "      "
            },

            {
                    "#   # ",
                    " # #  ",
                    "  #   ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "  #   ",
                    " # #  ",
                    "#   # ",
                    " # #  ",
                    "  #   ",
                    "      "
            },

            {
                    "   #  ",
                    "  # # ",
                    " #   #",
                    "  # # ",
                    "   #  ",
                    "      "
            },

            {
                    "      ",
                    "  #   ",
                    " # #  ",
                    "#   # ",
                    " # #  ",
                    "  #   "
            },

            {
                    "      ",
                    "   #  ",
                    "  # # ",
                    " #   #",
                    "  # # ",
                    "   #  "
            },

            {
                    "##    ",
                    "#     ",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    " #    ",
                    "##    ",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "  ##  ",
                    "  #   ",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "   #  ",
                    "  ##  ",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "    ##",
                    "    # ",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "     #",
                    "    ##",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "##    ",
                    "#     "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    " #    ",
                    "##    "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "  ##  ",
                    "  #   "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "   #  ",
                    "  ##  "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "    ##",
                    "    # "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "     #",
                    "    ##"
            },

            {
                    "######",
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "######",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "######",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "######",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "######",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "######"
            },



            {
                    "######",
                    "######",
                    "      ",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "######",
                    "######",
                    "      ",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "######",
                    "######",
                    "      ",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "######",
                    "######",
                    "      "
            },

            {
                    "      ",
                    "      ",
                    "      ",
                    "      ",
                    "######",
                    "######"
            },

    };


    private static class SelectedPattern {
        private final int i1;
        private final int i2;
        private final int i3;
        private final int i4;

        public SelectedPattern(int i1, int i2, int i3, int i4) {
            this.i1 = i1;
            this.i2 = i2;
            this.i3 = i3;
            this.i4 = i4;
        }

        public int getI1() {
            return i1;
        }

        public int getI2() {
            return i2;
        }

        public int getI3() {
            return i3;
        }

        public int getI4() {
            return i4;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SelectedPattern that = (SelectedPattern) o;
            return i1 == that.i1 &&
                    i2 == that.i2 &&
                    i3 == that.i3 &&
                    i4 == that.i4;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i1, i2, i3, i4);
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
        for (int y = 0 ; y < pattern.length ; y++) {
            for (int x = 0 ; x < pattern[y].length() ; x++) {
                char p = pattern[y].charAt(x);
                if (p == '#') {
                    builder.set(x, y, c);
                }
            }
        }
    }

    private static DimletPattern buildPattern(SelectedPattern selectedPattern) {
        PatternBuilder builder = new PatternBuilder();
        int i1 = selectedPattern.getI1();
        applyPattern(builder, PATTERNS[i1], PatternBuilder.SHARD);
        int i2 = selectedPattern.getI2();
        if (i2 != -1) {
            applyPattern(builder, PATTERNS[i2], PatternBuilder.LEV0);
        }
        int i3 = selectedPattern.getI3();
        if (i3 != -1) {
            applyPattern(builder, PATTERNS[i3], PatternBuilder.LEV1);
        }
        int i4 = selectedPattern.getI4();
        if (i4 != -1) {
            applyPattern(builder, PATTERNS[i4], PatternBuilder.LEV2);
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
        return random.nextInt(PATTERNS.length);
    }

}
