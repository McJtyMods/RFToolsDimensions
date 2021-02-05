package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.biomes.BiomeControllerType;
import mcjty.rftoolsdim.dimension.features.FeatureType;
import mcjty.rftoolsdim.dimension.terraintypes.AttributeType;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import mcjty.rftoolsdim.modules.dimlets.DimletModule;
import mcjty.rftoolsdim.modules.dimlets.data.DimletDictionary;
import mcjty.rftoolsdim.modules.dimlets.data.DimletKey;
import mcjty.rftoolsdim.modules.dimlets.data.DimletRarity;
import mcjty.rftoolsdim.modules.dimlets.data.DimletSettings;
import mcjty.rftoolsdim.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

import static mcjty.rftoolsdim.modules.knowledge.data.PatternBuilder.*;

public class KnowledgeManager {

    private long worldSeed = -1;    // To validate that the patterns are still ok

    // All patterns by knowledge key
    private Map<KnowledgeKey, DimletPattern> patterns = null;
    // All patterns that are actually used by dimlets
    private final Map<DimletRarity, List<KnowledgeKey>> knownPatterns = new HashMap<>();
    // All patterns with their corresponding reason (if any)
    private final Map<KnowledgeKey, String> keyReasons = new HashMap<>();

    private static final KnowledgeManager INSTANCE = new KnowledgeManager();

    private final CommonTags commonTags = new CommonTags();

    public static KnowledgeManager get() {
        return INSTANCE;
    }

    public void clear() {
        commonTags.clear();
        keyReasons.clear();
        knownPatterns.clear();
        patterns = null;
        worldSeed = -1;
    }

    private void resolve(long seed) {
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

    @Nullable
    private String getKnowledgeSetReason(DimletKey key) {
        switch (key.getType()) {
            case TERRAIN:
                return null;
            case ATTRIBUTE:
                return null;
            case BIOME_CONTROLLER:
                return null;
            case BIOME:
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(key.getKey()));
                if (biome != null) {
                    return biome.getCategory().getName() + " biomes";
                }
                return null;
            case FEATURE:
                return null;
            case TIME:
                return null;
            case SPECIAL:
                return null;
            case BLOCK:
                ResourceLocation tagId = getMostCommonTagForBlock(key);
                if (tagId != null) {
                    return tagId.getPath();
                }
                return null;
        }
        return null;
    }

    private KnowledgeSet getKnowledgeSet(DimletKey key) {
        switch (key.getType()) {
            case TERRAIN:
                return TerrainType.byName(key.getKey()).getSet();
            case ATTRIBUTE:
                return AttributeType.byName(key.getKey()).getSet();
            case BIOME_CONTROLLER:
                return BiomeControllerType.byName(key.getKey()).getSet();
            case BIOME:
                return getBiomeKnowledgeSet(key);
            case FEATURE:
                return FeatureType.byName(key.getKey()).getSet();
            case TIME:
                return TimeType.byName(key.getKey()).getSet();
            case BLOCK:
                return getBlockKnowledgeSet(key);
            case DIGIT:
                break;
            case SPECIAL:
                break;
        }

        return KnowledgeSet.SET1;
    }

    /// Create a knowledge set based on the most important tag for a given block
    private KnowledgeSet getBlockKnowledgeSet(DimletKey key) {
        ResourceLocation tagId = getMostCommonTagForBlock(key);
        if (tagId == null) {
            return KnowledgeSet.SET1;
        }

        int i = Math.abs(tagId.hashCode());
        return KnowledgeSet.values()[i%(KnowledgeSet.values().length)];
    }

    private ResourceLocation getMostCommonTagForBlock(DimletKey key) {
        ResourceLocation mostImportant = null;
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key.getKey()));
        if (block == null) {
            RFToolsDim.setup.getLogger().error("Block '" + key.getKey() + "' is missing!");
        } else {
            Set<ResourceLocation> tags = block.getTags();
            int maxAmount = -1;
            for (ResourceLocation tag : tags) {
                List<Block> elements = BlockTags.createOptional(tag).getAllElements();
                int size = elements.size();
                if (commonTags.isCommon(tag)) {
                    size += 10; // Extra bonus
                }
                if (size > maxAmount) {
                    mostImportant = tag;
                    maxAmount = size;
                }
            }
        }
        return mostImportant;
    }

    /// Create a knowledge set based on the category of a biome
    private KnowledgeSet getBiomeKnowledgeSet(DimletKey key) {
        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(key.getKey()));
        if (biome == null) {
            RFToolsDim.setup.getLogger().error("Biome '" + key.getKey() + "' is missing!");
            return KnowledgeSet.SET1;
        }
        return KnowledgeSet.values()[biome.getCategory().ordinal() % KnowledgeSet.values().length];
    }

    @Nullable
    public KnowledgeKey getKnowledgeKey(long seed, DimletKey key) {
        resolve(seed);
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        if (settings == null) {
            return null;
        }
        KnowledgeSet set = getKnowledgeSet(key);
        return new KnowledgeKey(key.getType(), settings.getRarity(), set);
    }

    @Nullable
    public DimletPattern getPattern(long seed, DimletKey key) {
        return patterns.get(getKnowledgeKey(seed, key));
    }

    public String getReason(World world, KnowledgeKey key) {
        getKnownPatterns(world, key.getRarity());   // Make sure to refresh known patterns (and keyReasons)
        return keyReasons.get(key);
    }

    public List<KnowledgeKey> getKnownPatterns(World world, DimletRarity rarity) {
        if (!knownPatterns.containsKey(rarity)) {
            List<KnowledgeKey> set = new ArrayList<>();
            for (DimletKey key : DimletDictionary.get().getDimlets()) {
                DimletSettings settings = DimletDictionary.get().getSettings(key);
                if (settings != null && Objects.equals(settings.getRarity(), rarity)) {
                    KnowledgeKey kkey = getKnowledgeKey(DimensionId.overworld().loadWorld(world).getSeed(), key);
                    if (kkey != null) {
                        set.add(kkey);
                        String reason = getKnowledgeSetReason(key);
                        if (reason != null) {
                            keyReasons.put(kkey, reason);
                        }
                    }
                }
            }
            RFToolsDim.setup.getLogger().info("Patterns for rarity " + rarity.name() + ": " + set.size());
            knownPatterns.put(rarity, set);
        }
        return knownPatterns.get(rarity);
    }
}
