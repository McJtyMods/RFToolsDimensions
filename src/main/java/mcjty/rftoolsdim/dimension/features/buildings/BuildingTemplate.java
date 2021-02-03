package mcjty.rftoolsdim.dimension.features.buildings;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BuildingTemplate {

    private final Map<Character, BlockState> palette = new HashMap<>();
    private final Map<Character, BiConsumer<ISeedReader, BlockPos>> paletteSpecial = new HashMap<>();

    private final List<Slice> slices = new ArrayList<>();

    public void addPalette(Character key, BlockState state) {
        palette.put(key, state);
    }

    public void addPalette(Character key, BiConsumer<ISeedReader, BlockPos> consumer) {
        paletteSpecial.put(key, consumer);
    }

    public void generate(ISeedReader reader, BlockPos pos) {
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        int y = pos.getY();
        for (Slice slice : slices) {
            int z = pos.getZ();
            for (String row : slice.rows) {
                for (int x = 0 ; x < row.length() ; x++) {
                    mpos.setPos(pos.getX() + x, y, z);
                    char key = row.charAt(x);
                    if (paletteSpecial.containsKey(key)) {
                        paletteSpecial.get(key).accept(reader, mpos);
                    } else {
                        reader.setBlockState(mpos, palette.get(key), 0);
                    }
                }
                z++;
            }
            y++;
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
