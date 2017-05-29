package mcjty.rftoolsdim.dimensions.world.terrain.lost;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class GenInfo {
    private final List<BlockPos> spawnerType1 = new ArrayList<>();
    private final List<BlockPos> spawnerType2 = new ArrayList<>();
    private final List<BlockPos> chest = new ArrayList<>();
    private final List<BlockPos> randomFeatures = new ArrayList<>();

    public void addSpawnerType1(BlockPos p) {
        spawnerType1.add(p);
    }

    public void addSpawnerType2(BlockPos p) {
        spawnerType2.add(p);
    }

    public void addChest(BlockPos p) {
        chest.add(p);
    }

    public void addRandomFeatures(BlockPos p) {
        randomFeatures.add(p);
    }

    public List<BlockPos> getSpawnerType1() {
        return spawnerType1;
    }

    public List<BlockPos> getSpawnerType2() {
        return spawnerType2;
    }

    public List<BlockPos> getChest() {
        return chest;
    }

    public List<BlockPos> getRandomFeatures() {
        return randomFeatures;
    }
}
