package mcjty.rftoolsdim.varia;

import java.util.*;

/**
 * A class that can return random items based on rarity.
 */
public class RarityRandomSelector<K,E> {
    private boolean dirty = true;

    private final boolean rarityScalesBySize;

    // A map associating every key with the chance that items of this key should be selected.
    private final Map<K,Float> keys = new HashMap<>();
    private float minChance = Float.MAX_VALUE;    // Used for calculation distribution with bonus.
    private float maxChance = Float.MIN_VALUE;

    private final Distribution<K> defaultDistribution = new Distribution<>();

    // All items for every key.
    private final Map<K,List<E>> items = new HashMap<>();

    public RarityRandomSelector(boolean rarityScalesBySize) {
        this.rarityScalesBySize = rarityScalesBySize;
    }

    public void clear() {
        dirty = true;
        keys.clear();
        minChance = Float.MAX_VALUE;
        maxChance = Float.MIN_VALUE;
        defaultDistribution.reset();
        items.clear();
    }

    /**
     * Add a new rarity key. All items associated with this key should
     * have 'chance' chance of being selected.
     */
    public void addRarity(K key, float chance) {
        keys.put(key, chance);
        items.put(key, new ArrayList<E>());
        if (chance < minChance) {
            minChance = chance;
        }
        if (chance > maxChance) {
            maxChance = chance;
        }
        dirty = true;
    }

    /**
     * Add a new item.
     */
    public void addItem(K key, E item) {
        items.get(key).add(item);
        dirty = true;
    }

    private void distribute() {
        if (dirty) {
            dirty = false;
            setupDistribution(defaultDistribution, 0.0f);
        }
    }

    private void setupDistribution(Distribution<K> distribution, float bonus) {
        float add = bonus * (maxChance - minChance);
        distribution.reset();
        keys.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).forEachOrdered(entry -> {
            K key = entry.getKey();
            int length = items.get(key).size();
            if (length > 0) {
                float chance = entry.getValue() + add;
                distribution.addKey(key, (rarityScalesBySize ? chance * length : chance));
            }
        });
    }

    /**
     * Create a new distribution. If the bonus is equal to 0.0f then this distribution
     * will be equal to the default one. With a bonus equal to 1.1f you will basically
     * make the chance of the rarest elements equal to half the chance of the most common
     * elements. Very large values will make the rarest elements almost as common as
     * the most common elements.
     */
    public Distribution<K> createDistribution(float bonus) {
        Distribution<K> distribution = new Distribution<>();
        setupDistribution(distribution, bonus);
        return distribution;
    }

    /**
     * Return a random element given a distribution.
     */
    public E select(Distribution<K> distribution, Random random) {
        distribute();
        NavigableMap<Float, K> keysChance = distribution.getKeysChance();
        if(keysChance.isEmpty()) return null;
        K key = keysChance.ceilingEntry(random.nextFloat() * distribution.getTotalChance()).getValue();
        if(key == null) key = keysChance.lastEntry().getValue();
        List<E> list = items.get(key);
        if (list == null) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Return a random element.
     */
    public E select(Random random) {
        return select(defaultDistribution, random);
    }

    public static class Distribution<K> {
        // A map associating every key with the chance that this key in total must be selected.
        private final NavigableMap<Float, K> keysChance = new TreeMap<>();
        private float totalChance = 0.0f;

        public NavigableMap<Float, K> getKeysChance() {
            return keysChance;
        }

        public float getTotalChance() {
            return totalChance;
        }

        public void reset() {
            keysChance.clear();
            totalChance = 0.0f;
        }

        public void addKey(K key, float chance) {
            totalChance += chance;
            keysChance.put(totalChance, key);
        }
    }
}
