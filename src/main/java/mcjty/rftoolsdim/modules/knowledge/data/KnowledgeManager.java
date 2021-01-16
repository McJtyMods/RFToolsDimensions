package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class KnowledgeManager {

    private long worldSeed = -1;    // To validate that the patterns are still ok
    private Map<Pair<DimletType, DimletRarity>, DimletPattern> patterns = null;

    private static final KnowledgeManager INSTANCE = new KnowledgeManager();

    public static KnowledgeManager get() {
        return INSTANCE;
    }

    private void resolve(World world) {
        long seed = ((ServerWorld) world).getSeed();
        if (seed != worldSeed || patterns == null) {
            worldSeed = seed;
            patterns = RandomPatternCreator.createRandomPatterns(seed);
        }
    }

    public DimletPattern getPattern(World world, DimletType type, DimletRarity rarity) {
        resolve(world);
        return patterns.get(Pair.of(type, rarity));
    }

}
