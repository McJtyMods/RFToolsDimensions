package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class DimletDictionary {

    private static final DimletDictionary INSTANCE = new DimletDictionary();

    private final Map<DimletKey, DimletSettings> dimlets = new HashMap<>();
    private final Map<DimletRarity, List<DimletKey>> dimletsByRarity = new HashMap<>(); // Only dimlets with worldgen == true
    private final Map<Pair<DimletType, DimletRarity>, List<DimletKey>> dimletsByRarityAndType = new HashMap<>();

    public static DimletDictionary get() {
        return INSTANCE;
    }

    public void reset() {
        dimlets.clear();
        dimletsByRarity.clear();
        dimletsByRarityAndType.clear();
    }

    public boolean register(RegistryAccess access, DimletKey key, DimletSettings settings) {
        if (DimletTools.isValidDimlet(access, key)) {
            dimlets.put(key, settings);
            return true;
        }
        return false;
    }

    public Set<DimletKey> getDimlets() {
        return dimlets.keySet();
    }

    public DimletSettings getSettings(DimletKey key) {
        return dimlets.get(key);
    }

    public DimletKey tryCraft(Level world, DimletType type, ItemStack memoryPart, ItemStack energyPart, ItemStack essence,
                              DimletPattern pattern) {
        for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
            DimletKey key = entry.getKey();
            if (type.equals(key.type())) {
                if (ItemStack.isSameItem(memoryPart, DimletTools.getNeededMemoryPart(key))) {
                    if (ItemStack.isSameItem(energyPart, DimletTools.getNeededEnergyPart(key))) {
                        ItemStack neededEssence = DimletTools.getNeededEssence(key, entry.getValue());
                        if (DimletTools.isFullEssence(essence, neededEssence, key.key())) {
                            ServerLevel overworld = LevelTools.getOverworld(world);
                            DimletPattern neededPattern = KnowledgeManager.get().getPattern(overworld, overworld.getSeed(), key);
                            if (Objects.equals(neededPattern, pattern)) {
                                return key;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    /// This only returns dimlets with worldgen == rue
    public DimletKey getRandomDimlet(DimletRarity rarity, RandomSource random) {
        List<DimletKey> keys = getDimletsByRarity(rarity);
        if (keys.isEmpty()) {
            return null;
        }
        if (keys.size() == 1) {
            return keys.get(0);
        } else {
            // There are multiple choices, try to give less chance to block dimlets. Do a few attempts to find something else
            DimletKey dimletKey = null;
            for (int i = 0 ; i < Math.max(2, Math.min(10, keys.size() / 20)) ; i++) {
                dimletKey = keys.get(random.nextInt(keys.size()));
                if (dimletKey.type() != DimletType.BLOCK) {
                    return dimletKey;
                }
            }
            return dimletKey;
        }
    }


    @Nullable
    // This only returns dimlet with worldgen == true
    public DimletKey getRandomDimlet(DimletType type, Random random) {
        DimletKey key = getRandomDimletInternal(type, DimletRarity.COMMON, random);
        if (key == null) {
            key = getRandomDimletInternal(type, DimletRarity.UNCOMMON, random);
            if (key == null) {
                key = getRandomDimletInternal(type, DimletRarity.RARE, random);
                if (key == null) {
                    key = getRandomDimletInternal(type, DimletRarity.LEGENDARY, random);
                }
            }
        }
        return key;
    }

    private DimletKey getRandomDimletInternal(DimletType type, DimletRarity startAt, Random random) {
        DimletRarity rarity = startAt;
        if (random.nextFloat() < .1f) {
            rarity = DimletRarity.UNCOMMON;
            if (random.nextFloat() < .1f) {
                rarity = DimletRarity.RARE;
                if (random.nextFloat() < .1f) {
                    rarity = DimletRarity.LEGENDARY;
                }
            }
        }
        while (true) {
            List<DimletKey> keys = getDimletsByRarityAndType(type, rarity);
            if (!keys.isEmpty()) {
                if (keys.size() == 1) {
                    return keys.get(0);
                } else {
                    return keys.get(random.nextInt(keys.size()));
                }
            }
            switch (rarity) {
                case COMMON:
                    return null;
                case UNCOMMON:
                    rarity = DimletRarity.COMMON;
                    break;
                case RARE:
                    rarity = DimletRarity.UNCOMMON;
                    break;
                case LEGENDARY:
                    rarity = DimletRarity.RARE;
                    break;
            }
        }
    }

    // This returns only dimlets with worldgen == true
    private List<DimletKey> getDimletsByRarity(DimletRarity rarity) {
        if (!dimletsByRarity.containsKey(rarity)) {
            List<DimletKey> dimletKeys = new ArrayList<>();
            for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
                if (entry.getValue().getRarity() == rarity) {
                    DimletKey key = entry.getKey();
                    if (getSettings(key).isWorldgen()) {
                        dimletKeys.add(key);
                    }
                }
            }
            dimletsByRarity.put(rarity, dimletKeys);
        }
        return dimletsByRarity.get(rarity);
    }

    private List<DimletKey> getDimletsByRarityAndType(DimletType type, DimletRarity rarity) {
        Pair<DimletType, DimletRarity> pair = Pair.of(type, rarity);
        if (!dimletsByRarityAndType.containsKey(pair)) {
            List<DimletKey> dimletKeys = new ArrayList<>();
            List<DimletKey> dimletsByRarity = getDimletsByRarity(rarity);
            for (DimletKey key : dimletsByRarity) {
                if (key.type() == type) {
                    dimletKeys.add(key);
                }
            }
            dimletsByRarityAndType.put(pair, dimletKeys);
        }
        return dimletsByRarityAndType.get(pair);
    }


    public void readPackage(RegistryAccess access, String filename) {
        DimletPackages.readPackage(filename, (key, settings) -> register(access, key, settings));
    }

    @Nullable
    public DimletKey getBlockDimlet(String block) {
        for (var entry : dimlets.entrySet()) {
            DimletKey key = entry.getKey();
            if (key.type().equals(DimletType.BLOCK)) {
                if (Objects.equals(key.key(), block)) {
                    return key;
                }
            }
        }
        return null;
    }

    @Nullable
    public DimletKey getStructureDimlet(String structure) {
        for (var entry : dimlets.entrySet()) {
            DimletKey key = entry.getKey();
            if (key.type().equals(DimletType.STRUCTURE)) {
                if (Objects.equals(key.key(), structure)) {
                    return key;
                }
            }
        }
        return null;
    }

    @Nullable
    public DimletKey getFluidDimlet(String fluid) {
        for (var entry : dimlets.entrySet()) {
            DimletKey key = entry.getKey();
            if (key.type().equals(DimletType.FLUID)) {
                if (Objects.equals(key.key(), fluid)) {
                    return key;
                }
            }
        }
        return null;
    }

    @Nullable
    public DimletKey getBiomeDimlet(String biomeId) {
        for (var entry : dimlets.entrySet()) {
            DimletKey key = entry.getKey();
            if (key.type().equals(DimletType.BIOME)) {
                if (Objects.equals(key.key(), biomeId)) {
                    return key;
                }
            }
        }
        return null;
    }
}
