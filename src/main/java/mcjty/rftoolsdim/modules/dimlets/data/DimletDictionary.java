package mcjty.rftoolsdim.modules.dimlets.data;

import mcjty.rftoolsdim.modules.knowledge.data.DimletPattern;
import mcjty.rftoolsdim.modules.knowledge.data.KnowledgeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class DimletDictionary {

    private static DimletDictionary INSTANCE = new DimletDictionary();

    private Map<DimletKey, DimletSettings> dimlets = new HashMap<>();
    private Map<DimletRarity, List<DimletKey>> dimletsByRarity = new HashMap<>();

    public static DimletDictionary get() {
        return INSTANCE;
    }

    private void register(DimletKey key, DimletSettings settings) {
        dimlets.put(key, settings);
    }

    public Set<DimletKey> getDimlets() {
        return dimlets.keySet();
    }

    public DimletSettings getSettings(DimletKey key) {
        return dimlets.get(key);
    }

    public DimletKey tryCraft(World world, DimletType type, ItemStack memoryPart, ItemStack energyPart, ItemStack essence,
                              DimletPattern pattern) {
        for (Map.Entry<DimletKey, DimletSettings> entry : dimlets.entrySet()) {
            DimletKey key = entry.getKey();
            if (type.equals(key.getType())) {
                if (memoryPart.isItemEqual(DimletTools.getNeededMemoryPart(key))) {
                    if (energyPart.isItemEqual(DimletTools.getNeededEnergyPart(key))) {
                        ItemStack neededEssence = DimletTools.getNeededEssence(key, entry.getValue());
                        if (DimletTools.isFullEssence(essence, neededEssence, key.getKey())) {
                            DimletPattern neededPattern = KnowledgeManager.get().getPattern(world, key);
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
    public DimletKey getRandomDimlet(DimletRarity rarity, Random random) {
        if (!dimletsByRarity.containsKey(rarity)) {
            List<DimletKey> dimletKeys = new ArrayList<>();

            dimletsByRarity.put(rarity, dimletKeys);
        }
        List<DimletKey> keys = dimletsByRarity.get(rarity);
        if (keys.isEmpty()) {
            return null;
        }
        return keys.get(random.nextInt(keys.size()));
    }

    public void readPackage(String filename) {
        DimletPackages.readPackage(filename, this::register);
    }

}
