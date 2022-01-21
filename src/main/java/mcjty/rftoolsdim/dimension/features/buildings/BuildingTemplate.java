package mcjty.rftoolsdim.dimension.features.buildings;

import mcjty.rftoolsdim.dimension.features.IFeature;
import mcjty.rftoolsdim.dimension.terraintypes.TerrainType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BuildingTemplate {

    private final Map<Character, BlockState> palette = new HashMap<>();
    private final Map<Character, BiConsumer<WorldGenLevel, BlockPos>> paletteSpecial = new HashMap<>();

    private final List<Slice> slices = new ArrayList<>();

    public void addPalette(Character key, BlockState state) {
        palette.put(key, state);
    }

    public void addPalette(Character key, BiConsumer<WorldGenLevel, BlockPos> consumer) {
        paletteSpecial.put(key, consumer);
    }

    public enum GenerateFlag {
        PLAIN,
        FILLDOWN,
        FILLDOWN_IFNOTVOID
    }

    public void generate(TerrainType type, WorldGenLevel reader, BlockPos pos, List<BlockState> states, GenerateFlag flag) {
        switch (flag) {
            case PLAIN:
                break;
            case FILLDOWN:
                fillDown(reader, pos, states);
                break;
            case FILLDOWN_IFNOTVOID:
                if (!type.isVoidLike()) {
                    fillDownIfNotVoid(reader, pos, states);
                }
                break;
        }

        int y = pos.getY();
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (Slice slice : slices) {
            int z = pos.getZ();
            for (String row : slice.rows) {
                for (int x = 0; x < row.length(); x++) {
                    mpos.set(pos.getX() + x, y, z);
                    char key = row.charAt(x);
                    if (paletteSpecial.containsKey(key)) {
                        paletteSpecial.get(key).accept(reader, mpos);
                    } else {
                        reader.setBlock(mpos, palette.get(key), 0);
                    }
                }
                z++;
            }
            y++;
        }
    }

    private void fillDown(WorldGenLevel reader, BlockPos pos, List<BlockState> states) {
        int y = pos.getY();
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        Slice slice = slices.get(0);
        int z = pos.getZ();
        for (String row : slice.rows) {
            for (int x = 0; x < row.length(); x++) {
                for (int yy = y - 1; yy > 1; yy--) {
                    mpos.set(pos.getX() + x, yy, z);
                    BlockState state = reader.getBlockState(mpos);
                    if (state.isAir()) {
                        BlockState blockState = IFeature.select(states, reader.getRandom());
                        reader.setBlock(mpos, blockState, 0);
                    } else {
                        break;
                    }
                }
            }
            z++;
        }
    }

    private void fillDownIfNotVoid(WorldGenLevel reader, BlockPos pos, List<BlockState> states) {
        int y = pos.getY();
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        Slice slice = slices.get(0);
        int z = pos.getZ();
        for (String row : slice.rows) {
            for (int x = 0; x < row.length(); x++) {
                boolean isVoid = true;
                for (int yy = y - 1; yy > reader.getMinBuildHeight()+1; yy--) {
                    mpos.set(pos.getX() + x, yy, z);
                    BlockState state = reader.getBlockState(mpos);
                    if (!state.isAir()) {
                        isVoid = false;
                        break;
                    }
                }
                if (!isVoid) {
                    for (int yy = y - 1; yy > reader.getMinBuildHeight()+1; yy--) {
                        mpos.set(pos.getX() + x, yy, z);
                        BlockState state = reader.getBlockState(mpos);
                        if (state.isAir()) {
                            BlockState blockState = IFeature.select(states, reader.getRandom());
                            reader.setBlock(mpos, blockState, 0);
                        } else {
                            break;
                        }
                    }
                }
            }
            z++;
        }
    }

    public Slice slice() {
        Slice slice = new Slice();
        slices.add(slice);
        return slice;
    }


    public static class Slice {
        private final List<String> rows = new ArrayList<>();

        public Slice row(String r) {
            rows.add(r);
            return this;
        }
    }

}
