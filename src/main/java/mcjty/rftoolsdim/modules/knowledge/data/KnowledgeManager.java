package mcjty.rftoolsdim.modules.knowledge.data;

import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.TagTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsdim.RFToolsDim;
import mcjty.rftoolsdim.dimension.TimeType;
import mcjty.rftoolsdim.dimension.additional.SkyDimletType;
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
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
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
        return switch (p) {
            case EMPTY -> ItemStack.EMPTY;
            case SHARD -> new ItemStack(Registration.DIMENSIONAL_SHARD.get());
            case LEV0 -> new ItemStack(DimletModule.COMMON_ESSENCE.get());
            case LEV1 -> new ItemStack(DimletModule.RARE_ESSENCE.get());
            case LEV2 -> new ItemStack(DimletModule.LEGENDARY_ESSENCE.get());
            default -> ItemStack.EMPTY;
        };
    }

    public static char getPatternChar(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        Item item = stack.getItem();
        if (item == Registration.DIMENSIONAL_SHARD.get()) {
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
    private String getKnowledgeSetReason(CommonLevelAccessor level, DimletKey key) {
        return switch (key.type()) {
            case TERRAIN -> null;
            case ATTRIBUTE -> null;
            case BIOME_CONTROLLER -> null;
            case BIOME_CATEGORY -> null;
            case BIOME -> getReasonBiome(key);
            case STRUCTURE -> getReasonStructure(level, key);
            case FEATURE -> null;
            case SKY -> null;
            case TIME -> null;
            case DIGIT -> null;
            case ADMIN -> null;
            case BLOCK -> getReasonBlock(key);
            case TAG -> new ResourceLocation(key.key()).getPath();
            case FLUID -> new ResourceLocation(key.key()).getNamespace();
        };
    }

    @Nullable
    private String getReasonBiome(DimletKey key) {
        ResourceLocation rl = new ResourceLocation(key.key());
        Biome biome = BuiltinRegistries.BIOME.get(rl);
        if (biome != null) {
            return getMostImportantIsTag(rl);
        }
        return null;
    }

    @Nullable
    private String getMostImportantIsTag(ResourceLocation rl) {
        return BuiltinRegistries.BIOME.getHolderOrThrow(ResourceKey.create(Registry.BIOME_REGISTRY, rl))
                .tags()
                .filter(t -> t.location().getPath().startsWith("is_"))
                .sorted()
                .findFirst()
                .map(k -> k.location().toString())
                .orElse(null);
    }

    @Nullable
    private String getReasonStructure(CommonLevelAccessor level, DimletKey key) {
        Structure structure = BuiltinRegistries.STRUCTURES.get(new ResourceLocation(key.key()));
        if (structure != null) {
            return Tools.getId(level, structure).getPath();
        }
        return null;
    }

    @Nullable
    private String getReasonBlock(DimletKey key) {
        TagKey<Block> tagId = getMostCommonTagForBlock(key);
        if (tagId != null) {
            return tagId.location().getPath();
        }
        return null;
    }

    private KnowledgeSet getKnowledgeSet(CommonLevelAccessor level, DimletKey key) {
        return switch (key.type()) {
            case TERRAIN -> TerrainType.byName(key.key()).getSet();
            case ATTRIBUTE -> AttributeType.byName(key.key()).getSet();
            case BIOME_CONTROLLER -> BiomeControllerType.byName(key.key()).getSet();
            case BIOME_CATEGORY -> getBiomeCategoryKnowledgeSet(key);
            case BIOME -> getBiomeKnowledgeSet(key);
            case STRUCTURE -> getStructureKnowledgeSet(level, key);
            case FEATURE -> FeatureType.byName(key.key()).getSet();
            case TIME -> TimeType.byName(key.key()).getSet();
            case BLOCK -> getBlockKnowledgeSet(key);
            case TAG -> getTagKnowledgeSet(key);
            case SKY -> getSkyKnowledgeSet(key);
            case FLUID -> getFluidKnowledgeSet(key);
            case DIGIT -> KnowledgeSet.SET1;
            case ADMIN -> KnowledgeSet.SET1;
        };
    }

    private KnowledgeSet getSkyKnowledgeSet(DimletKey key) {
        SkyDimletType skyType = SkyDimletType.byName(key.key());
        if (skyType == null) {
            return KnowledgeSet.SET1;
        }
        return skyType.getKnowledgeSet();
    }

    private KnowledgeSet getFluidKnowledgeSet(DimletKey key) {
        int i = Math.abs(new ResourceLocation(key.key()).getNamespace().hashCode());
        return KnowledgeSet.values()[i%(KnowledgeSet.values().length)];
    }

    private KnowledgeSet getBiomeCategoryKnowledgeSet(DimletKey key) {
        // @todo 1.19 is this a good way?
        return KnowledgeSet.values()[key.key().hashCode() % KnowledgeSet.values().length];
//        Biome.BiomeCategory category = Biome.BiomeCategory.byName(key.key());
//        return KnowledgeSet.values()[category.ordinal() % KnowledgeSet.values().length];
    }

    /// Create a knowledge set based on the most important tag for a given block
    private KnowledgeSet getTagKnowledgeSet(DimletKey key) {
        ResourceLocation tagId = new ResourceLocation(key.key());
        int i = Math.abs(tagId.hashCode());
        return KnowledgeSet.values()[i%(KnowledgeSet.values().length)];
    }

    /// Create a knowledge set based on the most important tag for a given block
    private KnowledgeSet getBlockKnowledgeSet(DimletKey key) {
        TagKey<Block> tagId = getMostCommonTagForBlock(key);
        if (tagId == null) {
            return KnowledgeSet.SET1;
        }

        int i = Math.abs(tagId.hashCode());
        return KnowledgeSet.values()[i%(KnowledgeSet.values().length)];
    }

    private TagKey<Block> getMostCommonTagForBlock(DimletKey key) {
        TagKey<Block> mostImportant = null;
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key.key()));
        if (block == null) {
            RFToolsDim.setup.getLogger().error("Block '" + key.key() + "' is missing!");
        } else {
            Collection<TagKey<Block>> tags = TagTools.getTags(block);
            int maxAmount = -1;
            for (TagKey<Block> tag : tags) {
                List<Block> elements = new ArrayList<>();
                TagTools.getBlocksForTag(tag).forEach(h -> elements.add(h.value()));
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

    private KnowledgeSet getStructureKnowledgeSet(CommonLevelAccessor level, DimletKey key) {
        Structure structure = BuiltinRegistries.STRUCTURES.get(new ResourceLocation(key.key()));
        if (structure == null) {
            if (key.key().equals("default") || key.key().equals("none")) {
                return KnowledgeSet.SET1;
            }
            RFToolsDim.setup.getLogger().error("Structure '" + key.key() + "' is missing!");
            return KnowledgeSet.SET2;
        }
        // @todo is this good?
        return KnowledgeSet.values()[(Math.abs(Tools.getId(level, structure).hashCode())) % KnowledgeSet.values().length];
    }

    /// Create a knowledge set based on the category of a biome
    private KnowledgeSet getBiomeKnowledgeSet(DimletKey key) {
        ResourceLocation rl = new ResourceLocation(key.key());
        Biome biome = BuiltinRegistries.BIOME.get(rl);
        if (biome == null) {
            RFToolsDim.setup.getLogger().error("Biome '" + key.key() + "' is missing!");
            return KnowledgeSet.SET1;
        }
        // @todo 1.19 is this right?
        return KnowledgeSet.values()[key.key().hashCode() % KnowledgeSet.values().length];
//        Biome.BiomeCategory category = Biome.getBiomeCategory(Holder.direct(biome));
//        return KnowledgeSet.values()[category.ordinal() % KnowledgeSet.values().length];
    }

    @Nullable
    public KnowledgeKey getKnowledgeKey(CommonLevelAccessor level, long seed, DimletKey key) {
        resolve(seed);
        DimletSettings settings = DimletDictionary.get().getSettings(key);
        if (settings == null) {
            return null;
        }
        KnowledgeSet set = getKnowledgeSet(level, key);
        return new KnowledgeKey(key.type(), settings.getRarity(), set);
    }

    @Nullable
    public DimletPattern getPattern(CommonLevelAccessor level, long seed, DimletKey key) {
        KnowledgeKey kkey = getKnowledgeKey(level, seed, key);
        if (kkey == null) {
            return null;
        }
        return patterns.get(kkey);
    }

    public String getReason(Level world, KnowledgeKey key) {
        getKnownPatterns(world, key.rarity());   // Make sure to refresh known patterns (and keyReasons)
        return keyReasons.get(key);
    }

    public List<KnowledgeKey> getKnownPatterns(Level world, DimletRarity rarity) {
        if (!knownPatterns.containsKey(rarity)) {
            List<KnowledgeKey> set = new ArrayList<>();
            for (DimletKey key : DimletDictionary.get().getDimlets()) {
                DimletSettings settings = DimletDictionary.get().getSettings(key);
                if (settings != null && Objects.equals(settings.getRarity(), rarity)) {
                    ServerLevel overworld = LevelTools.getOverworld(world);
                    KnowledgeKey kkey = getKnowledgeKey(overworld, overworld.getSeed(), key);
                    if (kkey != null) {
                        set.add(kkey);
                        String reason = getKnowledgeSetReason(world, key);
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
