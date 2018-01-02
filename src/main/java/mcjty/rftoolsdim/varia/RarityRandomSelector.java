package mcjty.rftoolsdim.varia;

import java.util.*;

/**
 * A class that can return random items based on rarity.
 */
public class RarityRandomSelector<K,E> {
    private boolean dirty = true;
    private boolean createdDistribution = false;

    private final boolean rarityScalesBySize;

    // A map associating every key with the chance that items of this key should be selected.
    private final Map<K,Float> keys = new HashMap<>();
    private float minChance = Float.MAX_VALUE;    // Used for calculation distribution with bonus.
    private float maxChance = Float.MIN_VALUE;

    private final Distribution defaultDistribution = new Distribution();

    // All items for every key.
    private final Map<K,List<E>> items = new HashMap<>();

    public RarityRandomSelector(boolean rarityScalesBySize) {
        this.rarityScalesBySize = rarityScalesBySize;
    }

    /**
     * Add a new rarity key. All items associated with this key should
     * have 'chance' chance of being selected.
     * @throws IllegalStateException If you call this method after calling createDistribution
     */
    public void addRarity(K key, float chance) {
        if(createdDistribution) {
            throw new IllegalStateException("Can't modify a RarityRandomSelector after calling createDistribution");
        }
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
     * @throws IllegalStateException If you call this method after calling createDistribution
     */
    public void addItem(K key, E item) {
        if(createdDistribution) {
            throw new IllegalStateException("Can't modify a RarityRandomSelector after calling createDistribution");
        }
        items.get(key).add(item);
        dirty = true;
    }

    private void setupDistribution(Distribution distribution, float bonus) {
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
    public Distribution createDistribution(float bonus) {
        createdDistribution = true;
        Distribution distribution = new Distribution();
        setupDistribution(distribution, bonus);
        return distribution;
    }

    /**
     * Return a random element.
     */
    public E select(Random random) {
        if (dirty) {
            dirty = false;
            setupDistribution(defaultDistribution, 0.0f);
        }
        return defaultDistribution.select(random);
    }

    public class Distribution {
        // A map associating every key with the chance that this key in total must be selected.
        private final NavigableMap<Float, K> keysChance = new TreeMap<>();
        private float totalChance = 0.0f;

        private void reset() {
            keysChance.clear();
            totalChance = 0.0f;
        }

        private void addKey(K key, float chance) {
            totalChance += chance;
            keysChance.put(totalChance, key);
        }

        public E select(Random random) {
            K key = keysChance.ceilingEntry(random.nextFloat() * totalChance).getValue();
            List<E> list = items.get(key);
            if (list == null) {
                return null;
            }
            return list.get(random.nextInt(list.size()));
        }
    }
}
