package mcjty.rftoolsdim.dimensions.description;

import net.minecraft.entity.Entity;

public class MobDescriptor {
    private Class<? extends Entity> entityClass;
    private final int spawnChance;
    private final int minGroup;
    private final int maxGroup;
    private final int maxLoaded;

    public MobDescriptor(Class<? extends Entity> entityClass, int spawnChance, int minGroup, int maxGroup, int maxLoaded) {
        this.entityClass = entityClass;
        this.spawnChance = spawnChance;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.maxLoaded = maxLoaded;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public int getSpawnChance() {
        return spawnChance;
    }

    public int getMinGroup() {
        return minGroup;
    }

    public int getMaxGroup() {
        return maxGroup;
    }

    public int getMaxLoaded() {
        return maxLoaded;
    }
}
