package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

import static mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder.*;

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

    public static ItemStack getPatternItem(char p) {
        switch (p) {
            case EMPTY: return ItemStack.EMPTY;
            case SHARD: return new ItemStack(Registration.DIMENSIONAL_SHARD);
            case LEV0: return new ItemStack(DimletModule.COMMON_ESSENCE.get());
            case LEV1: return new ItemStack(DimletModule.RARE_ESSENCE.get());
            case LEV2: return new ItemStack(DimletModule.LEGENDARY_ESSENCE.get());
        }
        return ItemStack.EMPTY;
    }

    public static char getPatternChar(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        Item item = stack.getItem();
        if (item == Registration.DIMENSIONAL_SHARD) {
            return SHARD;
        }
        if (item == DimletModule.COMMON_ESSENCE.get()) {
            return LEV0;
        }
        if (item == DimletModule.RARE_ESSENCE.get()) {
            return LEV1;
        }
        if (item == DimletModule.LEGENDARY_ESSENCE.get()) {
            return LEV2;
        }
        return EMPTY;
    }

    public DimletPattern getPattern(World world, DimletKey key) {
        resolve(world);
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        return patterns.get(Pair.of(key.getType(), settings.getRarity()));
    }

    public DimletPattern getPattern(World world, DimletType type, DimletRarity rarity) {
        resolve(world);
        return patterns.get(Pair.of(type, rarity));
    }

}
