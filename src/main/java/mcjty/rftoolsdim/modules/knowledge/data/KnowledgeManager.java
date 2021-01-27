package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.*;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

import static mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder.*;

public class KnowledgeManager {

    private long worldSeed = -1;    // To validate that the patterns are still ok

    // All patterns by knowledge key
    private Map<KnowledgeKey, DimletPattern> patterns = null;
    // All patterns that are actually used by dimlets
    private final Map<DimletRarity, List<KnowledgeKey>> knownPatterns = new HashMap<>();

    private static final KnowledgeManager INSTANCE = new KnowledgeManager();

    public static KnowledgeManager get() {
        return INSTANCE;
    }

    private void resolve(World world) {
        ServerWorld overworld = DimensionId.overworld().loadWorld(world);
        long seed = overworld.getSeed();
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

    private KnowledgeSet getKnowledgeSet(World world, DimletKey key) {
        ResourceLocation id = DimletTools.getResourceLocation(key);
        if (id == null) {
            return KnowledgeSet.SET1;
        } else {
            // Calculate a hash based on the modid
            int i = Math.abs(id.getNamespace().hashCode());
            return KnowledgeSet.values()[i%(KnowledgeSet.values().length)];
        }
    }

    @Nullable
    public KnowledgeKey getKnowledgeKey(World world, DimletKey key) {
        resolve(world);
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        if (settings == null) {
            return null;
        }
        KnowledgeSet set = getKnowledgeSet(world, key);
        return new KnowledgeKey(key.getType(), settings.getRarity(), set);
    }

    @Nullable
    public DimletPattern getPattern(World world, DimletKey key) {
        resolve(world);
        return patterns.get(getKnowledgeKey(world, key));
    }

    public DimletPattern getPattern(KnowledgeKey kkey) {
        return patterns.get(kkey);
    }

    public List<KnowledgeKey> getKnownPatterns(World world, DimletRarity rarity) {
        if (!knownPatterns.containsKey(rarity)) {
            List<KnowledgeKey> set = new ArrayList<>();
            for (DimletKey key : DimletDictionary.get().getDimlets()) {
                KnowledgeKey kkey = getKnowledgeKey(world, key);
                if (kkey != null) {
                    set.add(kkey);
                }
            }
            knownPatterns.put(rarity, set);
        }
        return knownPatterns.get(rarity);
    }
}
